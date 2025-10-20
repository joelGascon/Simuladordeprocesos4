/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author joelgascon
 */
package modelo;

import java.util.ArrayList;
import java.util.List;

public class Metricas {
    private double throughput;
    private double utilizacionCPU;
    private double equidad;
    private double tiempoRespuestaPromedio;
    private List<Double> historialThroughput;
    private List<Double> historialUtilizacion;
    private List<Double> historialTiempoRespuesta;
    private int ciclosTotales;
    private int ciclosOciosos;
    private int procesosCompletados;
    
    public Metricas() {
        this.historialThroughput = new ArrayList<>();
        this.historialUtilizacion = new ArrayList<>();
        this.historialTiempoRespuesta = new ArrayList<>();
        this.ciclosTotales = 0;
        this.ciclosOciosos = 0;
        this.procesosCompletados = 0;
    }
    
    public void actualizarMetricas(int ciclosTotales, int ciclosOciosos, 
                                   int procesosCompletados, double tiempoRespuestaPromedio) {
        this.ciclosTotales = ciclosTotales;
        this.ciclosOciosos = ciclosOciosos;
        this.procesosCompletados = procesosCompletados;
        this.tiempoRespuestaPromedio = tiempoRespuestaPromedio;
        
        this.throughput = ciclosTotales > 0 ? (double) procesosCompletados / ciclosTotales : 0;
        this.utilizacionCPU = ciclosTotales > 0 ? 
                (double) (ciclosTotales - ciclosOciosos) / ciclosTotales * 100 : 0;
        this.equidad = calcularEquidad();
        
        historialThroughput.add(throughput);
        historialUtilizacion.add(utilizacionCPU);
        historialTiempoRespuesta.add(tiempoRespuestaPromedio);
    }
    
    private double calcularEquidad() {
        if (historialTiempoRespuesta.size() < 2) return 1.0;
        
        double suma = 0;
        for (Double tiempo : historialTiempoRespuesta) {
            suma += tiempo;
        }
        double promedio = suma / historialTiempoRespuesta.size();
        
        double varianza = 0;
        for (Double tiempo : historialTiempoRespuesta) {
            varianza += Math.pow(tiempo - promedio, 2);
        }
        varianza /= historialTiempoRespuesta.size();
        
        double desviacion = Math.sqrt(varianza);
        return desviacion > 0 ? 1.0 / (1.0 + desviacion) : 1.0;
    }
    
    public double getThroughput() { return throughput; }
    public double getUtilizacionCPU() { return utilizacionCPU; }
    public double getEquidad() { return equidad; }
    public double getTiempoRespuestaPromedio() { return tiempoRespuestaPromedio; }
    public List<Double> getHistorialThroughput() { return historialThroughput; }
    public List<Double> getHistorialUtilizacion() { return historialUtilizacion; }
    public List<Double> getHistorialTiempoRespuesta() { return historialTiempoRespuesta; }
    public int getProcesosCompletados() { return procesosCompletados; }
    
    @Override
    public String toString() {
        return String.format("Métricas: Throughput=%.4f, CPU=%.2f%%, Equidad=%.4f, Respuesta=%.2f", 
                throughput, utilizacionCPU, equidad, tiempoRespuestaPromedio);
    }
}
