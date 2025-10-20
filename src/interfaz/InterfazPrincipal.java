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
    
    public InterfazPrincipal() {
        nucleo = NucleoSistema.getInstance();
        inicializarInterfaz();
    }
    
    private void inicializarInterfaz() {
        setTitle("Simulador de Planificación de Procesos");
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
        
        // Panel inferior (métricas y configuración)
        JPanel panelInferior = new JPanel(new GridLayout(1, 2));
        panelInferior.add(panelMetricas);
        panelInferior.add(panelConfiguracion);
        
        // Agregar todos los paneles
        add(panelSuperior, BorderLayout.NORTH);
        add(panelColas, BorderLayout.CENTER);
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
        setSize(1200, 800);
        setLocationRelativeTo(null);
    }
    
    private JPanel crearPanelControl() {
        JPanel panelControl = new JPanel();
        panelControl.setLayout(new BoxLayout(panelControl, BoxLayout.Y_AXIS));
        panelControl.setBorder(BorderFactory.createTitledBorder("Control de Simulación"));
        
        // Botones de control
        btnIniciar = new JButton("Iniciar");
        btnPausar = new JButton("Pausar");
        btnReanudar = new JButton("Reanudar");
        btnDetener = new JButton("Detener");
        
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
        panelVelocidad.add(new JLabel("Velocidad:"));
        sliderVelocidad = new JSlider(100, 5000, 1000);
        sliderVelocidad.setMajorTickSpacing(1000);
        sliderVelocidad.setPaintTicks(true);
        sliderVelocidad.setPaintLabels(true);
        sliderVelocidad.addChangeListener(e -> cambiarVelocidad());
        panelVelocidad.add(sliderVelocidad);
        
        // Estado de la simulación
        lblEstadoSimulacion = new JLabel("Simulación: DETENIDA");
        lblEstadoSimulacion.setForeground(Color.RED);
        
        // Agregar componentes al panel de control
        panelControl.add(btnIniciar);
        panelControl.add(btnPausar);
        panelControl.add(btnReanudar);
        panelControl.add(btnDetener);
        panelControl.add(Box.createVerticalStrut(10));
        panelControl.add(panelPlanificador);
        panelControl.add(Box.createVerticalStrut(10));
        panelControl.add(panelVelocidad);
        panelControl.add(Box.createVerticalStrut(10));
        panelControl.add(lblEstadoSimulacion);
        
        return panelControl;
    }
    
    private void iniciarSimulacion() {
        nucleo.iniciarSimulacion();
        actualizarEstadoBoton();
        Logger.getInstance().logEvento(new Evento("INTERFAZ", "Simulación iniciada por usuario", null));
    }
    
    private void pausarSimulacion() {
        nucleo.pausarSimulacion();
        actualizarEstadoBoton();
    }
    
    private void reanudarSimulacion() {
        nucleo.reanudarSimulacion();
        actualizarEstadoBoton();
    }
    
    private void detenerSimulacion() {
        nucleo.shutdown();
        actualizarEstadoBoton();
    }
    
    private void cambiarPlanificador() {
        String seleccion = (String) comboPlanificadores.getSelectedItem();
        String tipo = "";
        
        switch (seleccion) {
            case "FCFS": tipo = "FCFS"; break;
            case "SJF": tipo = "SJF"; break;
            case "Round Robin": tipo = "ROUNDROBIN"; break;
            case "Prioridades": tipo = "PRIORIDADES"; break;
        }
        
        nucleo.cambiarPlanificador(tipo);
    }
    
    private void cambiarVelocidad() {
        int velocidad = sliderVelocidad.getValue();
        nucleo.setDuracionCiclo(velocidad);
    }
    
    private void actualizarEstadoBoton() {
        boolean activa = nucleo.isSimulacionActiva();
        
        btnIniciar.setEnabled(!activa);
        btnPausar.setEnabled(activa);
        btnReanudar.setEnabled(!activa);
        btnDetener.setEnabled(true);
        
        if (activa) {
            lblEstadoSimulacion.setText("Simulación: EJECUTANDOSE");
            lblEstadoSimulacion.setForeground(Color.GREEN);
        } else {
            lblEstadoSimulacion.setText("Simulación: DETENIDA");
            lblEstadoSimulacion.setForeground(Color.RED);
        }
    }
    
    private void actualizarInterfaz() {
        panelEjecucion.actualizar();
        panelColas.actualizar();
        panelMetricas.actualizar();
        
        // Actualizar estado del sistema operativo
        boolean ejecutandoSO = nucleo.isEjecutandoSO();
        panelEjecucion.actualizarEstadoSO(ejecutandoSO);
    }
    
    public void agregarProceso(Proceso proceso) {
        nucleo.agregarProceso(proceso);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new InterfazPrincipal().setVisible(true);
        });
    }
}