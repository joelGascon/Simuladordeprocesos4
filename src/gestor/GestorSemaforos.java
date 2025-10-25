/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestor;

/**
 *
 * @author joelgascon
 */
import java.util.concurrent.Semaphore;

public class GestorSemaforos {
    private static GestorSemaforos instancia;
    
    // Semáforos para control de acceso
    private Semaphore semaforoCPU;
    private Semaphore semaforoMemoria;
    private Semaphore semaforoColas;
    private Semaphore semaforoEjecucion;
    
    private GestorSemaforos(int maxProcesosMemoria) {
        // Semáforo para CPU (solo 1 proceso puede ejecutar)
        this.semaforoCPU = new Semaphore(1, true); // fair = true para evitar inanición
        
        // Semáforo para memoria (máximo X procesos en memoria)
        this.semaforoMemoria = new Semaphore(maxProcesosMemoria, true);
        
        // Semáforo para acceso a colas (exclusión mutua)
        this.semaforoColas = new Semaphore(1, true);
        
        // Semáforo para control de ejecución
        this.semaforoEjecucion = new Semaphore(1, true);
    }
    
    public static GestorSemaforos getInstance(int maxProcesosMemoria) {
        if (instancia == null) {
            instancia = new GestorSemaforos(maxProcesosMemoria);
        }
        return instancia;
    }
    
    public static GestorSemaforos getInstance() {
        if (instancia == null) {
            throw new IllegalStateException("GestorSemaforos no inicializado. Llame primero a getInstance(int)");
        }
        return instancia;
    }
    
    // Métodos para adquirir y liberar semáforos
    
    public boolean adquirirCPU() {
        try {
            return semaforoCPU.tryAcquire();
        } catch (Exception e) {
            System.err.println("Error adquiriendo semáforo CPU: " + e.getMessage());
            return false;
        }
    }
    
    public void liberarCPU() {
        semaforoCPU.release();
    }
    
    public boolean adquirirMemoria() {
        try {
            return semaforoMemoria.tryAcquire();
        } catch (Exception e) {
            System.err.println("Error adquiriendo semáforo memoria: " + e.getMessage());
            return false;
        }
    }
    
    public void liberarMemoria() {
        semaforoMemoria.release();
    }
    
    public void adquirirColas() throws InterruptedException {
        semaforoColas.acquire();
    }
    
    public void liberarColas() {
        semaforoColas.release();
    }
    
    public void adquirirEjecucion() throws InterruptedException {
        semaforoEjecucion.acquire();
    }
    
    public void liberarEjecucion() {
        semaforoEjecucion.release();
    }
    
    public int permisosCPUDispobibles() {
        return semaforoCPU.availablePermits();
    }
    
    public int permisosMemoriaDisponibles() {
        return semaforoMemoria.availablePermits();
    }
    
    public int getQueueLengthCPU() {
        return semaforoCPU.getQueueLength();
    }
    
    public int getQueueLengthMemoria() {
        return semaforoMemoria.getQueueLength();
    }
    
    public void reiniciar() {
        // Reiniciar todos los semáforos
        semaforoCPU.drainPermits();
        semaforoCPU.release(1);
        
        semaforoMemoria.drainPermits();
        int maxMemoria = semaforoMemoria.availablePermits();
        semaforoMemoria.release(maxMemoria);
        
        semaforoColas.drainPermits();
        semaforoColas.release(1);
        
        semaforoEjecucion.drainPermits();
        semaforoEjecucion.release(1);
    }
}
