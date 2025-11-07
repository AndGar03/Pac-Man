package com.miempresa.pacman.cliente.controlador;

/**
 * Punto de entrada principal de la aplicación cliente.
 * 
 * <p>Esta clase tiene una única responsabilidad: iniciar la aplicación cliente.
 * Sigue estrictamente el principio de responsabilidad única (SRP).
 * No contiene ninguna lógica de negocio, solo el punto de entrada.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0
 */
public class ClientLauncher {
    
    /**
     * Método principal que inicia la aplicación cliente.
     * 
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        new ClientController().iniciarCliente();
    }
}

