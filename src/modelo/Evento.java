/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author joelgascon
 */
package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Evento {
    private LocalDateTime timestamp;
    private String tipo;
    private String descripcion;
    private Proceso proceso;
    
    public Evento(String tipo, String descripcion, Proceso proceso) {
        this.timestamp = LocalDateTime.now();
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.proceso = proceso;
    }
    
    public String getMensaje() {
        String fecha = timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        return String.format("[%s] %s: %s - %s", 
                fecha, tipo, descripcion, 
                proceso != null ? proceso.toString() : "Sistema");
    }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public Proceso getProceso() { return proceso; }
}
