package udistrital.avanzada.pacman.servidor.controlador;

/**
 * Punto de entrada principal de la aplicación servidor.
 * 
 * <p>Esta clase tiene una única responsabilidad: iniciar la aplicación.
 * Sigue estrictamente el principio de responsabilidad única (SRP).
 * No contiene ninguna lógica de negocio, solo el punto de entrada.
 * 
 * @author And_Gar03
 * @version 1.0
 */
public class Launcher {
    
    /**
     * Método principal que inicia la aplicación servidor.
     * 
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        new ControlServidor().iniciarServidor();
    }
}

