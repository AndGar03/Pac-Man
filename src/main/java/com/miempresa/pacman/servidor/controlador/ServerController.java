package com.miempresa.pacman.servidor.controlador;

import com.miempresa.pacman.dao.IJugadorDAO;
import com.miempresa.pacman.servidor.vista.GamePanel;
import com.miempresa.pacman.servidor.vista.ServerWindow;
import com.miempresa.pacman.util.Configuracion;

import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Controlador principal del servidor.
 * 
 * <p>Esta clase coordina entre el modelo, la vista y maneja la lógica
 * del servidor. Sigue el patrón MVC y los principios SOLID:
 * - SRP: Coordina el servidor, no contiene lógica de negocio del juego
 * - DIP: Depende de la abstracción IJugadorDAO, no de la implementación
 * 
 * @author Equipo de Desarrollo
 * @version 1.0
 */
public class ServerController {
    
    private ServerWindow vista;
    private ServerSocket serverSocket;
    private IJugadorDAO jugadorDAO;
    private boolean servidorActivo;
    private final List<ClientHandler> clientesActivos;
    private final ManejadorResultados manejadorResultados;
    
    private static final String RUTA_PROPERTIES = "src/main/resources/data/server.properties";
    private static final Dimension AREA_JUEGO = new Dimension(800, 600);
    
    /**
     * Constructor del controlador del servidor.
     */
    public ServerController() {
        this.clientesActivos = new ArrayList<>();
        this.manejadorResultados = new ManejadorResultados();
        this.servidorActivo = false;
    }
    
    /**
     * Inicia el servidor y la aplicación.
     */
    public void iniciarServidor() {
        SwingUtilities.invokeLater(() -> {
            // Inicializar vista
            vista = new ServerWindow();
            configurarEventos();
            vista.mostrar();
            
            // Cargar configuración
            Properties props = Configuracion.cargarPropiedades(RUTA_PROPERTIES);
            String puertoStr = Configuracion.obtenerPropiedad(props, "server.port", "8888");
            String dbUrl = Configuracion.obtenerPropiedad(props, "db.url", "jdbc:mysql://localhost:3306/pacman_db");
            String dbUser = Configuracion.obtenerPropiedad(props, "db.user", "root");
            String dbPassword = Configuracion.obtenerPropiedad(props, "db.password", "root");
            
            // Cargar driver de MySQL
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.err.println("Error: No se encontró el driver de MySQL. Asegúrate de tener mysql-connector-j en el classpath.");
            }
            
            // Inicializar DAO (Dependency Injection)
            jugadorDAO = new com.miempresa.pacman.dao.JugadorDAOImpl(dbUrl, dbUser, dbPassword);
            
            // Cargar usuarios desde properties
            jugadorDAO.cargarUsuariosDesdeProperties(RUTA_PROPERTIES);
            
            // Iniciar servidor en hilo separado
            new Thread(this::iniciarServerSocket).start();
        });
    }
    
    /**
     * Configura los eventos de la vista.
     */
    private void configurarEventos() {
        vista.getBtnSalir().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarServidor();
            }
        });
    }
    
    /**
     * Inicia el ServerSocket y acepta conexiones de clientes.
     */
    private void iniciarServerSocket() {
        try {
            Properties props = Configuracion.cargarPropiedades(RUTA_PROPERTIES);
            int puerto = Integer.parseInt(Configuracion.obtenerPropiedad(props, "server.port", "8888"));
            
            serverSocket = new ServerSocket(puerto);
            servidorActivo = true;
            
            System.out.println("Servidor iniciado en puerto " + puerto);
            
            GamePanel gamePanel = vista.getGamePanel();
            
            while (servidorActivo) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clienteSocket.getInetAddress());
                
                // Crear hilo para manejar el cliente
                ClientHandler handler = new ClientHandler(clienteSocket, jugadorDAO, gamePanel, AREA_JUEGO);
                clientesActivos.add(handler);
                handler.start();
            }
            
        } catch (Exception e) {
            if (servidorActivo) {
                System.err.println("Error en servidor: " + e.getMessage());
            }
        }
    }
    
    /**
     * Cierra el servidor y muestra el mejor resultado.
     */
    private void cerrarServidor() {
        servidorActivo = false;
        
        try {
            // Cerrar todos los clientes activos
            for (ClientHandler handler : clientesActivos) {
                if (handler.isAlive()) {
                    handler.interrupt();
                }
            }
            
            // Cerrar ServerSocket
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            
            // Leer resultados y encontrar el mejor
            List<ManejadorResultados.ResultadoJuego> resultados = manejadorResultados.leerTodosLosResultados();
            
            if (resultados.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(
                    vista,
                    "No hay resultados guardados.",
                    "Resultados",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                ManejadorResultados.ResultadoJuego mejor = encontrarMejorJugador(resultados);
                javax.swing.JOptionPane.showMessageDialog(
                    vista,
                    "Mejor Jugador:\n" +
                    "Nombre: " + mejor.getNombreJugador() + "\n" +
                    "Puntaje: " + mejor.getPuntaje() + "\n" +
                    "Tiempo: " + mejor.getTiempoSegundos() + " segundos",
                    "Mejor Jugador",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
                );
            }
            
            System.exit(0);
            
        } catch (Exception e) {
            System.err.println("Error al cerrar servidor: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Encuentra el mejor jugador (mayor puntaje, menor tiempo en caso de empate).
     * 
     * @param resultados Lista de resultados
     * @return El mejor resultado
     */
    private ManejadorResultados.ResultadoJuego encontrarMejorJugador(
            List<ManejadorResultados.ResultadoJuego> resultados) {
        
        ManejadorResultados.ResultadoJuego mejor = resultados.get(0);
        
        for (ManejadorResultados.ResultadoJuego resultado : resultados) {
            if (resultado.getPuntaje() > mejor.getPuntaje() ||
                (resultado.getPuntaje() == mejor.getPuntaje() && 
                 resultado.getTiempoSegundos() < mejor.getTiempoSegundos())) {
                mejor = resultado;
            }
        }
        
        return mejor;
    }
}

