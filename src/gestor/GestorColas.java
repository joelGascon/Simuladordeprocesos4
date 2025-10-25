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
import java.util.concurrent.Semaphore;

public class GestorColas {
    private ColaPersonalizada<Proceso> colaListos;
    private ColaPersonalizada<Proceso> colaBloqueados;
    private ColaPersonalizada<Proceso> colaListosSuspendidos;
    private ColaPersonalizada<Proceso> colaBloqueadosSuspendidos;
    private List<Proceso> procesosTerminados;
    private List<Proceso> todosProcesos;
    private GestorSemaforos gestorSemaforos;
    
    public GestorColas() {
        this.colaListos = new ColaPersonalizada<>();
        this.colaBloqueados = new ColaPersonalizada<>();
        this.colaListosSuspendidos = new ColaPersonalizada<>();
        this.colaBloqueadosSuspendidos = new ColaPersonalizada<>();
        this.procesosTerminados = new ArrayList<>();
        this.todosProcesos = new ArrayList<>();
        this.gestorSemaforos = GestorSemaforos.getInstance();
    }
    
    public void agregarProceso(Proceso proceso) {
        try {
            gestorSemaforos.adquirirColas();
            
            proceso.setEstado(EstadoProceso.LISTO);
            proceso.setTiempoLlegada(nucleo.NucleoSistema.getInstance().getCicloActual());
            colaListos.encolar(proceso);
            todosProcesos.add(proceso);
            persistencia.Logger.getInstance().logEvento(new Evento("SISTEMA", 
                "Proceso agregado a cola de listos", proceso));
                
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupción al agregar proceso: " + e.getMessage());
        } finally {
            gestorSemaforos.liberarColas();
        }
    }
    
    public Proceso obtenerSiguienteProceso() {
        try {
            gestorSemaforos.adquirirColas();
            
            if (!colaListos.estaVacia()) {
                Proceso proceso = colaListos.desencolar();
                proceso.setEstado(EstadoProceso.EJECUTANDO);
                return proceso;
            }
            return null;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupción al obtener siguiente proceso: " + e.getMessage());
            return null;
        } finally {
            gestorSemaforos.liberarColas();
        }
    }
    
    public void bloquearProceso(Proceso proceso) {
        try {
            gestorSemaforos.adquirirColas();
            
            proceso.setEstado(EstadoProceso.BLOQUEADO);
            colaBloqueados.encolar(proceso);
            persistencia.Logger.getInstance().logEvento(new Evento("BLOQUEO", 
                "Proceso bloqueado por E/S", proceso));
                
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupción al bloquear proceso: " + e.getMessage());
        } finally {
            gestorSemaforos.liberarColas();
        }
    }
    
    public void desbloquearProceso(Proceso proceso) {
        try {
            gestorSemaforos.adquirirColas();
            
            colaBloqueados.eliminar(proceso);
            proceso.setEstado(EstadoProceso.LISTO);
            colaListos.encolar(proceso);
            persistencia.Logger.getInstance().logEvento(new Evento("DESBLOQUEO", 
                "Proceso desbloqueado", proceso));
                
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupción al desbloquear proceso: " + e.getMessage());
        } finally {
            gestorSemaforos.liberarColas();
        }
    }
    
    public void suspenderProceso(Proceso proceso) {
        try {
            gestorSemaforos.adquirirColas();
            
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
                
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupción al suspender proceso: " + e.getMessage());
        } finally {
            gestorSemaforos.liberarColas();
        }
    }
    
    public void reanudarProceso(Proceso proceso) {
        try {
            gestorSemaforos.adquirirColas();
            
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
                
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupción al reanudar proceso: " + e.getMessage());
        } finally {
            gestorSemaforos.liberarColas();
        }
    }
    
    public void terminarProceso(Proceso proceso) {
        try {
            gestorSemaforos.adquirirColas();
            
            proceso.setEstado(EstadoProceso.TERMINADO);
            proceso.setTiempoFinalizacion(nucleo.NucleoSistema.getInstance().getCicloActual());
            procesosTerminados.add(proceso);
            persistencia.Logger.getInstance().logEvento(new Evento("TERMINACION", 
                "Proceso terminado", proceso));
                
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupción al terminar proceso: " + e.getMessage());
        } finally {
            gestorSemaforos.liberarColas();
        }
    }
    
    public void actualizarTiemposEspera() {
        try {
            gestorSemaforos.adquirirColas();
            
            for (Proceso p : colaListos.toList()) {
                p.setTiempoEspera(p.getTiempoEspera() + 1);
            }
            for (Proceso p : colaListosSuspendidos.toList()) {
                p.setTiempoEspera(p.getTiempoEspera() + 1);
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupción al actualizar tiempos de espera: " + e.getMessage());
        } finally {
            gestorSemaforos.liberarColas();
        }
    }
    
    // Getters con protección de semáforos
    public List<Proceso> getColaListos() {
        try {
            gestorSemaforos.adquirirColas();
            return new ArrayList<>(colaListos.toList());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        } finally {
            gestorSemaforos.liberarColas();
        }
    }
    
    public List<Proceso> getColaBloqueados() {
        try {
            gestorSemaforos.adquirirColas();
            return new ArrayList<>(colaBloqueados.toList());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        } finally {
            gestorSemaforos.liberarColas();
        }
    }
    
    public List<Proceso> getColaListosSuspendidos() {
        try {
            gestorSemaforos.adquirirColas();
            return new ArrayList<>(colaListosSuspendidos.toList());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        } finally {
            gestorSemaforos.liberarColas();
        }
    }
    
    public List<Proceso> getColaBloqueadosSuspendidos() {
        try {
            gestorSemaforos.adquirirColas();
            return new ArrayList<>(colaBloqueadosSuspendidos.toList());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        } finally {
            gestorSemaforos.liberarColas();
        }
    }
    
    public List<Proceso> getProcesosTerminados() {
        try {
            gestorSemaforos.adquirirColas();
            return new ArrayList<>(procesosTerminados);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        } finally {
            gestorSemaforos.liberarColas();
        }
    }
    
    public List<Proceso> getTodosProcesos() {
        try {
            gestorSemaforos.adquirirColas();
            return new ArrayList<>(todosProcesos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        } finally {
            gestorSemaforos.liberarColas();
        }
    }
    
    public int getTotalProcesos() {
        try {
            gestorSemaforos.adquirirColas();
            return colaListos.tamano() + colaBloqueados.tamano() + 
                   colaListosSuspendidos.tamano() + colaBloqueadosSuspendidos.tamano() +
                   procesosTerminados.size();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 0;
        } finally {
            gestorSemaforos.liberarColas();
        }
    }
    
    // Método para obtener información de los semáforos
    public String getEstadoSemaforos() {
        return String.format("CPU: %d/%d, Memoria: %d/%d, Colas: %d, Ejecución: %d",
            gestorSemaforos.permisosCPUDispobibles(), 1,
            gestorSemaforos.permisosMemoriaDisponibles(), 
            gestorSemaforos.getQueueLengthMemoria(),
            gestorSemaforos.getQueueLengthCPU(),
            1 - gestorSemaforos.permisosCPUDispobibles());
    }
}