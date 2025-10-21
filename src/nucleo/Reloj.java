/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nucleo;

public class Reloj implements Runnable {
    private long duracionCiclo;
    private volatile boolean activo;
    private NucleoSistema nucleo;
    private Thread hiloReloj;
    
    public Reloj(NucleoSistema nucleo, long duracionCiclo) {
        this.nucleo = nucleo;
        this.duracionCiclo = duracionCiclo;
        this.activo = false;
        this.hiloReloj = null;
    }
    
    @Override
    public void run() {
        while (activo) {
            try {
                Thread.sleep(duracionCiclo);
                if (activo && nucleo.isSimulacionActiva()) {
                    nucleo.ejecutarCiclo();
                }
            } catch (InterruptedException e) {
                System.out.println("Reloj interrumpido");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Error en el reloj: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    public void iniciar() {
        if (!activo) {
            activo = true;
            hiloReloj = new Thread(this, "Reloj-Simulacion");
            hiloReloj.start();
        }
    }
    
    public void detener() {
        activo = false;
        if (hiloReloj != null) {
            hiloReloj.interrupt();
            try {
                hiloReloj.join(1000); // Esperar máximo 1 segundo
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            hiloReloj = null;
        }
    }
    
    public void setDuracionCiclo(long duracion) {
        this.duracionCiclo = duracion;
    }
    
    public boolean isActivo() {
        return activo;
    }
}
