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
    private ExecutorService executor;
    private ConcurrentHashMap<String, Future<?>> hilosProcesos;
    private ConcurrentHashMap<String, Proceso> procesosEjecutando;
    
    public GestorHilos() {
        this.executor = Executors.newCachedThreadPool();
        this.hilosProcesos = new ConcurrentHashMap<>();
        this.procesosEjecutando = new ConcurrentHashMap<>();
    }
    
    public void ejecutarProceso(Proceso proceso, Runnable tarea) {
        Future<?> future = executor.submit(tarea);
        hilosProcesos.put(proceso.getId(), future);
        procesosEjecutando.put(proceso.getId(), proceso);
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
        executor.shutdownNow();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}