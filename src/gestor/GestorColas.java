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
import utils.ColaPersonalizada;
import java.util.ArrayList;
import java.util.List;

public class GestorColas {
    private ColaPersonalizada<Proceso> colaListos;
    private ColaPersonalizada<Proceso> colaBloqueados;
    private ColaPersonalizada<Proceso> colaListosSuspendidos;
    private ColaPersonalizada<Proceso> colaBloqueadosSuspendidos;
    private List<Proceso> procesosTerminados;
    private List<Proceso> todosProcesos;
    
    public GestorColas() {
        this.colaListos = new ColaPersonalizada<>();
        this.colaBloqueados = new ColaPersonalizada<>();
        this.colaListosSuspendidos = new ColaPersonalizada<>();
        this.colaBloqueadosSuspendidos = new ColaPersonalizada<>();
        this.procesosTerminados = new ArrayList<>();
        this.todosProcesos = new ArrayList<>();
    }
    
    public synchronized void agregarProceso(Proceso proceso) {
        proceso.setEstado(EstadoProceso.LISTO);
        proceso.setTiempoLlegada(nucleo.NucleoSistema.getInstance().getCicloActual());
        colaListos.encolar(proceso);
        todosProcesos.add(proceso);
        persistencia.Logger.getInstance().logEvento(new Evento("SISTEMA", 
            "Proceso agregado a cola de listos", proceso));
    }
    
    public synchronized Proceso obtenerSiguienteProceso() {
        if (!colaListos.estaVacia()) {
            Proceso proceso = colaListos.desencolar();
            proceso.setEstado(EstadoProceso.EJECUTANDO);
            return proceso;
        }
        return null;
    }
    
    public synchronized void bloquearProceso(Proceso proceso) {
        proceso.setEstado(EstadoProceso.BLOQUEADO);
        colaBloqueados.encolar(proceso);
        persistencia.Logger.getInstance().logEvento(new Evento("BLOQUEO", 
            "Proceso bloqueado por E/S", proceso));
    }
    
    public synchronized void desbloquearProceso(Proceso proceso) {
        colaBloqueados.eliminar(proceso);
        proceso.setEstado(EstadoProceso.LISTO);
        colaListos.encolar(proceso);
        persistencia.Logger.getInstance().logEvento(new Evento("DESBLOQUEO", 
            "Proceso desbloqueado", proceso));
    }
    
    public synchronized void suspenderProceso(Proceso proceso) {
        EstadoProceso estadoAnterior = proceso.getEstado();
        if (estadoAnterior == EstadoProceso.LISTO) {
            colaListos.eliminar(proceso);
            proceso.setEstado(EstadoProceso.LISTO_SUSPENDIDO);
            colaListosSuspendidos.encolar(proceso);
        } else if (estadoAnterior == EstadoProceso.BLOQUEADO) {
            colaBloqueados.eliminar(proceso);
            proceso.setEstado(EstadoProceso.BLOQUEADO_SUSPENDIDO);
            colaBloqueadosSuspendidos.encolar(proceso);
        }
        persistencia.Logger.getInstance().logEvento(new Evento("SUSPENSION", 
            "Proceso suspendido por memoria", proceso));
    }
    
    public synchronized void reanudarProceso(Proceso proceso) {
        EstadoProceso estado = proceso.getEstado();
        if (estado == EstadoProceso.LISTO_SUSPENDIDO) {
            colaListosSuspendidos.eliminar(proceso);
            proceso.setEstado(EstadoProceso.LISTO);
            colaListos.encolar(proceso);
        } else if (estado == EstadoProceso.BLOQUEADO_SUSPENDIDO) {
            colaBloqueadosSuspendidos.eliminar(proceso);
            proceso.setEstado(EstadoProceso.BLOQUEADO);
            colaBloqueados.encolar(proceso);
        }
        persistencia.Logger.getInstance().logEvento(new Evento("REANUDACION", 
            "Proceso reanudado", proceso));
    }
    
    public synchronized void terminarProceso(Proceso proceso) {
        proceso.setEstado(EstadoProceso.TERMINADO);
        proceso.setTiempoFinalizacion(nucleo.NucleoSistema.getInstance().getCicloActual());
        procesosTerminados.add(proceso);
        persistencia.Logger.getInstance().logEvento(new Evento("TERMINACION", 
            "Proceso terminado", proceso));
    }
    
    public synchronized void actualizarTiemposEspera() {
        for (Proceso p : colaListos.toList()) {
            p.setTiempoEspera(p.getTiempoEspera() + 1);
        }
        for (Proceso p : colaListosSuspendidos.toList()) {
            p.setTiempoEspera(p.getTiempoEspera() + 1);
        }
    }
    
    // Getters
    public List<Proceso> getColaListos() { return colaListos.toList(); }
    public List<Proceso> getColaBloqueados() { return colaBloqueados.toList(); }
    public List<Proceso> getColaListosSuspendidos() { return colaListosSuspendidos.toList(); }
    public List<Proceso> getColaBloqueadosSuspendidos() { return colaBloqueadosSuspendidos.toList(); }
    public List<Proceso> getProcesosTerminados() { return new ArrayList<>(procesosTerminados); }
    public List<Proceso> getTodosProcesos() { return new ArrayList<>(todosProcesos); }
    
    public int getTotalProcesos() {
        return colaListos.tamano() + colaBloqueados.tamano() + 
               colaListosSuspendidos.tamano() + colaBloqueadosSuspendidos.tamano() +
               procesosTerminados.size();
    }
}
