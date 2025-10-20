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

public class DialogoNuevoProceso extends JDialog {
    private Proceso procesoCreado;
    private boolean aceptado;
    
    private JTextField txtNombre;
    private JSpinner spinnerInstrucciones;
    private JComboBox<TipoProceso> comboTipo;
    private JSpinner spinnerCiclosExcepcion;
    private JSpinner spinnerCiclosSatisfacer;
    
    public DialogoNuevoProceso(Frame parent) {
        super(parent, "Crear Nuevo Proceso", true);
        inicializarDialogo();
    }
    
    private void inicializarDialogo() {
        setLayout(new GridLayout(6, 2, 10, 10));
        
        // Componentes de entrada
        txtNombre = new JTextField("Proceso");
        spinnerInstrucciones = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
        comboTipo = new JComboBox<>(TipoProceso.values());
        spinnerCiclosExcepcion = new JSpinner(new SpinnerNumberModel(3, 1, 100, 1));
        spinnerCiclosSatisfacer = new JSpinner(new SpinnerNumberModel(2, 1, 100, 1));
        
        // Botones
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnAceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validarDatos()) {
                    crearProceso();
                    aceptado = true;
                    dispose();
                }
            }
        });
        
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aceptado = false;
                dispose();
            }
        });
        
        // Agregar componentes
        add(new JLabel("Nombre:"));
        add(txtNombre);
        add(new JLabel("Total Instrucciones:"));
        add(spinnerInstrucciones);
        add(new JLabel("Tipo:"));
        add(comboTipo);
        add(new JLabel("Ciclos para Excepción:"));
        add(spinnerCiclosExcepcion);
        add(new JLabel("Ciclos para Satisfacer:"));
        add(spinnerCiclosSatisfacer);
        add(btnAceptar);
        add(btnCancelar);
        
        pack();
        setLocationRelativeTo(getParent());
    }
    
    private boolean validarDatos() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    private void crearProceso() {
        String nombre = txtNombre.getText();
        int instrucciones = (Integer) spinnerInstrucciones.getValue();
        TipoProceso tipo = (TipoProceso) comboTipo.getSelectedItem();
        int ciclosExcepcion = (Integer) spinnerCiclosExcepcion.getValue();
        int ciclosSatisfacer = (Integer) spinnerCiclosSatisfacer.getValue();
        
        procesoCreado = new Proceso(nombre, instrucciones, tipo, ciclosExcepcion, ciclosSatisfacer);
    }
    
    public Proceso getProcesoCreado() {
        return procesoCreado;
    }
    
    public boolean isAceptado() {
        return aceptado;
    }
}
