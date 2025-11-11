package com.miempresa.pacman.servidor.controlador;

import com.miempresa.pacman.dao.IJugadorDAO;
import com.miempresa.pacman.servidor.modelo.Juego;
import com.miempresa.pacman.servidor.modelo.Jugador;
import com.miempresa.pacman.servidor.modelo.PacMan;
import com.miempresa.pacman.servidor.vista.GamePanel;
import com.miempresa.pacman.util.ManejadorSockets;

import java.awt.Dimension;
import java.net.Socket;

/**
 * Maneja la comunicación con un cliente individual en un hilo separado.
 * 
 * <p>Esta clase es responsable de:
 * - Autenticación del cliente
 * - Gestión del juego para ese cliente
 * - Comunicación bidireccional
 * 
 * Sigue el principio de responsabilidad única (SRP) y permite que el servidor
 * maneje múltiples clientes concurrentemente.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0
 */
public class ClientHandler extends Thread {
    
    private final Socket clienteSocket;
    private final IJugadorDAO jugadorDAO;
    private final GamePanel gamePanel;
    private final ManejadorSockets manejadorSockets;
    private final ManejadorResultados manejadorResultados;
    private final Dimension areaJuego;
    
    private Juego juego;
    private Jugador jugador;
    
    /**
     * Constructor del manejador de cliente.
     * 
     * @param clienteSocket El socket del cliente
     * @param jugadorDAO El DAO para validar credenciales
     * @param gamePanel El panel de juego para actualizar
     * @param areaJuego Las dimensiones del área de juego
     */
    public ClientHandler(Socket clienteSocket, IJugadorDAO jugadorDAO, 
                        GamePanel gamePanel, Dimension areaJuego) {
        this.clienteSocket = clienteSocket;
        this.jugadorDAO = jugadorDAO;
        this.gamePanel = gamePanel;
        this.areaJuego = areaJuego;
        this.manejadorResultados = new ManejadorResultados();
        
        try {
            this.manejadorSockets = new ManejadorSockets(clienteSocket);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear manejador de sockets", e);
        }
    }
    
    @Override
    public void run() {
        try {
            // Paso 1: Autenticación
            if (!autenticarCliente()) {
                return;
            }
            
            // Paso 2: Inicializar juego
            inicializarJuego();
            
            // Paso 3: Bucle principal del juego
            ejecutarJuego();
            
        } catch (Exception e) {
            System.err.println("Error en ClientHandler: " + e.getMessage());
        } finally {
            manejadorSockets.cerrar();
        }
    }
    
    /**
     * Autentica al cliente solicitando usuario y contraseña.
     * 
     * @return true si la autenticación fue exitosa, false en caso contrario
     */
    private boolean autenticarCliente() {
        try {
            // Solicitar usuario
            manejadorSockets.escribirLinea("Usuario:");
            String usuario = manejadorSockets.leerLinea();
            if (usuario != null) usuario = usuario.trim();
            
            // Solicitar contraseña
            manejadorSockets.escribirLinea("Contraseña:");
            String contraseña = manejadorSockets.leerLinea();
            if (contraseña != null) contraseña = contraseña.trim();
            
            if (usuario == null || usuario.isEmpty() || contraseña == null || contraseña.isEmpty()) {
                manejadorSockets.escribirLinea("Credenciales inválidas");
                return false;
            }
            
            // Validar credenciales
            jugador = jugadorDAO.validarCredenciales(usuario, contraseña);
            
            if (jugador == null) {
                manejadorSockets.escribirLinea("Credenciales inválidas");
                return false;
            }
            
            manejadorSockets.escribirLinea("Autenticación exitosa. Bienvenido " + jugador.getNombre());
            return true;
            
        } catch (Exception e) {
            System.err.println("Error en autenticación: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Inicializa un nuevo juego para este cliente.
     */
    private void inicializarJuego() {
        juego = new Juego(areaJuego);
        actualizarVista();
        manejadorSockets.escribirLinea("Juego iniciado. Envía comandos: arriba, abajo, izquierda, derecha");
    }
    
    /**
     * Ejecuta el bucle principal del juego.
     */
    private void ejecutarJuego() {
        while (!juego.isJuegoTerminado() && !manejadorSockets.estaCerrado()) {
            String comando = manejadorSockets.leerLinea();
            
            if (comando == null || comando.equalsIgnoreCase("salir")) {
                break;
            }
            
            // Procesar movimiento
            PacMan.Direccion direccion = PacMan.parsearDireccion(comando);
            
            if (direccion == null) {
                manejadorSockets.escribirLinea("Comando inválido. Usa: arriba, abajo, izquierda, derecha");
                continue;
            }
            
            Juego.ResultadoMovimiento resultado = juego.procesarMovimiento(direccion);
            actualizarVista();
            
            // Enviar respuesta al cliente
            manejadorSockets.escribirLinea(resultado.getMensaje());
            
            // Si el juego terminó
            if (juego.isJuegoTerminado()) {
                // Guardar resultado
                int puntaje = juego.getPuntaje();
                long tiempo = juego.getTiempoTranscurrido();
                manejadorResultados.guardarResultado(jugador.getNombre(), puntaje, tiempo);
                
                // Enviar resultado final
                manejadorSockets.escribirLinea("FIN_JUEGO:Puntaje=" + puntaje + ",Tiempo=" + tiempo + "s");
                break;
            }
        }
    }
    
    /**
     * Actualiza la vista con el estado actual del juego.
     */
    private void actualizarVista() {
        if (juego != null) {
            gamePanel.actualizarEstado(juego.getPacMan(), juego.getItems());
        }
    }
}

