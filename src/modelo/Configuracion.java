/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

public class Configuracion {
    private long duracionCiclo;
    private String tipoPlanificador;
    private int quantumRR;
    private int maxProcesosMemoria;
    
    public Configuracion() {
        this.duracionCiclo = 1000;
        this.tipoPlanificador = "FCFS";
        this.quantumRR = 4;
        this.maxProcesosMemoria = 10;
    }
    
    public long getDuracionCiclo() { return duracionCiclo; }
    public void setDuracionCiclo(long duracion) { this.duracionCiclo = duracion; }
    public String getTipoPlanificador() { return tipoPlanificador; }
    public void setTipoPlanificador(String tipo) { this.tipoPlanificador = tipo; }
    public int getQuantumRR() { return quantumRR; }
    public void setQuantumRR(int quantum) { this.quantumRR = quantum; }
    public int getMaxProcesosMemoria() { return maxProcesosMemoria; }
    public void setMaxProcesosMemoria(int max) { this.maxProcesosMemoria = max; }
}