package udistrital.avanzada.pacman.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Utilidad para manejar operaciones de lectura y escritura en sockets.
 * 
 * <p>Esta clase encapsula las operaciones de I/O de sockets, siguiendo
 * el principio de responsabilidad única (SRP).
 * 
 * @author SanSantax
 * @version 1.0
 */
public class ManejadorSockets {
    
    private final Socket socket;
    private final BufferedReader entrada;
    private final PrintWriter salida;
    
    /**
     * Constructor que inicializa los streams de entrada y salida.
     * 
     * @param socket El socket a manejar
     * @throws IOException Si hay un error al crear los streams
     */
    public ManejadorSockets(Socket socket) throws IOException {
        this.socket = socket;
        this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.salida = new PrintWriter(socket.getOutputStream(), true);
    }
    
    /**
     * Lee una línea del socket.
     * 
     * @return La línea leída, o null si hay un error
     */
    public String leerLinea() {
        try {
            return entrada.readLine();
        } catch (IOException e) {
            System.err.println("Error al leer del socket: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Escribe una línea en el socket.
     * 
     * @param mensaje El mensaje a enviar
     */
    public void escribirLinea(String mensaje) {
        salida.println(mensaje);
    }
    
    /**
     * Cierra los streams y el socket.
     */
    public void cerrar() {
        try {
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar socket: " + e.getMessage());
        }
    }
    
    /**
     * Verifica si el socket está cerrado.
     * 
     * @return true si el socket está cerrado, false en caso contrario
     */
    public boolean estaCerrado() {
        return socket == null || socket.isClosed();
    }
}

