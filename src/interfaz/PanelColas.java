/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz;

import modelo.Proceso;
import nucleo.NucleoSistema;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelColas extends JPanel {
    private JList<String> listaListos;
    private JList<String> listaBloqueados;
    private JList<String> listaSuspendidos;
    private JList<String> listaTerminados;
    
    private DefaultListModel<String> modelListos;
    private DefaultListModel<String> modelBloqueados;
    private DefaultListModel<String> modelSuspendidos;
    private DefaultListModel<String> modelTerminados;
    
    public PanelColas() {
        inicializarPanel();
    }
    
    private void inicializarPanel() {
        setLayout(new GridLayout(2, 2));
        setBorder(BorderFactory.createTitledBorder("Colas de Procesos"));
        
        // Inicializar modelos
        modelListos = new DefaultListModel<>();
        modelBloqueados = new DefaultListModel<>();
        modelSuspendidos = new DefaultListModel<>();
        modelTerminados = new DefaultListModel<>();
        
        // Crear listas
        listaListos = new JList<>(modelListos);
        listaBloqueados = new JList<>(modelBloqueados);
        listaSuspendidos = new JList<>(modelSuspendidos);
        listaTerminados = new JList<>(modelTerminados);
        
        // Configurar scroll panes
        JScrollPane scrollListos = new JScrollPane(listaListos);
        JScrollPane scrollBloqueados = new JScrollPane(listaBloqueados);
        JScrollPane scrollSuspendidos = new JScrollPane(listaSuspendidos);
        JScrollPane scrollTerminados = new JScrollPane(listaTerminados);
        
        scrollListos.setBorder(BorderFactory.createTitledBorder("Listos"));
        scrollBloqueados.setBorder(BorderFactory.createTitledBorder("Bloqueados"));
        scrollSuspendidos.setBorder(BorderFactory.createTitledBorder("Suspendidos"));
        scrollTerminados.setBorder(BorderFactory.createTitledBorder("Terminados"));
        
        add(scrollListos);
        add(scrollBloqueados);
        add(scrollSuspendidos);
        add(scrollTerminados);
    }
    
    public void actualizar() {
        NucleoSistema nucleo = NucleoSistema.getInstance();
        
        // Limpiar modelos
        modelListos.clear();
        modelBloqueados.clear();
        modelSuspendidos.clear();
        modelTerminados.clear();
        
        // Actualizar lista de listos
        List<Proceso> listos = nucleo.getGestorColas().getColaListos();
        for (Proceso p : listos) {
            modelListos.addElement(p.toString());
        }
        
        // Actualizar lista de bloqueados
        List<Proceso> bloqueados = nucleo.getGestorColas().getColaBloqueados();
        for (Proceso p : bloqueados) {
            modelBloqueados.addElement(p.toString());
        }
        
        // Actualizar lista de suspendidos
        List<Proceso> suspendidosListos = nucleo.getGestorColas().getColaListosSuspendidos();
        List<Proceso> suspendidosBloqueados = nucleo.getGestorColas().getColaBloqueadosSuspendidos();
        for (Proceso p : suspendidosListos) {
            modelSuspendidos.addElement("[Listo Suspendido] " + p.toString());
        }
        for (Proceso p : suspendidosBloqueados) {
            modelSuspendidos.addElement("[Bloqueado Suspendido] " + p.toString());
        }
        
        // Actualizar lista de terminados
        List<Proceso> terminados = nucleo.getGestorColas().getProcesosTerminados();
        for (Proceso p : terminados) {
            modelTerminados.addElement(p.toString() + " - Tiempo: " + p.getTiempoEjecucion());
        }
    }
}
