/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz;

import modelo.Proceso;
import nucleo.NucleoSistema;

import javax.swing.*;
import java.awt.*;

public class PanelProcesoEjecucion extends JPanel {
    private JLabel lblProcesoEjecutando;
    private JLabel lblProgramCounter;
    private JLabel lblEstadoSO;
    private JLabel lblCicloActual;
    
    public PanelProcesoEjecucion() {
        inicializarPanel();
    }
    
    private void inicializarPanel() {
        setLayout(new GridLayout(4, 1));
        setBorder(BorderFactory.createTitledBorder("Ejecución Actual"));
        
        lblProcesoEjecutando = new JLabel("Ningún proceso ejecutándose");
        lblProgramCounter = new JLabel("Program Counter: -");
        lblEstadoSO = new JLabel("Sistema Operativo: INACTIVO");
        lblCicloActual = new JLabel("Ciclo: 0");
        
        // Estilos
        lblProcesoEjecutando.setFont(new Font("Arial", Font.BOLD, 14));
        lblEstadoSO.setFont(new Font("Arial", Font.ITALIC, 12));
        
        add(lblProcesoEjecutando);
        add(lblProgramCounter);
        add(lblEstadoSO);
        add(lblCicloActual);
    }
    
    public void actualizar() {
        NucleoSistema nucleo = NucleoSistema.getInstance();
        Proceso proceso = nucleo.getProcesoEjecutando();
        
        if (proceso != null) {
            lblProcesoEjecutando.setText(
                String.format("Proceso: %s (ID: %s)", 
                    proceso.getNombre(), proceso.getId())
            );
            lblProgramCounter.setText(
                String.format("Program Counter: %d / %d", 
                    proceso.getProgramCounter(), proceso.getTotalInstrucciones())
            );
        } else {
            lblProcesoEjecutando.setText("Ningún proceso ejecutándose");
            lblProgramCounter.setText("Program Counter: -");
        }
        
        lblCicloActual.setText("Ciclo: " + nucleo.getCicloActual());
    }
    
    public void actualizarEstadoSO(boolean ejecutandoSO) {
        if (ejecutandoSO) {
            lblEstadoSO.setText("Sistema Operativo: ACTIVO");
            lblEstadoSO.setForeground(Color.BLUE);
        } else {
            lblEstadoSO.setText("Sistema Operativo: INACTIVO");
            lblEstadoSO.setForeground(Color.GRAY);
        }
    }
}
