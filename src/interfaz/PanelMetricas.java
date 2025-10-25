/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz;

import nucleo.NucleoSistema;
import javax.swing.*;
import java.awt.*;

public class PanelMetricas extends JPanel {
    private JLabel lblThroughput;
    private JLabel lblUtilizacionCPU;
    private JLabel lblEquidad;
    private JLabel lblTiempoRespuesta;
    private JLabel lblProcesosCompletados;
    private JLabel lblEstadoSemaforos; // NUEVO
    
    public PanelMetricas() {
        inicializarPanel();
    }
    
    private void inicializarPanel() {
        setLayout(new GridLayout(6, 1)); // Aumentado a 6
        setBorder(BorderFactory.createTitledBorder("Métricas del Sistema"));
        
        lblThroughput = new JLabel("Throughput: 0.00");
        lblUtilizacionCPU = new JLabel("Utilización CPU: 0%");
        lblEquidad = new JLabel("Equidad: 0.00");
        lblTiempoRespuesta = new JLabel("Tiempo Respuesta Promedio: 0.00");
        lblProcesosCompletados = new JLabel("Procesos Completados: 0");
        lblEstadoSemaforos = new JLabel("Semaforos: CPU[1/1] Mem[0/0]"); // NUEVO
        
        // Estilos
        Font metricFont = new Font("Arial", Font.PLAIN, 12);
        lblThroughput.setFont(metricFont);
        lblUtilizacionCPU.setFont(metricFont);
        lblEquidad.setFont(metricFont);
        lblTiempoRespuesta.setFont(metricFont);
        lblProcesosCompletados.setFont(metricFont);
        lblEstadoSemaforos.setFont(new Font("Arial", Font.ITALIC, 10)); // Más pequeño
        lblEstadoSemaforos.setForeground(Color.DARK_GRAY);
        
        add(lblThroughput);
        add(lblUtilizacionCPU);
        add(lblEquidad);
        add(lblTiempoRespuesta);
        add(lblProcesosCompletados);
        add(lblEstadoSemaforos); // NUEVO
    }
    
    public void actualizar() {
        NucleoSistema nucleo = NucleoSistema.getInstance();
        
        lblThroughput.setText(String.format("Throughput: %.4f", 
            nucleo.getMetricas().getThroughput()));
        lblUtilizacionCPU.setText(String.format("Utilización CPU: %.2f%%", 
            nucleo.getMetricas().getUtilizacionCPU()));
        lblEquidad.setText(String.format("Equidad: %.4f", 
            nucleo.getMetricas().getEquidad()));
        lblTiempoRespuesta.setText(String.format("Tiempo Respuesta Promedio: %.2f", 
            nucleo.getMetricas().getTiempoRespuestaPromedio()));
        lblProcesosCompletados.setText(String.format("Procesos Completados: %d", 
            nucleo.getMetricas().getProcesosCompletados()));
        
        // Actualizar estado de semáforos
        lblEstadoSemaforos.setText(nucleo.getGestorColas().getEstadoSemaforos());
    }
}