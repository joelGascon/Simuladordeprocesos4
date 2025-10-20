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

public class ColaPersonalizada<T> {
    private List<T> elementos;
    
    public ColaPersonalizada() {
        this.elementos = new ArrayList<>();
    }
    
    public void encolar(T elemento) {
        elementos.add(elemento);
    }
    
    public T desencolar() {
        if (elementos.isEmpty()) {
            return null;
        }
        return elementos.remove(0);
    }
    
    public T frente() {
        if (elementos.isEmpty()) {
            return null;
        }
        return elementos.get(0);
    }
    
    public boolean estaVacia() {
        return elementos.isEmpty();
    }
    
    public int tamano() {
        return elementos.size();
    }
    
    public boolean contiene(T elemento) {
        return elementos.contains(elemento);
    }
    
    public void eliminar(T elemento) {
        elementos.remove(elemento);
    }
    
    public List<T> toList() {
        return new ArrayList<>(elementos);
    }
    
    public void limpiar() {
        elementos.clear();
    }
}
