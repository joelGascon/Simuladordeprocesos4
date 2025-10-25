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
import utils.ListaPersonalizada;
import java.util.List;

public class SRTPlanificador implements Planificador {
    private GestorColas gestorColas;
    private ListaPersonalizada<Proceso> colaListos;
    private Proceso procesoEjecutando;
    
    public SRTPlanificador(GestorColas gestorColas) {
        this.gestorColas = gestorColas;
        this.colaListos = new ListaPersonalizada<>();
        this.procesoEjecutando = null;
    }
    
    @Override
    public Proceso seleccionarSiguiente() {
        // Siempre buscar el proceso con menor tiempo restante (incluyendo el actual)
        Proceso mejorProceso = procesoEjecutando;
        int menorTiempo = (procesoEjecutando != null) ? 
            procesoEjecutando.getInstruccionesRestantes() : Integer.MAX_VALUE;
        
        // Buscar en la cola de listos
        for (int i = 0; i < colaListos.tamano(); i++) {
            Proceso p = colaListos.obtener(i);
            int tiempoRestante = p.getInstruccionesRestantes();
            if (tiempoRestante < menorTiempo) {
                menorTiempo = tiempoRestante;
                mejorProceso = p;
            }
        }
        
        // Si encontramos uno mejor que el actual
        if (mejorProceso != procesoEjecutando) {
            if (procesoEjecutando != null && !procesoEjecutando.estaTerminado()) {
                colaListos.agregar(procesoEjecutando);
            }
            if (mejorProceso != null) {
                colaListos.eliminar(mejorProceso);
            }
            procesoEjecutando = mejorProceso;
        }
        
        if (procesoEjecutando != null) {
            persistencia.Logger.getInstance().logEvento(new Evento("PLANIFICADOR", 
                "SRT selecciona: " + procesoEjecutando.getNombre() + 
                " (tiempo restante: " + procesoEjecutando.getInstruccionesRestantes() + ")", 
                procesoEjecutando));
        }
        
        return procesoEjecutando;
    }
    
    @Override
    public void agregarProceso(Proceso proceso) {
        colaListos.agregar(proceso);
    }
    
    @Override
    public void ejecutarCiclo() {
        actualizarCola();
    }
    
    private void actualizarCola() {
        List<Proceso> procesosGestor = gestorColas.getColaListos();
        for (Proceso p : procesosGestor) {
            if (!colaListos.contiene(p) && p != procesoEjecutando) {
                colaListos.agregar(p);
            }
        }
    }
    
    @Override
    public String getNombre() {
        return "SRT";
    }
    
    @Override
    public String getDescripcion() {
        return "Shortest Remaining Time - El proceso con menor tiempo restante primero (preemptivo)";
    }
    
    @Override
    public List<Proceso> getColaListos() {
        return colaListos.toList();
    }
}