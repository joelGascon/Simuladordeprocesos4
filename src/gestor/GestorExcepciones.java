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
    
    public GestorExcepciones() {
        this.executorIO = Executors.newCachedThreadPool();
        this.excepcionesPendientes = new ConcurrentHashMap<>();
    }
    
    public void manejarExcepcionIO(Proceso proceso) {
        Runnable tareaIO = () -> {
            try {
                int ciclosEspera = proceso.getCiclosParaSatisfacerExcepcion();
                for (int i = 0; i < ciclosEspera; i++) {
                    Thread.sleep(nucleo.NucleoSistema.getInstance().getConfiguracion().getDuracionCiclo());
                    proceso.setCiclosEsperaIO(proceso.getCiclosEsperaIO() + 1);
                }
                
                nucleo.NucleoSistema.getInstance().getGestorColas().desbloquearProceso(proceso);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                excepcionesPendientes.remove(proceso.getId());
            }
        };
        
        Future<?> future = executorIO.submit(tareaIO);
        excepcionesPendientes.put(proceso.getId(), future);
        
        persistencia.Logger.getInstance().logEvento(new Evento("EXCEPCION", 
            "Iniciando manejo de E/S para proceso", proceso));
    }
    
    public void cancelarExcepcion(String procesoId) {
        Future<?> future = excepcionesPendientes.get(procesoId);
        if (future != null) {
            future.cancel(true);
            excepcionesPendientes.remove(procesoId);
        }
    }
    
    public void shutdown() {
        executorIO.shutdownNow();
        try {
            if (!executorIO.awaitTermination(3, TimeUnit.SECONDS)) {
                executorIO.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorIO.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}