/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nucleo;

import interfaz.InterfazPrincipal;
import javax.swing.SwingUtilities;

public class Simulador {
    public static void main(String[] args) {
        // Inicializar el núcleo del sistema
        NucleoSistema nucleo = NucleoSistema.getInstance();
        
        // Ejecutar la interfaz gráfica en el EDT
        SwingUtilities.invokeLater(() -> {
            InterfazPrincipal interfaz = new InterfazPrincipal();
            interfaz.setVisible(true);
        });
        
        // Agregar shutdown hook para limpieza
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            nucleo.shutdown();
        }));
    }
}