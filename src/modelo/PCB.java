/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author joelgascon
 */
package modelo;

public class PCB {
    private Proceso proceso;
    private int registros[];
    
    public PCB(Proceso proceso) {
        this.proceso = proceso;
        this.registros = new int[8];
    }
    
    public Proceso getProceso() { return proceso; }
    public int[] getRegistros() { return registros; }
    public void setRegistro(int index, int valor) { 
        if (index >= 0 && index < registros.length) {
            registros[index] = valor;
        }
    }
    
    public void guardarEstado() {
        registros[0] = proceso.getProgramCounter();
        registros[1] = proceso.getMemoryAddressRegister();
    }
    
    public void restaurarEstado() {
        proceso.setProgramCounter(registros[0]);
        proceso.setMemoryAddressRegister(registros[1]);
    }
    
    @Override
    public String toString() {
        return String.format("PCB[%s]: PC=%d, MAR=%d, Estado=%s", 
                proceso.getId(), proceso.getProgramCounter(), 
                proceso.getMemoryAddressRegister(), proceso.getEstado());
    }
}
