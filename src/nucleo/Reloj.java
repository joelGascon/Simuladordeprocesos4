/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nucleo;

public class Reloj implements Runnable {
    private long duracionCiclo;
    private boolean activo;
    private NucleoSistema nucleo;
    
    public Reloj(NucleoSistema nucleo, long duracionCiclo) {
        this.nucleo = nucleo;
        this.duracionCiclo = duracionCiclo;
        this.activo = false;
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
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    public void iniciar() {
        if (!activo) {
            activo = true;
            new Thread(this).start();
        }
    }
    
    public void detener() {
        activo = false;
    }
    
    public void setDuracionCiclo(long duracion) {
        this.duracionCiclo = duracion;
    }
}
