/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz;

import modelo.Proceso;
import modelo.TipoProceso;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelConfiguracion extends JPanel {
    private InterfazPrincipal interfazPrincipal;
    
    private JTextField txtNombre;
    private JTextField txtInstrucciones;
    private JComboBox<String> comboTipo;
    private JTextField txtCiclosExcepcion;
    private JTextField txtCiclosSatisfacer;
    private JButton btnAgregarProceso;
    
    public PanelConfiguracion(InterfazPrincipal interfaz) {
        this.interfazPrincipal = interfaz;
        inicializarPanel();
    }
    
    private void inicializarPanel() {
        setLayout(new GridLayout(6, 2, 5, 5));
        setBorder(BorderFactory.createTitledBorder("Agregar Nuevo Proceso"));
        
        // Componentes de entrada
        txtNombre = new JTextField("Proceso1");
        txtInstrucciones = new JTextField("10");
        comboTipo = new JComboBox<>(new String[]{"CPU_BOUND", "IO_BOUND"});
        txtCiclosExcepcion = new JTextField("3");
        txtCiclosSatisfacer = new JTextField("2");
        btnAgregarProceso = new JButton("Agregar Proceso");
        
        // Configurar acción del botón
        btnAgregarProceso.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarProceso();
            }
        });
        
        // Agregar componentes al panel
        add(new JLabel("Nombre:"));
        add(txtNombre);
        add(new JLabel("Instrucciones:"));
        add(txtInstrucciones);
        add(new JLabel("Tipo:"));
        add(comboTipo);
        add(new JLabel("Ciclos para Excepción:"));
        add(txtCiclosExcepcion);
        add(new JLabel("Ciclos para Satisfacer:"));
        add(txtCiclosSatisfacer);
        add(new JLabel("")); // Espacio vacío
        add(btnAgregarProceso);
    }
    
    private void agregarProceso() {
        try {
            String nombre = txtNombre.getText();
            int instrucciones = Integer.parseInt(txtInstrucciones.getText());
            TipoProceso tipo = comboTipo.getSelectedItem().equals("CPU_BOUND") ? 
                TipoProceso.CPU_BOUND : TipoProceso.IO_BOUND;
            int ciclosExcepcion = Integer.parseInt(txtCiclosExcepcion.getText());
            int ciclosSatisfacer = Integer.parseInt(txtCiclosSatisfacer.getText());
            
            Proceso proceso = new Proceso(nombre, instrucciones, tipo, ciclosExcepcion, ciclosSatisfacer);
            interfazPrincipal.agregarProceso(proceso);
            
            JOptionPane.showMessageDialog(this, 
                "Proceso agregado exitosamente: " + nombre, 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: Verifique que todos los valores numéricos sean correctos", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
