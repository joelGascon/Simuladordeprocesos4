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
import java.util.concurrent.*;

public class GestorExcepciones {
    private ExecutorService executorIO;
    private ConcurrentHashMap<String, Future<?>> excepcionesPendientes;
    private volatile boolean activo;
    
    public GestorExcepciones() {
        this.executorIO = Executors.newCachedThreadPool();
        this.excepcionesPendientes = new ConcurrentHashMap<>();
        this.activo = true;
    }
    
    public void manejarExcepcionIO(Proceso proceso) {
        if (!activo) {
            System.err.println("GestorExcepciones no está activo, no se puede manejar excepción para: " + proceso.getId());
            return;
        }
        
        Runnable tareaIO = () -> {
            try {
                int ciclosEspera = proceso.getCiclosParaSatisfacerExcepcion();
                for (int i = 0; i < ciclosEspera; i++) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException("Tarea interrumpida");
                    }
                    Thread.sleep(nucleo.NucleoSistema.getInstance().getConfiguracion().getDuracionCiclo());
                    proceso.setCiclosEsperaIO(proceso.getCiclosEsperaIO() + 1);
                }
                
                // Solo desbloquear si el proceso todavía existe y no fue terminado
                if (proceso.getEstado() == EstadoProceso.BLOQUEADO) {
                    nucleo.NucleoSistema.getInstance().getGestorColas().desbloquearProceso(proceso);
                }
                
            } catch (InterruptedException e) {
                System.out.println("Manejo de E/S interrumpido para proceso: " + proceso.getId());
                Thread.currentThread().interrupt();
            } finally {
                excepcionesPendientes.remove(proceso.getId());
            }
        };
        
        try {
            Future<?> future = executorIO.submit(tareaIO);
            excepcionesPendientes.put(proceso.getId(), future);
            
            persistencia.Logger.getInstance().logEvento(new Evento("EXCEPCION", 
                "Iniciando manejo de E/S para proceso", proceso));
        } catch (RejectedExecutionException e) {
            System.err.println("No se pudo programar tarea de E/S - Executor cerrado: " + e.getMessage());
        }
    }
    
    public void cancelarExcepcion(String procesoId) {
        Future<?> future = excepcionesPendientes.get(procesoId);
        if (future != null) {
            future.cancel(true);
            excepcionesPendientes.remove(procesoId);
        }
    }
    
    public void shutdown() {
        activo = false;
        
        // Cancelar todas las tareas pendientes
        for (Future<?> future : excepcionesPendientes.values()) {
            if (future != null && !future.isDone()) {
                future.cancel(true);
            }
        }
        excepcionesPendientes.clear();
        
        // Cerrar el executor
        if (executorIO != null) {
            executorIO.shutdownNow();
            try {
                if (!executorIO.awaitTermination(2, TimeUnit.SECONDS)) {
                    System.err.println("Timeout esperando por cierre de GestorExcepciones");
                }
            } catch (InterruptedException e) {
                executorIO.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public boolean isActivo() {
        return activo;
    }
}