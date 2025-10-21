/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author joelgascon
 */
package persistencia;

import modelo.Evento;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Logger {
    private static Logger instancia;
    private List<Evento> eventos;
    private PrintWriter archivoLog;
    private final int MAX_EVENTOS = 1000; // Límite para evitar uso excesivo de memoria
    
    private Logger() {
        this.eventos = Collections.synchronizedList(new ArrayList<>());
        try {
            // Crear directorio logs si no existe
            File directorioLogs = new File("logs");
            if (!directorioLogs.exists()) {
                directorioLogs.mkdirs();
            }
            this.archivoLog = new PrintWriter(new FileWriter("logs/simulador.log", true));
        } catch (IOException e) {
            System.err.println("Error inicializando logger: " + e.getMessage());
        }
    }
    
    public static Logger getInstance() {
        if (instancia == null) {
            instancia = new Logger();
        }
        return instancia;
    }
    
    public void logEvento(Evento evento) {
        // Controlar el tamaño de la lista de eventos
        synchronized (eventos) {
            if (eventos.size() >= MAX_EVENTOS) {
                eventos.remove(0); // Remover el evento más antiguo
            }
            eventos.add(evento);
        }
        
        String mensaje = evento.getMensaje();
        System.out.println(mensaje);
        
        if (archivoLog != null) {
            archivoLog.println(mensaje);
            archivoLog.flush();
        }
    }
    
    public List<Evento> getEventos() {
        synchronized (eventos) {
            return new ArrayList<>(eventos);
        }
    }
    
    public void limpiarLog() {
        synchronized (eventos) {
            eventos.clear();
        }
        if (archivoLog != null) {
            archivoLog.close();
            try {
                archivoLog = new PrintWriter(new FileWriter("logs/simulador.log", false));
            } catch (IOException e) {
                System.err.println("Error limpiando log: " + e.getMessage());
            }
        }
    }
    
    public void close() {
        if (archivoLog != null) {
            archivoLog.close();
        }
    }
}
