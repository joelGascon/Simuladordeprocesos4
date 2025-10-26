/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz;

import modelo.*;
import nucleo.NucleoSistema;
import persistencia.Logger;
import planificadores.RoundRobinPlanificador;

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
    private PanelEstados panelEstados;
    private PanelGraficas panelGraficas;
    
    // Componentes de la barra de control
    private JButton btnIniciar, btnPausar, btnReanudar, btnDetener, btnLimpiarTodo;
    private JComboBox<String> comboPlanificadores;
    private JSlider sliderVelocidad;
    private JLabel lblEstadoSimulacion;
    private JTextArea txtLog;
    private JLabel lblInfoPlanificador;
    private JLabel lblCicloGlobal;
    private JLabel lblProcesosActivos;
    
    public InterfazPrincipal() {
        nucleo = NucleoSistema.getInstance();
        inicializarInterfaz();
    }
    
    private void inicializarInterfaz() {
        setTitle("Simulador de Planificación de Procesos - Sistema Operativo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Crear paneles
        panelEjecucion = new PanelProcesoEjecucion();
        panelColas = new PanelColas();
        panelMetricas = new PanelMetricas();
        panelConfiguracion = new PanelConfiguracion(this);
        panelEstados = new PanelEstados();
        panelGraficas = new PanelGraficas();
        
        // Panel superior (ejecución y control - MÁS COMPACTO)
        JPanel panelSuperior = new JPanel(new BorderLayout());
        
        // Panel izquierdo superior (ejecución y gráficas)
        JPanel panelIzquierdoSuperior = new JPanel(new BorderLayout());
        panelIzquierdoSuperior.add(panelEjecucion, BorderLayout.NORTH);
        panelIzquierdoSuperior.add(panelGraficas, BorderLayout.CENTER);
        
        panelSuperior.add(panelIzquierdoSuperior, BorderLayout.CENTER);
        panelSuperior.add(crearPanelControl(), BorderLayout.EAST);
        
        // Panel central izquierdo (colas y estados)
        JPanel panelCentralIzquierdo = new JPanel(new BorderLayout());
        panelCentralIzquierdo.add(panelColas, BorderLayout.CENTER);
        panelCentralIzquierdo.add(panelEstados, BorderLayout.SOUTH);
        
        // Panel central derecho (log)
        JScrollPane panelLog = crearPanelLog();
        
        // Panel central completo
        JPanel panelCentral = new JPanel(new GridLayout(1, 2));
        panelCentral.add(panelCentralIzquierdo);
        panelCentral.add(panelLog);
        
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
        setSize(1800, 1200); // Ventana más grande para acomodar gráficas
        setLocationRelativeTo(null);
    }
    
    private JPanel crearPanelControl() {
        JPanel panelControl = new JPanel();
        panelControl.setLayout(new BoxLayout(panelControl, BoxLayout.Y_AXIS));
        panelControl.setBorder(BorderFactory.createTitledBorder("Control de Simulación"));
        panelControl.setPreferredSize(new Dimension(350, 600));
        panelControl.setBackground(new Color(245, 245, 245));
        
        // Título y ciclo global
        JLabel lblTitulo = new JLabel("CONTROL PRINCIPAL");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(0, 0, 128));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblCicloGlobal = new JLabel("Ciclo Global: 0");
        lblCicloGlobal.setFont(new Font("Arial", Font.BOLD, 14));
        lblCicloGlobal.setForeground(Color.BLUE);
        lblCicloGlobal.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblProcesosActivos = new JLabel("Procesos Activos: 0");
        lblProcesosActivos.setFont(new Font("Arial", Font.BOLD, 12));
        lblProcesosActivos.setForeground(new Color(0, 100, 0));
        lblProcesosActivos.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Botones de control
        btnIniciar = crearBoton("▶ INICIAR SIMULACIÓN", new Color(46, 125, 50));
        btnPausar = crearBoton("⏸️ PAUSAR", new Color(237, 108, 2));
        btnReanudar = crearBoton("⏵ REANUDAR", new Color(2, 119, 189));
        btnDetener = crearBoton("⏹️ DETENER", new Color(198, 40, 40));
        btnLimpiarTodo = crearBoton("🗑️ LIMPIAR TODO", new Color(120, 120, 120));
        
        btnIniciar.addActionListener(e -> iniciarSimulacion());
        btnPausar.addActionListener(e -> pausarSimulacion());
        btnReanudar.addActionListener(e -> reanudarSimulacion());
        btnDetener.addActionListener(e -> detenerSimulacion());
        btnLimpiarTodo.addActionListener(e -> limpiarTodo());
        
        // Selector de planificador
        JPanel panelPlanificador = crearPanelPlanificador();
        
        // Control de velocidad
        JPanel panelVelocidad = crearPanelVelocidad();
        
        // Estado de la simulación
        JPanel panelEstado = crearPanelEstado();
        
        // Agregar componentes al panel de control
        panelControl.add(Box.createVerticalStrut(10));
        panelControl.add(lblTitulo);
        panelControl.add(Box.createVerticalStrut(5));
        panelControl.add(lblCicloGlobal);
        panelControl.add(Box.createVerticalStrut(3));
        panelControl.add(lblProcesosActivos);
        panelControl.add(Box.createVerticalStrut(15));
        
        panelControl.add(btnIniciar);
        panelControl.add(Box.createVerticalStrut(8));
        panelControl.add(btnPausar);
        panelControl.add(Box.createVerticalStrut(8));
        panelControl.add(btnReanudar);
        panelControl.add(Box.createVerticalStrut(8));
        panelControl.add(btnDetener);
        panelControl.add(Box.createVerticalStrut(8));
        panelControl.add(btnLimpiarTodo);
        panelControl.add(Box.createVerticalStrut(20));
        
        panelControl.add(panelPlanificador);
        panelControl.add(Box.createVerticalStrut(15));
        
        panelControl.add(panelVelocidad);
        panelControl.add(Box.createVerticalStrut(15));
        
        panelControl.add(panelEstado);
        panelControl.add(Box.createVerticalStrut(10));
        
        actualizarEstadoBoton();
        return panelControl;
    }
    
    private JPanel crearPanelPlanificador() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createTitledBorder("Planificación"));
        
        JPanel panelCombo = new JPanel(new FlowLayout());
        panelCombo.setBackground(new Color(245, 245, 245));
        JLabel lblPlanificador = new JLabel("Algoritmo:");
        lblPlanificador.setFont(new Font("Arial", Font.BOLD, 12));
        panelCombo.add(lblPlanificador);
        
        comboPlanificadores = new JComboBox<>(new String[]{
            "FCFS", "SPN", "SRT", "Round Robin", "HRRN", "Prioridades"
        });
        comboPlanificadores.setFont(new Font("Arial", Font.PLAIN, 12));
        comboPlanificadores.setBackground(Color.WHITE);
        comboPlanificadores.addActionListener(e -> cambiarPlanificador());
        panelCombo.add(comboPlanificadores);
        
        lblInfoPlanificador = new JLabel("Seleccione un algoritmo");
        lblInfoPlanificador.setFont(new Font("Arial", Font.ITALIC, 11));
        lblInfoPlanificador.setForeground(new Color(0, 100, 0));
        lblInfoPlanificador.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(panelCombo);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblInfoPlanificador);
        
        return panel;
    }
    
    private JPanel crearPanelVelocidad() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createTitledBorder("Velocidad"));
        
        JLabel lblVelocidad = new JLabel("Duración del Ciclo (ms):");
        lblVelocidad.setFont(new Font("Arial", Font.BOLD, 12));
        lblVelocidad.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        sliderVelocidad = new JSlider(100, 3000, 1000);
        sliderVelocidad.setMajorTickSpacing(1000);
        sliderVelocidad.setMinorTickSpacing(500);
        sliderVelocidad.setPaintTicks(true);
        sliderVelocidad.setPaintLabels(true);
        sliderVelocidad.setBackground(new Color(245, 245, 245));
        sliderVelocidad.addChangeListener(e -> cambiarVelocidad());
        
        JLabel lblValorVelocidad = new JLabel("1000 ms");
        lblValorVelocidad.setFont(new Font("Arial", Font.BOLD, 11));
        lblValorVelocidad.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblValorVelocidad.setForeground(Color.BLUE);
        
        sliderVelocidad.addChangeListener(e -> {
            lblValorVelocidad.setText(sliderVelocidad.getValue() + " ms");
        });
        
        panel.add(lblVelocidad);
        panel.add(Box.createVerticalStrut(5));
        panel.add(sliderVelocidad);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblValorVelocidad);
        
        return panel;
    }
    
    private JPanel crearPanelEstado() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createTitledBorder("Estado del Sistema"));
        
        lblEstadoSimulacion = new JLabel("SIMULACIÓN: DETENIDA");
        lblEstadoSimulacion.setFont(new Font("Arial", Font.BOLD, 14));
        lblEstadoSimulacion.setForeground(Color.RED);
        lblEstadoSimulacion.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblInfoMemoria = new JLabel("Memoria: 0/10 procesos");
        lblInfoMemoria.setFont(new Font("Arial", Font.ITALIC, 10));
        lblInfoMemoria.setForeground(Color.DARK_GRAY);
        lblInfoMemoria.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(lblEstadoSimulacion);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblInfoMemoria);
        
        return panel;
    }
    
    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.BOLD, 12));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createRaisedBevelBorder());
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(220, 40));
        
        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });
        
        return boton;
    }
    
    private JScrollPane crearPanelLog() {
        JPanel panelLog = new JPanel(new BorderLayout());
        panelLog.setBorder(BorderFactory.createTitledBorder("Log de Eventos del Sistema"));
        
        txtLog = new JTextArea(25, 40);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 11));
        txtLog.setBackground(new Color(253, 246, 227));
        txtLog.setForeground(Color.BLACK);
        txtLog.setMargin(new Insets(5, 5, 5, 5));
        
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // Panel de botones para el log
        JPanel panelBotonesLog = new JPanel(new FlowLayout());
        panelBotonesLog.setBackground(new Color(245, 245, 245));
        
        JButton btnLimpiarLog = new JButton("Limpiar Log");
        btnLimpiarLog.setBackground(new Color(120, 120, 120));
        btnLimpiarLog.setForeground(Color.WHITE);
        btnLimpiarLog.addActionListener(e -> limpiarLog());
        
        JButton btnExportarLog = new JButton("Exportar Log");
        btnExportarLog.setBackground(new Color(46, 125, 50));
        btnExportarLog.setForeground(Color.WHITE);
        btnExportarLog.addActionListener(e -> exportarLog());
        
        panelBotonesLog.add(btnLimpiarLog);
        panelBotonesLog.add(btnExportarLog);
        
        panelLog.add(scrollLog, BorderLayout.CENTER);
        panelLog.add(panelBotonesLog, BorderLayout.SOUTH);
        
        return scrollLog;
    }
    
    private void iniciarSimulacion() {
        try {
            nucleo.iniciarSimulacion();
            actualizarEstadoBoton();
            agregarEventoLog("🟢 SIMULACIÓN INICIADA - Ciclo: " + nucleo.getCicloActual());
        } catch (Exception e) {
            mostrarError("Error al iniciar simulación: " + e.getMessage());
        }
    }
    
    private void pausarSimulacion() {
        try {
            nucleo.pausarSimulacion();
            actualizarEstadoBoton();
            agregarEventoLog("⏸️ SIMULACIÓN PAUSADA - Ciclo: " + nucleo.getCicloActual());
        } catch (Exception e) {
            mostrarError("Error al pausar simulación: " + e.getMessage());
        }
    }
    
    private void reanudarSimulacion() {
        try {
            nucleo.reanudarSimulacion();
            actualizarEstadoBoton();
            agregarEventoLog("🔵 SIMULACIÓN REANUDADA - Ciclo: " + nucleo.getCicloActual());
        } catch (Exception e) {
            mostrarError("Error al reanudar simulación: " + e.getMessage());
        }
    }
    
    private void detenerSimulacion() {
        try {
            int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea detener la simulación?\nSe perderá el progreso actual.",
                "Confirmar Detención", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                nucleo.shutdown();
                nucleo = NucleoSistema.getInstance();
                if (panelGraficas != null) {
                    panelGraficas.limpiar();
                }
                actualizarEstadoBoton();
                agregarEventoLog("🔴 SIMULACIÓN DETENIDA - Sistema reiniciado");
                JOptionPane.showMessageDialog(this, 
                    "Simulación detenida y sistema reiniciado", 
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            mostrarError("Error al detener simulación: " + e.getMessage());
        }
    }
    
    private void limpiarTodo() {
        try {
            int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea limpiar todo?\nSe eliminarán todos los procesos y métricas.",
                "Confirmar Limpieza Total", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                nucleo.shutdown();
                nucleo = NucleoSistema.getInstance();
                if (panelGraficas != null) {
                    panelGraficas.limpiar();
                }
                limpiarLog();
                actualizarEstadoBoton();
                agregarEventoLog("🧹 SISTEMA LIMPIADO - Todo reiniciado");
                JOptionPane.showMessageDialog(this, 
                    "Sistema limpiado completamente", 
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            mostrarError("Error al limpiar sistema: " + e.getMessage());
        }
    }
    
    private void limpiarLog() {
        if (txtLog != null) {
            txtLog.setText("");
        }
        Logger.getInstance().limpiarLog();
    }
    
    private void exportarLog() {
        try {
            JOptionPane.showMessageDialog(this, 
                "Función de exportación implementada\nEl log se guardaría en archivo logs/simulador.log", 
                "Exportar Log", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            mostrarError("Error al exportar log: " + e.getMessage());
        }
    }
    
    private void cambiarPlanificador() {
        try {
            String seleccion = (String) comboPlanificadores.getSelectedItem();
            String tipo = "";
            String descripcion = "";
            
            switch (seleccion) {
                case "FCFS": 
                    tipo = "FCFS";
                    descripcion = "🏷️ First-Come, First-Served - Por orden de llegada";
                    break;
                case "SPN": 
                    tipo = "SPN";
                    descripcion = "⚡ Shortest Process Next - El proceso más corto primero";
                    break;
                case "SRT": 
                    tipo = "SRT";
                    descripcion = "🎯 Shortest Remaining Time - Menor tiempo restante primero";
                    break;
                case "Round Robin": 
                    tipo = "RR";
                    descripcion = "🔄 Round Robin - Tiempos compartidos con quantum";
                    break;
                case "HRRN": 
                    tipo = "HRRN";
                    descripcion = "📊 Highest Response Ratio Next - Mayor ratio de respuesta";
                    break;
                case "Prioridades": 
                    tipo = "PRIORIDADES";
                    descripcion = "⭐ Por Prioridades - Mayor prioridad primero";
                    break;
            }
            
            nucleo.cambiarPlanificador(tipo);
            if (lblInfoPlanificador != null) {
                lblInfoPlanificador.setText(descripcion);
            }
            agregarEventoLog("🔄 CAMBIO DE PLANIFICADOR: " + seleccion);
            
        } catch (Exception e) {
            mostrarError("Error al cambiar planificador: " + e.getMessage());
        }
    }
    
    private void cambiarVelocidad() {
        try {
            int velocidad = sliderVelocidad.getValue();
            nucleo.setDuracionCiclo(velocidad);
        } catch (Exception e) {
            mostrarError("Error al cambiar velocidad: " + e.getMessage());
        }
    }
    
    private void actualizarEstadoBoton() {
        boolean activa = nucleo.isSimulacionActiva();
        
        if (btnIniciar != null) btnIniciar.setEnabled(!activa);
        if (btnPausar != null) btnPausar.setEnabled(activa);
        if (btnReanudar != null) btnReanudar.setEnabled(!activa);
        if (btnDetener != null) btnDetener.setEnabled(true);
        if (btnLimpiarTodo != null) btnLimpiarTodo.setEnabled(true);
        
        if (lblEstadoSimulacion != null) {
            if (activa) {
                lblEstadoSimulacion.setText("SIMULACIÓN: EJECUTÁNDOSE");
                lblEstadoSimulacion.setForeground(new Color(0, 128, 0));
            } else {
                lblEstadoSimulacion.setText("SIMULACIÓN: DETENIDA");
                lblEstadoSimulacion.setForeground(Color.RED);
            }
        }
    }
    
    private void actualizarInterfaz() {
        try {
            // Actualizar información general
            if (lblCicloGlobal != null) {
                lblCicloGlobal.setText("Ciclo Global: " + nucleo.getCicloActual());
            }
            if (lblProcesosActivos != null) {
                lblProcesosActivos.setText("Procesos Activos: " + nucleo.getGestorColas().getTotalProcesos());
            }
            
            // Actualizar todos los paneles
            if (panelEjecucion != null) panelEjecucion.actualizar();
            if (panelColas != null) panelColas.actualizar();
            if (panelMetricas != null) panelMetricas.actualizar();
            if (panelEstados != null) panelEstados.actualizar();
            if (panelGraficas != null) panelGraficas.actualizar();
            
            // Actualizar estado del sistema operativo
            boolean ejecutandoSO = nucleo.isEjecutandoSO();
            if (panelEjecucion != null) {
                panelEjecucion.actualizarEstadoSO(ejecutandoSO);
            }
            
            // Actualizar información específica del planificador
            actualizarInfoEspecificaPlanificador();
            
            // Actualizar información de memoria
            actualizarInfoMemoria();
            
            // Actualizar log
            actualizarLog();
            
        } catch (Exception e) {
            System.err.println("Error actualizando interfaz: " + e.getMessage());
        }
    }
    
    private void actualizarInfoEspecificaPlanificador() {
        if (lblInfoPlanificador != null) {
            String textoBase = lblInfoPlanificador.getText().split(" \\| ")[0];
            
            if (nucleo.getPlanificadorActual() instanceof RoundRobinPlanificador) {
                String infoExtra = " | Quantum: " + nucleo.getContadorQuantum() + 
                                  "/" + nucleo.getConfiguracion().getQuantumRR();
                lblInfoPlanificador.setText(textoBase + infoExtra);
            } else {
                lblInfoPlanificador.setText(textoBase);
            }
        }
    }
    
    private void actualizarInfoMemoria() {
        // Buscar el label de memoria en el panel de estado
        if (lblEstadoSimulacion != null && lblEstadoSimulacion.getParent() != null) {
            Component[] componentes = ((JPanel)lblEstadoSimulacion.getParent()).getComponents();
            for (Component comp : componentes) {
                if (comp instanceof JLabel && ((JLabel)comp).getText().contains("Memoria")) {
                    int procesosMemoria = nucleo.getGestorColas().getColaListos().size() + 
                                        nucleo.getGestorColas().getColaBloqueados().size();
                    ((JLabel)comp).setText("Memoria: " + procesosMemoria + "/" + 
                        nucleo.getConfiguracion().getMaxProcesosMemoria() + " procesos");
                    break;
                }
            }
        }
    }
    
    private void actualizarLog() {
        if (txtLog == null) return;
        
        java.util.List<Evento> eventos = Logger.getInstance().getEventos();
        int maxEventos = 40;
        
        if (eventos.size() > maxEventos) {
            eventos = eventos.subList(eventos.size() - maxEventos, eventos.size());
        }
        
        StringBuilder logContent = new StringBuilder();
        for (Evento evento : eventos) {
            logContent.append(evento.getMensaje()).append("\n");
        }
        
        String nuevoLog = logContent.toString();
        if (!nuevoLog.equals(txtLog.getText())) {
            txtLog.setText(nuevoLog);
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        }
    }
    
    private void agregarEventoLog(String mensaje) {
        Logger.getInstance().logEvento(new Evento("INTERFAZ", mensaje, null));
        actualizarLog();
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        agregarEventoLog("❌ ERROR: " + mensaje);
    }
    
    public void agregarProceso(Proceso proceso) {
        try {
            nucleo.agregarProceso(proceso);
            agregarEventoLog("➕ NUEVO PROCESO: " + proceso.getNombre() + 
                           " (Instrucciones: " + proceso.getTotalInstrucciones() + 
                           ", Tipo: " + proceso.getTipo() + ")");
        } catch (Exception e) {
            mostrarError("Error al agregar proceso: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        // LookAndFeel seguro
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception ex) {
                    System.err.println("No se pudo establecer ningún look and feel: " + ex.getMessage());
                }
            }
            
            InterfazPrincipal interfaz = new InterfazPrincipal();
            interfaz.setVisible(true);
        });
    }
}