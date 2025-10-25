/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz;

/**
 *
 * @author joelgascon
 */
import modelo.*;
import nucleo.NucleoSistema;
import javax.swing.*;
import java.awt.*;

public class PanelEstados extends JPanel {
    private JLabel lblListos, lblEjecutando, lblBloqueados, lblSuspendidos, lblTerminados;
    
    public PanelEstados() {
        inicializarPanel();
    }
    
    private void inicializarPanel() {
        setLayout(new GridLayout(1, 5, 5, 5));
        setBorder(BorderFactory.createTitledBorder("Resumen de Estados"));
        setBackground(new Color(245, 245, 245));
        
        // Estado LISTOS
        JPanel panelListos = crearPanelEstado("LISTOS", new Color(144, 238, 144), Color.BLACK);
        lblListos = new JLabel("0", JLabel.CENTER);
        lblListos.setFont(new Font("Arial", Font.BOLD, 16));
        panelListos.add(lblListos);
        
        // Estado EJECUTANDO
        JPanel panelEjecutando = crearPanelEstado("EJECUTANDO", new Color(135, 206, 250), Color.BLACK);
        lblEjecutando = new JLabel("0", JLabel.CENTER);
        lblEjecutando.setFont(new Font("Arial", Font.BOLD, 16));
        panelEjecutando.add(lblEjecutando);
        
        // Estado BLOQUEADOS
        JPanel panelBloqueados = crearPanelEstado("BLOQUEADOS", new Color(255, 165, 0), Color.WHITE);
        lblBloqueados = new JLabel("0", JLabel.CENTER);
        lblBloqueados.setFont(new Font("Arial", Font.BOLD, 16));
        panelBloqueados.add(lblBloqueados);
        
        // Estado SUSPENDIDOS
        JPanel panelSuspendidos = crearPanelEstado("SUSPENDIDOS", new Color(220, 20, 60), Color.WHITE);
        lblSuspendidos = new JLabel("0", JLabel.CENTER);
        lblSuspendidos.setFont(new Font("Arial", Font.BOLD, 16));
        panelSuspendidos.add(lblSuspendidos);
        
        // Estado TERMINADOS
        JPanel panelTerminados = crearPanelEstado("TERMINADOS", new Color(128, 128, 128), Color.WHITE);
        lblTerminados = new JLabel("0", JLabel.CENTER);
        lblTerminados.setFont(new Font("Arial", Font.BOLD, 16));
        panelTerminados.add(lblTerminados);
        
        add(panelListos);
        add(panelEjecutando);
        add(panelBloqueados);
        add(panelSuspendidos);
        add(panelTerminados);
    }
    
    private JPanel crearPanelEstado(String titulo, Color colorFondo, Color colorTexto) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(colorFondo);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        
        JLabel lblTitulo = new JLabel(titulo, JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        lblTitulo.setForeground(colorTexto);
        lblTitulo.setOpaque(true);
        lblTitulo.setBackground(colorFondo.darker());
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        return panel;
    }
    
    public void actualizar() {
        NucleoSistema nucleo = NucleoSistema.getInstance();
        
        int listos = nucleo.getGestorColas().getColaListos().size();
        int ejecutando = (nucleo.getProcesoEjecutando() != null) ? 1 : 0;
        int bloqueados = nucleo.getGestorColas().getColaBloqueados().size();
        int suspendidos = nucleo.getGestorColas().getColaListosSuspendidos().size() + 
                         nucleo.getGestorColas().getColaBloqueadosSuspendidos().size();
        int terminados = nucleo.getGestorColas().getProcesosTerminados().size();
        
        lblListos.setText(String.valueOf(listos));
        lblEjecutando.setText(String.valueOf(ejecutando));
        lblBloqueados.setText(String.valueOf(bloqueados));
        lblSuspendidos.setText(String.valueOf(suspendidos));
        lblTerminados.setText(String.valueOf(terminados));
        
        // Actualizar tooltips con información detallada
        lblListos.setToolTipText(listos + " procesos listos para ejecutar");
        lblEjecutando.setToolTipText(ejecutando + " proceso(s) ejecutándose en CPU");
        lblBloqueados.setToolTipText(bloqueados + " procesos bloqueados por E/S");
        lblSuspendidos.setToolTipText(suspendidos + " procesos suspendidos por memoria");
        lblTerminados.setToolTipText(terminados + " procesos terminados");
    }
}