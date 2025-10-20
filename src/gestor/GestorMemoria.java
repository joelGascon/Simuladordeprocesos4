/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author joelgascon
 */
package gestor;

import modelo.*;
import java.util.ArrayList;
import java.util.List;

public class GestorMemoria {
    private int maxProcesosMemoria;
    private List<Proceso> procesosEnMemoria;
    
    public GestorMemoria(int maxProcesos) {
        this.maxProcesosMemoria = maxProcesos;
        this.procesosEnMemoria = new ArrayList<>();
    }
    
    public synchronized boolean puedeCargarProceso() {
        return procesosEnMemoria.size() < maxProcesosMemoria;
    }
    
    public synchronized boolean cargarProceso(Proceso proceso) {
        if (procesosEnMemoria.size() < maxProcesosMemoria) {
            procesosEnMemoria.add(proceso);
            persistencia.Logger.getInstance().logEvento(new Evento("MEMORIA", 
                "Proceso cargado en memoria", proceso));
            return true;
        }
        return false;
    }
    
    public synchronized void descargarProceso(Proceso proceso) {
        procesosEnMemoria.remove(proceso);
        persistencia.Logger.getInstance().logEvento(new Evento("MEMORIA", 
            "Proceso descargado de memoria", proceso));
    }
    
    public synchronized Proceso seleccionarProcesoSuspender() {
        GestorColas gestorColas = nucleo.NucleoSistema.getInstance().getGestorColas();
        
        for (Proceso p : gestorColas.getColaBloqueados()) {
            if (p.getTipo() == TipoProceso.IO_BOUND && p.getCiclosEsperaIO() > 10) {
                return p;
            }
        }
        
        Proceso menorPrioridad = null;
        for (Proceso p : gestorColas.getColaListos()) {
            if (menorPrioridad == null || p.getPrioridad() < menorPrioridad.getPrioridad()) {
                menorPrioridad = p;
            }
        }
        
        return menorPrioridad;
    }
    
    public synchronized int getProcesosEnMemoria() {
        return procesosEnMemoria.size();
    }
    
    public synchronized int getEspacioDisponible() {
        return maxProcesosMemoria - procesosEnMemoria.size();
    }
}
