/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author joelgascon
 */
package persistencia;

import modelo.Configuracion;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GestorConfiguracion {
    
    public Configuracion cargarConfiguracion(String ruta) {
        try {
            // Verificar si la ruta es relativa y ajustarla
            File archivoConfig = new File(ruta);
            if (!archivoConfig.exists()) {
                // Intentar con ruta desde el directorio del proyecto
                archivoConfig = new File("./" + ruta);
            }
            if (!archivoConfig.exists()) {
                System.err.println("Archivo de configuración no encontrado: " + ruta);
                System.err.println("Buscando en: " + archivoConfig.getAbsolutePath());
                return new Configuracion(); // Configuración por defecto
            }
            
            String contenido = new String(Files.readAllBytes(archivoConfig.toPath()));
            return parsearJSON(contenido);
        } catch (IOException e) {
            System.err.println("Error cargando configuración: " + e.getMessage());
            return new Configuracion(); // Configuración por defecto
        }
    }
    
    public void guardarConfiguracion(Configuracion config, String ruta) {
        try {
            File archivoConfig = new File(ruta);
            // Crear directorio si no existe
            archivoConfig.getParentFile().mkdirs();
            
            String json = convertirAJSON(config);
            Files.write(archivoConfig.toPath(), json.getBytes());
            System.out.println("Configuración guardada en: " + archivoConfig.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error guardando configuración: " + e.getMessage());
        }
    }
    
    private Configuracion parsearJSON(String json) {
        Configuracion config = new Configuracion();
        
        if (json.contains("\"duracionCiclo\":")) {
            String valor = extraerValor(json, "duracionCiclo");
            config.setDuracionCiclo(Long.parseLong(valor));
        }
        
        if (json.contains("\"tipoPlanificador\":")) {
            String valor = extraerValor(json, "tipoPlanificador");
            config.setTipoPlanificador(valor.replace("\"", ""));
        }
        
        if (json.contains("\"quantumRR\":")) {
            String valor = extraerValor(json, "quantumRR");
            config.setQuantumRR(Integer.parseInt(valor));
        }
        
        if (json.contains("\"maxProcesosMemoria\":")) {
            String valor = extraerValor(json, "maxProcesosMemoria");
            config.setMaxProcesosMemoria(Integer.parseInt(valor));
        }
        
        return config;
    }
    
    private String convertirAJSON(Configuracion config) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"duracionCiclo\": ").append(config.getDuracionCiclo()).append(",\n");
        json.append("  \"tipoPlanificador\": \"").append(config.getTipoPlanificador()).append("\",\n");
        json.append("  \"quantumRR\": ").append(config.getQuantumRR()).append(",\n");
        json.append("  \"maxProcesosMemoria\": ").append(config.getMaxProcesosMemoria()).append("\n");
        json.append("}");
        return json.toString();
    }
    
    private String extraerValor(String json, String clave) {
        String busqueda = "\"" + clave + "\":";
        int inicio = json.indexOf(busqueda) + busqueda.length();
        int fin = json.indexOf(",", inicio);
        if (fin == -1) fin = json.indexOf("}", inicio);
        return json.substring(inicio, fin).trim();
    }
}
