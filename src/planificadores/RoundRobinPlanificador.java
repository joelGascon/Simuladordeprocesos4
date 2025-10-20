/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author joelgascon
 */
package planificadores;

import modelo.*;
import gestor.GestorColas;
import utils.ColaPersonalizada;
import java.util.List;

public class RoundRobinPlanificador implements Planificador {
    private GestorColas gestorColas;
    private ColaPersonalizada<Proceso> colaListos;
    private int quantum;
    private int contadorQuantum;
    private Proceso procesoActual;
    
    public RoundRobinPlanificador(GestorColas gestorColas, int quantum) {
        this.gestorColas = gestorColas;
        this.colaListos = new ColaPersonalizada<>();
        this.quantum = quantum;
        this.contadorQuantum = 0;
        this.procesoActual = null;
    }
    
    @Override
    public Proceso seleccionarSiguiente() {
        if (procesoActual != null && contadorQuantum < quantum) {
            contadorQuantum++;
            return procesoActual;
        }
        
        if (procesoActual != null && !procesoActual.estaTerminado()) {
            colaListos.encolar(procesoActual);
        }
        
        if (!colaListos.estaVacia()) {
            procesoActual = colaListos.desencolar();
            contadorQuantum = 1;
            persistencia.Logger.getInstance().logEvento(new Evento("PLANIFICADOR", 
                "Round Robin selecciona: " + procesoActual.getNombre() + 
                " (quantum: " + quantum + ")", procesoActual));
            return procesoActual;
        } else {
            procesoActual = null;
            contadorQuantum = 0;
            return null;
        }
    }
    
    @Override
    public void agregarProceso(Proceso proceso) {
        colaListos.encolar(proceso);
    }
    
    @Override
    public void ejecutarCiclo() {
        actualizarCola();
    }
    
    private void actualizarCola() {
        List<Proceso> procesosGestor = gestorColas.getColaListos();
        for (Proceso p : procesosGestor) {
            if (!colaListos.contiene(p) && p != procesoActual) {
                colaListos.encolar(p);
            }
        }
    }
    
    @Override
    public String getNombre() {
        return "Round Robin";
    }
    
    @Override
    public String getDescripcion() {
        return "Round Robin - Rotación circular con quantum de " + quantum + " ciclos";
    }
    
    @Override
    public List<Proceso> getColaListos() {
        return colaListos.toList();
    }
    
    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }
}
