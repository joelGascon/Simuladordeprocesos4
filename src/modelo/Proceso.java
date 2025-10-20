/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

public class Proceso {
    private static int contadorId = 1;
    
    private String id;
    private String nombre;
    private int totalInstrucciones;
    private int instruccionesEjecutadas;
    private EstadoProceso estado;
    private TipoProceso tipo;
    private int ciclosParaExcepcion;
    private int ciclosParaSatisfacerExcepcion;
    private int ciclosEsperaIO;
    private int programCounter;
    private int memoryAddressRegister;
    private int tiempoLlegada;
    private int tiempoFinalizacion;
    private int tiempoEjecucion;
    private int tiempoEspera;
    private int prioridad;
    
    public Proceso(String nombre, int totalInstrucciones, TipoProceso tipo, 
                  int ciclosParaExcepcion, int ciclosParaSatisfacerExcepcion) {
        this.id = "P" + contadorId++;
        this.nombre = nombre;
        this.totalInstrucciones = totalInstrucciones;
        this.tipo = tipo;
        this.ciclosParaExcepcion = ciclosParaExcepcion;
        this.ciclosParaSatisfacerExcepcion = ciclosParaSatisfacerExcepcion;
        this.estado = EstadoProceso.NUEVO;
        this.programCounter = 0;
        this.memoryAddressRegister = 0;
        this.instruccionesEjecutadas = 0;
        this.ciclosEsperaIO = 0;
        this.prioridad = 1;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public int getTotalInstrucciones() { return totalInstrucciones; }
    public int getInstruccionesEjecutadas() { return instruccionesEjecutadas; }
    public EstadoProceso getEstado() { return estado; }
    public void setEstado(EstadoProceso estado) { this.estado = estado; }
    public TipoProceso getTipo() { return tipo; }
    public int getCiclosParaExcepcion() { return ciclosParaExcepcion; }
    public int getCiclosParaSatisfacerExcepcion() { return ciclosParaSatisfacerExcepcion; }
    public int getProgramCounter() { return programCounter; }
    public void setProgramCounter(int pc) { this.programCounter = pc; }
    public int getMemoryAddressRegister() { return memoryAddressRegister; }
    public void setMemoryAddressRegister(int mar) { this.memoryAddressRegister = mar; }
    public int getTiempoLlegada() { return tiempoLlegada; }
    public void setTiempoLlegada(int tiempo) { this.tiempoLlegada = tiempo; }
    public int getTiempoFinalizacion() { return tiempoFinalizacion; }
    public void setTiempoFinalizacion(int tiempo) { this.tiempoFinalizacion = tiempo; }
    public int getTiempoEjecucion() { return tiempoEjecucion; }
    public void setTiempoEjecucion(int tiempo) { this.tiempoEjecucion = tiempo; }
    public int getTiempoEspera() { return tiempoEspera; }
    public void setTiempoEspera(int tiempo) { this.tiempoEspera = tiempo; }
    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }
    public int getCiclosEsperaIO() { return ciclosEsperaIO; }
    public void setCiclosEsperaIO(int ciclos) { this.ciclosEsperaIO = ciclos; }
    
    public void ejecutarInstruccion() {
        if (instruccionesEjecutadas < totalInstrucciones) {
            instruccionesEjecutadas++;
            programCounter++;
            memoryAddressRegister++;
            tiempoEjecucion++;
        }
    }
    
    public boolean estaTerminado() {
        return instruccionesEjecutadas >= totalInstrucciones;
    }
    
    public int getInstruccionesRestantes() {
        return totalInstrucciones - instruccionesEjecutadas;
    }
    
    public boolean debeGenerarExcepcion() {
        if (tipo == TipoProceso.IO_BOUND && ciclosParaExcepcion > 0) {
            return (instruccionesEjecutadas % ciclosParaExcepcion) == 0 && 
                   instruccionesEjecutadas > 0;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) - PC: %d/%d - Estado: %s", 
                nombre, id, programCounter, totalInstrucciones, estado);
    }
}