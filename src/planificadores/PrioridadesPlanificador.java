/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author joelgascon
 */
package planificadores;

import modelo.*;
import gestor.GestorColas;
import utils.ListaPersonalizada;
import java.util.List;

public class PrioridadesPlanificador implements Planificador {
    private GestorColas gestorColas;
    private ListaPersonalizada<Proceso> colaListos;
    
    public PrioridadesPlanificador(GestorColas gestorColas) {
        this.gestorColas = gestorColas;
        this.colaListos = new ListaPersonalizada<>();
    }
    
    @Override
    public Proceso seleccionarSiguiente() {
        if (colaListos.tamano() > 0) {
            Proceso seleccionado = colaListos.obtener(0);
            int maxPrioridad = seleccionado.getPrioridad();
            int indiceSeleccionado = 0;
            
            for (int i = 1; i < colaListos.tamano(); i++) {
                Proceso p = colaListos.obtener(i);
                if (p.getPrioridad() > maxPrioridad) {
                    maxPrioridad = p.getPrioridad();
                    seleccionado = p;
                    indiceSeleccionado = i;
                }
            }
            
            colaListos.eliminar(indiceSeleccionado);
            persistencia.Logger.getInstance().logEvento(new Evento("PLANIFICADOR", 
                "Prioridades selecciona: " + seleccionado.getNombre() + 
                " (prioridad: " + seleccionado.getPrioridad() + ")", seleccionado));
            return seleccionado;
        }
        return null;
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
        return "Prioridades";
    }
    
    @Override
    public String getDescripcion() {
        return "Planificación por Prioridades - Mayor prioridad primero";
    }
    
    @Override
    public List<Proceso> getColaListos() {
        return colaListos.toList();
    }
}
