/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author joelgascon
 */
package utils;

import java.util.ArrayList;
import java.util.List;

public class ListaPersonalizada<T> {
    private List<T> elementos;
    
    public ListaPersonalizada() {
        this.elementos = new ArrayList<>();
    }
    
    public void agregar(T elemento) {
        elementos.add(elemento);
    }
    
    public T obtener(int indice) {
        if (indice >= 0 && indice < elementos.size()) {
            return elementos.get(indice);
        }
        return null;
    }
    
    public void eliminar(int indice) {
        if (indice >= 0 && indice < elementos.size()) {
            elementos.remove(indice);
        }
    }
    
    public void eliminar(T elemento) {
        elementos.remove(elemento);
    }
    
    public boolean contiene(T elemento) {
        return elementos.contains(elemento);
    }
    
    public int tamano() {
        return elementos.size();
    }
    
    public List<T> toList() {
        return new ArrayList<>(elementos);
    }
    
    public void limpiar() {
        elementos.clear();
    }
}
