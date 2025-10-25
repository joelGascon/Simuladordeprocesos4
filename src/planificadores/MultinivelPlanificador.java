/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package planificadores;

/**
 *
 * @author joelgascon
 */
import modelo.*;
import gestor.GestorColas;
import utils.ColaPersonalizada;
import java.util.ArrayList;
import java.util.List;

public class MultinivelPlanificador implements Planificador {
    private GestorColas gestorColas;
    private List<ColaPersonalizada<Proceso>> colasNiveles;
    private List<String> algoritmosNiveles;
    
    public MultinivelPlanificador(GestorColas gestorColas) {
        this.gestorColas = gestorColas;
        this.colasNiveles = new ArrayList<>();
        this.algoritmosNiveles = new ArrayList<>();
        
        // Configurar 3 niveles con diferentes algoritmos
        for (int i = 0; i < 3; i++) {
            colasNiveles.add(new ColaPersonalizada<>());
        }
        
        // Asignar algoritmos a cada nivel
        algoritmosNiveles.add("RR");    // Nivel 0: Round Robin
        algoritmosNiveles.add("RR");    // Nivel 1: Round Robin  
        algoritmosNiveles.add("FCFS");  // Nivel 2: FCFS
    }
    
    @Override
    public Proceso seleccionarSiguiente() {
        // Buscar desde el nivel más alto (0) al más bajo
        for (int i = 0; i < colasNiveles.size(); i++) {
            ColaPersonalizada<Proceso> cola = colasNiveles.get(i);
            if (!cola.estaVacia()) {
                Proceso proceso = seleccionarDeNivel(cola, i);
                
                if (proceso != null) {
                    persistencia.Logger.getInstance().logEvento(new Evento("PLANIFICADOR", 
                        "Multinivel selecciona: " + proceso.getNombre() + 
                        " (nivel: " + i + ", algoritmo: " + algoritmosNiveles.get(i) + ")", proceso));
                    return proceso;
                }
            }
        }
        return null;
    }
    
    private Proceso seleccionarDeNivel(ColaPersonalizada<Proceso> cola, int nivel) {
        String algoritmo = algoritmosNiveles.get(nivel);
        
        switch (algoritmo) {
            case "FCFS":
                // Primer proceso en la cola
                return cola.desencolar();
                
            case "RR":
                // Round Robin - toma el primero
                return cola.desencolar();
                
            case "SJF":
                // Shortest Job First - busca el más corto
                Proceso masCorto = null;
                int minInstrucciones = Integer.MAX_VALUE;
                
                // Buscar en toda la cola
                List<Proceso> procesos = cola.toList();
                for (Proceso p : procesos) {
                    if (p.getInstruccionesRestantes() < minInstrucciones) {
                        minInstrucciones = p.getInstruccionesRestantes();
                        masCorto = p;
                    }
                }
                
                if (masCorto != null) {
                    cola.eliminar(masCorto);
                }
                return masCorto;
                
            default:
                return cola.desencolar();
        }
    }
    
    @Override
    public void agregarProceso(Proceso proceso) {
        // Los nuevos procesos van al nivel más alto (0)
        proceso.setPrioridad(0);
        colasNiveles.get(0).encolar(proceso);
    }
    
    public void devolverProceso(Proceso proceso, boolean completo) {
        if (proceso.estaTerminado()) {
            return; // Proceso terminado
        }
        
        int nivelActual = proceso.getPrioridad();
        
        if (!completo && nivelActual < colasNiveles.size() - 1) {
            // No completó su ejecución, bajar de nivel
            proceso.setPrioridad(nivelActual + 1);
            colasNiveles.get(nivelActual + 1).encolar(proceso);
        } else {
            // Completó o está en el último nivel, regresar al mismo nivel
            colasNiveles.get(nivelActual).encolar(proceso);
        }
    }
    
    @Override
    public void ejecutarCiclo() {
        actualizarColas();
    }
    
    private void actualizarColas() {
        List<Proceso> procesosGestor = gestorColas.getColaListos();
        for (Proceso p : procesosGestor) {
            boolean encontrado = false;
            for (ColaPersonalizada<Proceso> cola : colasNiveles) {
                if (cola.contiene(p)) {
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                // Proceso nuevo, va al nivel más alto
                p.setPrioridad(0);
                colasNiveles.get(0).encolar(p);
            }
        }
    }
    
    @Override
    public String getNombre() {
        return "Multinivel";
    }
    
    @Override
    public String getDescripcion() {
        return "Colas Multinivel - Múltiples colas con diferentes algoritmos de planificación";
    }
    
    @Override
    public List<Proceso> getColaListos() {
        List<Proceso> todos = new ArrayList<>();
        for (ColaPersonalizada<Proceso> cola : colasNiveles) {
            todos.addAll(cola.toList());
        }
        return todos;
    }
    
    public List<String> getEstadoColas() {
        List<String> estados = new ArrayList<>();
        for (int i = 0; i < colasNiveles.size(); i++) {
            estados.add("Nivel " + i + " (" + algoritmosNiveles.get(i) + "): " + 
                       colasNiveles.get(i).tamano() + " procesos");
        }
        return estados;
    }
    
    public int getQuantumParaNivel(int nivel) {
        if (algoritmosNiveles.get(nivel).equals("RR")) {
            return 4; // Quantum fijo para Round Robin
        }
        return 0; // No aplica para FCFS o SJF
    }
}