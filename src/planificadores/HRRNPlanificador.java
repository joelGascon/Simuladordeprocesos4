/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package planificadores;

/**
 *
 * @author joelgascon
 */
import modelo.*;
import gestor.GestorColas;
import utils.ListaPersonalizada;
import java.util.List;

public class HRRNPlanificador implements Planificador {
    private GestorColas gestorColas;
    private ListaPersonalizada<Proceso> colaListos;
    
    public HRRNPlanificador(GestorColas gestorColas) {
        this.gestorColas = gestorColas;
        this.colaListos = new ListaPersonalizada<>();
    }
    
    @Override
    public Proceso seleccionarSiguiente() {
        if (colaListos.tamano() == 0) {
            return null;
        }
        
        Proceso mejorProceso = null;
        double mayorRatio = -1;
        
        for (int i = 0; i < colaListos.tamano(); i++) {
            Proceso p = colaListos.obtener(i);
            double ratio = calcularResponseRatio(p);
            
            if (ratio > mayorRatio) {
                mayorRatio = ratio;
                mejorProceso = p;
            }
        }
        
        if (mejorProceso != null) {
            colaListos.eliminar(mejorProceso);
            persistencia.Logger.getInstance().logEvento(new Evento("PLANIFICADOR", 
                "HRRN selecciona: " + mejorProceso.getNombre() + 
                " (ratio: " + String.format("%.2f", mayorRatio) + ")", mejorProceso));
        }
        
        return mejorProceso;
    }
    
    private double calcularResponseRatio(Proceso proceso) {
        int tiempoEspera = proceso.getTiempoEspera();
        int tiempoServicio = proceso.getTiempoEjecucion();
        int tiempoTotal = tiempoEspera + tiempoServicio;
        
        if (tiempoServicio == 0) {
            return 1 + tiempoEspera; // Evitar división por cero
        }
        
        return (double) (tiempoEspera + tiempoServicio) / tiempoServicio;
    }
    
    @Override
    public void agregarProceso(Proceso proceso) {
        colaListos.agregar(proceso);
    }
    
    @Override
    public void ejecutarCiclo() {
        actualizarCola();
    }
    
    private void actualizarCola() {
        List<Proceso> procesosGestor = gestorColas.getColaListos();
        for (Proceso p : procesosGestor) {
            if (!colaListos.contiene(p)) {
                colaListos.agregar(p);
            }
        }
    }
    
    @Override
    public String getNombre() {
        return "HRRN";
    }
    
    @Override
    public String getDescripcion() {
        return "Highest Response Ratio Next - Mayor ratio de respuesta primero (no preemptivo)";
    }
    
    @Override
    public List<Proceso> getColaListos() {
        return colaListos.toList();
    }
}