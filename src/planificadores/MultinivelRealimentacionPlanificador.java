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

public class MultinivelRealimentacionPlanificador implements Planificador {
    private GestorColas gestorColas;
    private List<ColaPersonalizada<Proceso>> colasNiveles;
    private List<Integer> quantums;
    private int contadorQuantum;
    private Proceso procesoEjecutando;
    private int nivelActual;
    
    public MultinivelRealimentacionPlanificador(GestorColas gestorColas) {
        this.gestorColas = gestorColas;
        this.colasNiveles = new ArrayList<>();
        this.quantums = new ArrayList<>();
        this.contadorQuantum = 0;
        this.procesoEjecutando = null;
        this.nivelActual = 0;
        
        // Configurar 4 niveles con quantums decrecientes
        for (int i = 0; i < 4; i++) {
            colasNiveles.add(new ColaPersonalizada<>());
            quantums.add(8 / (i + 1)); // Quantum: 8, 4, 2, 1
        }
    }
    
    @Override
    public Proceso seleccionarSiguiente() {
        // Si hay proceso actual y no ha agotado quantum, continuar con él
        if (procesoEjecutando != null && contadorQuantum < quantums.get(nivelActual)) {
            contadorQuantum++;
            return procesoEjecutando;
        }
        
        // Buscar proceso desde el nivel más alto al más bajo
        Proceso nuevoProceso = null;
        int nuevoNivel = -1;
        
        for (int i = 0; i < colasNiveles.size(); i++) {
            ColaPersonalizada<Proceso> cola = colasNiveles.get(i);
            if (!cola.estaVacia()) {
                nuevoProceso = cola.desencolar();
                nuevoNivel = i;
                break;
            }
        }
        
        // Manejar el proceso anterior
        if (procesoEjecutando != null && !procesoEjecutando.estaTerminado()) {
            if (contadorQuantum >= quantums.get(nivelActual)) {
                // Agotó quantum, bajar de nivel si es posible
                int siguienteNivel = Math.min(nivelActual + 1, colasNiveles.size() - 1);
                procesoEjecutando.setPrioridad(siguienteNivel);
                colasNiveles.get(siguienteNivel).encolar(procesoEjecutando);
                persistencia.Logger.getInstance().logEvento(new Evento("REALIMENTACION", 
                    "Proceso " + procesoEjecutando.getNombre() + " baja al nivel " + siguienteNivel, 
                    procesoEjecutando));
            } else {
                // No agotó quantum, regresar al mismo nivel
                colasNiveles.get(nivelActual).encolar(procesoEjecutando);
            }
        }
        
        // Configurar nuevo proceso
        procesoEjecutando = nuevoProceso;
        nivelActual = (nuevoProceso != null) ? nuevoNivel : 0;
        contadorQuantum = (nuevoProceso != null) ? 1 : 0;
        
        if (procesoEjecutando != null) {
            persistencia.Logger.getInstance().logEvento(new Evento("PLANIFICADOR", 
                "Multinivel Realimentación selecciona: " + procesoEjecutando.getNombre() + 
                " (nivel: " + nivelActual + ", quantum: " + quantums.get(nivelActual) + 
                ", contador: " + contadorQuantum + ")", procesoEjecutando));
        }
        
        return procesoEjecutando;
    }
    
    @Override
    public void agregarProceso(Proceso proceso) {
        // Nuevos procesos van al nivel más alto (quantum más grande)
        proceso.setPrioridad(0);
        colasNiveles.get(0).encolar(proceso);
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
                if (cola.contiene(p) || p == procesoEjecutando) {
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
        return "Multinivel Realimentación";
    }
    
    @Override
    public String getDescripcion() {
        return "Colas Multinivel con Realimentación - Procesos bajan de nivel si agotan quantum";
    }
    
    @Override
    public List<Proceso> getColaListos() {
        List<Proceso> todos = new ArrayList<>();
        for (ColaPersonalizada<Proceso> cola : colasNiveles) {
            todos.addAll(cola.toList());
        }
        if (procesoEjecutando != null && !procesoEjecutando.estaTerminado()) {
            todos.add(procesoEjecutando);
        }
        return todos;
    }
    
    public List<String> getEstadoColas() {
        List<String> estados = new ArrayList<>();
        for (int i = 0; i < colasNiveles.size(); i++) {
            String estado = "Nivel " + i + " (Q=" + quantums.get(i) + "): " + 
                           colasNiveles.get(i).tamano() + " procesos";
            if (procesoEjecutando != null && nivelActual == i) {
                estado += " [EJECUTANDO: " + procesoEjecutando.getNombre() + "]";
            }
            estados.add(estado);
        }
        return estados;
    }
    
    public int getQuantumActual() {
        return (procesoEjecutando != null) ? quantums.get(nivelActual) : 0;
    }
    
    public int getContadorQuantum() {
        return contadorQuantum;
    }
    
    public int getNivelActual() {
        return nivelActual;
    }
    
    public Proceso getProcesoEjecutando() {
        return procesoEjecutando;
    }
}
