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

public class FCFSPlanificador implements Planificador {
    private GestorColas gestorColas;
    private ColaPersonalizada<Proceso> colaListos;
    
    public FCFSPlanificador(GestorColas gestorColas) {
        this.gestorColas = gestorColas;
        this.colaListos = new ColaPersonalizada<>();
    }
    
    @Override
    public Proceso seleccionarSiguiente() {
        if (!colaListos.estaVacia()) {
            Proceso proceso = colaListos.desencolar();
            persistencia.Logger.getInstance().logEvento(new Evento("PLANIFICADOR", 
                "FCFS selecciona: " + proceso.getNombre(), proceso));
            return proceso;
        }
        return null;
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
            if (!colaListos.contiene(p)) {
                colaListos.encolar(p);
            }
        }
    }
    
    @Override
    public String getNombre() {
        return "FCFS";
    }
    
    @Override
    public String getDescripcion() {
        return "First-Come, First-Served - El primero en llegar es el primero en ser servido";
    }
    
    @Override
    public List<Proceso> getColaListos() {
        return colaListos.toList();
    }
}