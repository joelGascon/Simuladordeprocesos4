/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package planificadores;

import modelo.Proceso;
import java.util.List;

public interface Planificador {
    Proceso seleccionarSiguiente();
    void agregarProceso(Proceso proceso);
    void ejecutarCiclo();
    String getNombre();
    String getDescripcion();
    List<Proceso> getColaListos();
}
