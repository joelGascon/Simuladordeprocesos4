/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz;

/**
 *
 * @author joelgascon
 */
import nucleo.NucleoSistema;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelGraficas extends JPanel {
    private NucleoSistema nucleo;
    private List<Double> historialThroughput;
    private List<Double> historialUtilizacion;
    private List<Double> historialTiempoRespuesta;
    private final int MAX_PUNTOS = 50;
    
    public PanelGraficas() {
        this.nucleo = NucleoSistema.getInstance();
        this.historialThroughput = new ArrayList<>();
        this.historialUtilizacion = new ArrayList<>();
        this.historialTiempoRespuesta = new ArrayList<>();
        
        setPreferredSize(new Dimension(600, 400));
        setBorder(BorderFactory.createTitledBorder("Métricas en Tiempo Real"));
        setBackground(Color.WHITE);
    }
    
    public void actualizar() {
        // Agregar nuevos puntos
        historialThroughput.add(nucleo.getMetricas().getThroughput());
        historialUtilizacion.add(nucleo.getMetricas().getUtilizacionCPU() / 100.0); // Normalizar a 0-1
        historialTiempoRespuesta.add(nucleo.getMetricas().getTiempoRespuestaPromedio() / 10.0); // Normalizar
        
        // Mantener solo los últimos puntos
        if (historialThroughput.size() > MAX_PUNTOS) {
            historialThroughput.remove(0);
            historialUtilizacion.remove(0);
            historialTiempoRespuesta.remove(0);
        }
        
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        int padding = 50;
        int graphWidth = width - 2 * padding;
        int graphHeight = height - 2 * padding;
        
        // Dibujar ejes
        g2d.setColor(Color.BLACK);
        g2d.drawLine(padding, padding, padding, height - padding); // Eje Y
        g2d.drawLine(padding, height - padding, width - padding, height - padding); // Eje X
        
        // Dibujar leyenda
        dibujarLeyenda(g2d, width, padding);
        
        // Dibujar líneas de referencia
        dibujarLineasReferencia(g2d, width, height, padding, graphHeight);
        
        // Dibujar las gráficas
        if (historialThroughput.size() > 1) {
            dibujarGrafica(g2d, historialThroughput, Color.BLUE, "Throughput", padding, graphWidth, graphHeight);
            dibujarGrafica(g2d, historialUtilizacion, Color.GREEN, "Utilización CPU", padding, graphWidth, graphHeight);
            dibujarGrafica(g2d, historialTiempoRespuesta, Color.RED, "Tiempo Respuesta", padding, graphWidth, graphHeight);
        }
        
        // Dibujar labels de ejes
        g2d.setColor(Color.BLACK);
        g2d.drawString("Tiempo (ciclos)", width / 2 - 30, height - 10);
        g2d.rotate(-Math.PI / 2);
        g2d.drawString("Métricas Normalizadas", -height / 2 - 40, 15);
        g2d.rotate(Math.PI / 2);
    }
    
    private void dibujarLeyenda(Graphics2D g2d, int width, int padding) {
        int leyendaY = padding - 20;
        g2d.setColor(Color.BLUE);
        g2d.fillRect(width - 120, leyendaY, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Throughput", width - 105, leyendaY + 8);
        
        g2d.setColor(Color.GREEN);
        g2d.fillRect(width - 120, leyendaY + 15, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Utilización CPU", width - 105, leyendaY + 23);
        
        g2d.setColor(Color.RED);
        g2d.fillRect(width - 120, leyendaY + 30, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Tiempo Respuesta", width - 105, leyendaY + 38);
    }
    
    private void dibujarLineasReferencia(Graphics2D g2d, int width, int height, int padding, int graphHeight) {
        g2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= 10; i++) {
            int y = height - padding - (i * graphHeight / 10);
            g2d.drawLine(padding, y, width - padding, y);
            g2d.setColor(Color.BLACK);
            g2d.drawString(String.format("%.1f", i * 0.1), padding - 25, y + 5);
            g2d.setColor(Color.LIGHT_GRAY);
        }
    }
    
    private void dibujarGrafica(Graphics2D g2d, List<Double> datos, Color color, String nombre, 
                               int padding, int graphWidth, int graphHeight) {
        if (datos.size() < 2) return;
        
        g2d.setColor(color);
        for (int i = 1; i < datos.size(); i++) {
            double prevVal = Math.max(0, Math.min(1, datos.get(i-1)));
            double currVal = Math.max(0, Math.min(1, datos.get(i)));
            
            int x1 = padding + ((i-1) * graphWidth / (datos.size() - 1));
            int y1 = padding + graphHeight - (int)(prevVal * graphHeight);
            int x2 = padding + (i * graphWidth / (datos.size() - 1));
            int y2 = padding + graphHeight - (int)(currVal * graphHeight);
            
            g2d.drawLine(x1, y1, x2, y2);
            
            // Puntos en cada dato
            g2d.fillOval(x1 - 2, y1 - 2, 4, 4);
        }
    }
    
    public void limpiar() {
        historialThroughput.clear();
        historialUtilizacion.clear();
        historialTiempoRespuesta.clear();
        repaint();
    }
}
