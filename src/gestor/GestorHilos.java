/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author joelgascon
 */
package gestor;

import modelo.Proceso;
import java.util.concurrent.*;

public class GestorHilos {
    private final ExecutorService executor;
    private final ConcurrentHashMap<String, Future<?>> hilosProcesos;
    private final ConcurrentHashMap<String, Proceso> procesosEjecutando;
    private volatile boolean activo;
    
    public GestorHilos() {
        this.executor = Executors.newCachedThreadPool();
        this.hilosProcesos = new ConcurrentHashMap<>();
        this.procesosEjecutando = new ConcurrentHashMap<>();
        this.activo = true;
    }
    
    public void ejecutarProceso(Proceso proceso, Runnable tarea) {
        if (!activo) {
            System.err.println("GestorHilos no está activo, no se puede ejecutar proceso: " + proceso.getId());
            return;
        }
        
        try {
            Future<?> future = executor.submit(tarea);
            hilosProcesos.put(proceso.getId(), future);
            procesosEjecutando.put(proceso.getId(), proceso);
        } catch (RejectedExecutionException e) {
            System.err.println("No se pudo ejecutar proceso - Executor cerrado: " + e.getMessage());
        }
    }
    
    public void detenerProceso(String procesoId) {
        Future<?> future = hilosProcesos.get(procesoId);
        if (future != null) {
            future.cancel(true);
            hilosProcesos.remove(procesoId);
            procesosEjecutando.remove(procesoId);
        }
    }
    
    public boolean estaEjecutando(String procesoId) {
        Future<?> future = hilosProcesos.get(procesoId);
        return future != null && !future.isDone();
    }
    
    public void shutdown() {
        activo = false;
        
        // Cancelar todas las tareas pendientes
        for (Future<?> future : hilosProcesos.values()) {
            if (future != null && !future.isDone()) {
                future.cancel(true);
            }
        }
        hilosProcesos.clear();
        procesosEjecutando.clear();
        
        // Cerrar el executor
        if (executor != null) {
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                    System.err.println("Timeout esperando por cierre de GestorHilos");
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public boolean isActivo() {
        return activo;
    }
}