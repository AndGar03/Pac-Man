package com.miempresa.pacman.servidor.modelo;

/**
 * Representa un jugador en el sistema.
 * 
 * <p>Esta clase es un modelo de datos puro que sigue el principio
 * de responsabilidad única (SRP). No contiene lógica de negocio ni I/O.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0
 */
public class Jugador {
    
    private final int id;
    private final String nombre;
    
    /**
     * Constructor de Jugador.
     * 
     * @param id El identificador único del jugador
     * @param nombre El nombre de usuario del jugador
     */
    public Jugador(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    
    /**
     * Obtiene el identificador del jugador.
     * 
     * @return El id del jugador
     */
    public int getId() {
        return id;
    }
    
    /**
     * Obtiene el nombre del jugador.
     * 
     * @return El nombre del jugador
     */
    public String getNombre() {
        return nombre;
    }
    
    @Override
    public String toString() {
        return "Jugador{id=" + id + ", nombre='" + nombre + "'}";
    }
}

