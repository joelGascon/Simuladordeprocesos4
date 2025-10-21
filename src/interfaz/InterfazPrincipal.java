/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz;

import modelo.*;
import nucleo.NucleoSistema;
import persistencia.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InterfazPrincipal extends JFrame {
    private NucleoSistema nucleo;
    private Timer timer;
    
    // Paneles principales
    private PanelProcesoEjecucion panelEjecucion;
    private PanelColas panelColas;
    private PanelMetricas panelMetricas;
    private PanelConfiguracion panelConfiguracion;
    
    // Componentes de la barra de control
    private JButton btnIniciar, btnPausar, btnReanudar, btnDetener;
    private JComboBox<String> comboPlanificadores;
    private JSlider sliderVelocidad;
    private JLabel lblEstadoSimulacion;
    private JTextArea txtLog;
    
    public InterfazPrincipal() {
        nucleo = NucleoSistema.getInstance();
        inicializarInterfaz();
    }
    
    private void inicializarInterfaz() {
        setTitle("Simulador de Planificación de Procesos - Universidad");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Crear paneles
        panelEjecucion = new PanelProcesoEjecucion();
        panelColas = new PanelColas();
        panelMetricas = new PanelMetricas();
        panelConfiguracion = new PanelConfiguracion(this);
        
        // Panel superior (ejecución y control)
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelEjecucion, BorderLayout.CENTER);
        panelSuperior.add(crearPanelControl(), BorderLayout.EAST);
        
        // Panel central (colas y log)
        JPanel panelCentral = new JPanel(new GridLayout(1, 2));
        panelCentral.add(panelColas);
        panelCentral.add(crearPanelLog());
        
        // Panel inferior (métricas y configuración)
        JPanel panelInferior = new JPanel(new GridLayout(1, 2));
        panelInferior.add(panelMetricas);
        panelInferior.add(panelConfiguracion);
        
        // Agregar todos los paneles
        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
        
        // Configurar timer para actualizar la interfaz
        timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarInterfaz();
            }
        });
        timer.start();
        
        pack();
        setSize(1400, 900);
        setLocationRelativeTo(null);
    }
    
    private JPanel crearPanelControl() {
        JPanel panelControl = new JPanel();
        panelControl.setLayout(new BoxLayout(panelControl, BoxLayout.Y_AXIS));
        panelControl.setBorder(BorderFactory.createTitledBorder("Control de Simulación"));
        panelControl.setPreferredSize(new Dimension(300, 400));
        
        // Botones de control
        btnIniciar = new JButton("Iniciar Simulación");
        btnPausar = new JButton("Pausar");
        btnReanudar = new JButton("Reanudar");
        btnDetener = new JButton("Detener y Limpiar");
        
        btnIniciar.addActionListener(e -> iniciarSimulacion());
        btnPausar.addActionListener(e -> pausarSimulacion());
        btnReanudar.addActionListener(e -> reanudarSimulacion());
        btnDetener.addActionListener(e -> detenerSimulacion());
        
        // Selector de planificador
        JPanel panelPlanificador = new JPanel(new FlowLayout());
        panelPlanificador.add(new JLabel("Planificador:"));
        comboPlanificadores = new JComboBox<>(new String[]{
            "FCFS", "SJF", "Round Robin", "Prioridades"
        });
        comboPlanificadores.addActionListener(e -> cambiarPlanificador());
        panelPlanificador.add(comboPlanificadores);
        
        // Control de velocidad
        JPanel panelVelocidad = new JPanel(new FlowLayout());
        panelVelocidad.add(new JLabel("Velocidad (ms):"));
        sliderVelocidad = new JSlider(100, 3000, 1000);
        sliderVelocidad.setMajorTickSpacing(1000);
        sliderVelocidad.setMinorTickSpacing(500);
        sliderVelocidad.setPaintTicks(true);
        sliderVelocidad.setPaintLabels(true);
        sliderVelocidad.addChangeListener(e -> cambiarVelocidad());
        panelVelocidad.add(sliderVelocidad);
        
        // Estado de la simulación
        lblEstadoSimulacion = new JLabel("Simulación: DETENIDA");
        lblEstadoSimulacion.setForeground(Color.RED);
        lblEstadoSimulacion.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Agregar componentes al panel de control
        panelControl.add(Box.createVerticalStrut(10));
        panelControl.add(btnIniciar);
        panelControl.add(Box.createVerticalStrut(5));
        panelControl.add(btnPausar);
        panelControl.add(Box.createVerticalStrut(5));
        panelControl.add(btnReanudar);
        panelControl.add(Box.createVerticalStrut(5));
        panelControl.add(btnDetener);
        panelControl.add(Box.createVerticalStrut(15));
        panelControl.add(panelPlanificador);
        panelControl.add(Box.createVerticalStrut(15));
        panelControl.add(panelVelocidad);
        panelControl.add(Box.createVerticalStrut(15));
        panelControl.add(lblEstadoSimulacion);
        panelControl.add(Box.createVerticalStrut(10));
        
        actualizarEstadoBoton();
        return panelControl;
    }
    
    private JScrollPane crearPanelLog() {
        JPanel panelLog = new JPanel(new BorderLayout());
        panelLog.setBorder(BorderFactory.createTitledBorder("Log de Eventos"));
        
        txtLog = new JTextArea(15, 30);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollLog = new JScrollPane(txtLog);
        
        JButton btnLimpiarLog = new JButton("Limpiar Log");
        btnLimpiarLog.addActionListener(e -> txtLog.setText(""));
        
        panelLog.add(scrollLog, BorderLayout.CENTER);
        panelLog.add(btnLimpiarLog, BorderLayout.SOUTH);
        
        return scrollLog;
    }
    
    private void iniciarSimulacion() {
        try {
            nucleo.iniciarSimulacion();
            actualizarEstadoBoton();
            Logger.getInstance().logEvento(new Evento("INTERFAZ", "Simulación iniciada por usuario", null));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al iniciar simulación: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void pausarSimulacion() {
        try {
            nucleo.pausarSimulacion();
            actualizarEstadoBoton();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al pausar simulación: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void reanudarSimulacion() {
        try {
            nucleo.reanudarSimulacion();
            actualizarEstadoBoton();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al reanudar simulación: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void detenerSimulacion() {
        try {
            int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea detener la simulación y limpiar todos los procesos?",
                "Confirmar Detención", JOptionPane.YES_NO_OPTION);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                nucleo.shutdown();
                // Reiniciar el núcleo
                nucleo = NucleoSistema.getInstance();
                actualizarEstadoBoton();
                JOptionPane.showMessageDialog(this, 
                    "Simulación detenida y sistema reiniciado", 
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al detener simulación: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cambiarPlanificador() {
        try {
            String seleccion = (String) comboPlanificadores.getSelectedItem();
            String tipo = "";
            
            switch (seleccion) {
                case "FCFS": tipo = "FCFS"; break;
                case "SJF": tipo = "SJF"; break;
                case "Round Robin": tipo = "ROUNDROBIN"; break;
                case "Prioridades": tipo = "PRIORIDADES"; break;
            }
            
            nucleo.cambiarPlanificador(tipo);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cambiar planificador: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cambiarVelocidad() {
        try {
            int velocidad = sliderVelocidad.getValue();
            nucleo.setDuracionCiclo(velocidad);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cambiar velocidad: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarEstadoBoton() {
        boolean activa = nucleo.isSimulacionActiva();
        
        btnIniciar.setEnabled(!activa);
        btnPausar.setEnabled(activa);
        btnReanudar.setEnabled(!activa);
        btnDetener.setEnabled(true);
        
        if (activa) {
            lblEstadoSimulacion.setText("Simulación: EJECUTANDOSE");
            lblEstadoSimulacion.setForeground(new Color(0, 128, 0)); // Verde oscuro
        } else {
            lblEstadoSimulacion.setText("Simulación: DETENIDA");
            lblEstadoSimulacion.setForeground(Color.RED);
        }
    }
    
    private void actualizarInterfaz() {
        try {
            panelEjecucion.actualizar();
            panelColas.actualizar();
            panelMetricas.actualizar();
            
            // Actualizar estado del sistema operativo
            boolean ejecutandoSO = nucleo.isEjecutandoSO();
            panelEjecucion.actualizarEstadoSO(ejecutandoSO);
            
            // Actualizar log
            actualizarLog();
            
        } catch (Exception e) {
            System.err.println("Error actualizando interfaz: " + e.getMessage());
        }
    }
    
    private void actualizarLog() {
        // Mostrar los últimos eventos en el log
        java.util.List<Evento> eventos = Logger.getInstance().getEventos();
        if (eventos.size() > 50) { // Mostrar solo los últimos 50 eventos
            eventos = eventos.subList(eventos.size() - 50, eventos.size());
        }
        
        StringBuilder logContent = new StringBuilder();
        for (Evento evento : eventos) {
            logContent.append(evento.getMensaje()).append("\n");
        }
        
        txtLog.setText(logContent.toString());
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }
    
    public void agregarProceso(Proceso proceso) {
        try {
            nucleo.agregarProceso(proceso);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al agregar proceso: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        // Versión simplificada - usar look and feel por defecto
        SwingUtilities.invokeLater(() -> {
            new InterfazPrincipal().setVisible(true);
        });
    }
}