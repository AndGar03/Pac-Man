package udistrital.avanzada.pacman.servidor.controlador;

import udistrital.avanzada.pacman.dao.IJugadorDAO;
import udistrital.avanzada.pacman.servidor.vista.PanelJuego;
import udistrital.avanzada.pacman.servidor.vista.VentanaServidor;
import udistrital.avanzada.pacman.util.Configuracion;

import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
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
 * @author And_Gar03
 * @version 1.0
 */
public class ControlServidor {
    
    private udistrital.avanzada.pacman.servidor.vista.VentanaServidor vista;
    private ServerSocket serverSocket;
    private IJugadorDAO jugadorDAO;
    private boolean servidorActivo;
    private final List<ManejadorCliente> clientesActivos;
    private final ManejadorResultados manejadorResultados;
    
    private static final String RUTA_PROPERTIES = "src/data/server.properties";
    private static final Dimension AREA_JUEGO = new Dimension(800, 600);
    
    /**
     * Constructor del controlador del servidor.
     */
    public ControlServidor() {
        this.clientesActivos = new ArrayList<>();
        this.manejadorResultados = new ManejadorResultados();
        this.servidorActivo = false;
    }
    
    /**
     * Inicia el servidor y la aplicación.
     */
    public void iniciarServidor() {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Iniciando servidor...");
                
                // Inicializar vista
                System.out.println("Creando ventana...");
                vista = new udistrital.avanzada.pacman.servidor.vista.VentanaServidor();
                configurarEventos();
                System.out.println("Mostrando ventana...");
                vista.mostrar();
                System.out.println("Ventana mostrada.");
                
                // Cargar configuración
                System.out.println("Cargando configuración...");
                Properties props = Configuracion.cargarPropiedades(RUTA_PROPERTIES);
                String puertoStr = Configuracion.obtenerPropiedad(props, "socket.port", "9090");
                String dbUrl = Configuracion.obtenerPropiedad(props, "db.url", "jdbc:mysql://localhost:3306/pacman_db");
                String dbUser = Configuracion.obtenerPropiedad(props, "db.user", "root");
                String dbPassword = Configuracion.obtenerPropiedad(props, "db.password", "TomateRojo");
                
                System.out.println("Configuración cargada:");
                System.out.println("  Puerto servidor: " + puertoStr);
                System.out.println("  URL MySQL: " + dbUrl);
                System.out.println("  Usuario MySQL: " + dbUser);
                
                // Cargar driver de MySQL
                System.out.println("Cargando driver MySQL...");
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    System.out.println("Driver MySQL cargado correctamente.");
                } catch (ClassNotFoundException e) {
                    System.err.println("ERROR: No se encontró el driver de MySQL. Asegúrate de tener mysql-connector-j en el classpath.");
                    javax.swing.JOptionPane.showMessageDialog(null, 
                        "Error: No se encontró el driver de MySQL.\nAsegúrate de tener mysql-connector-j en el classpath.",
                        "Error de Configuración", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Probar conexión antes de continuar
                try (Connection cx = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                    System.out.println("Conexión a MySQL exitosa.");
                } catch (Exception e) {
                    System.err.println("ERROR: No fue posible conectar a MySQL: " + e.getMessage());
                    javax.swing.JOptionPane.showMessageDialog(
                        vista,
                        "No fue posible conectar a MySQL con la configuración actual.\n" +
                        "URL: " + dbUrl + "\nUsuario: " + dbUser + "\n" +
                        "Detalle: " + e.getMessage(),
                        "Error de Conexión a BD",
                        javax.swing.JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                
                // Inicializar DAO (Dependency Injection)
                System.out.println("Inicializando DAO...");
                jugadorDAO = new udistrital.avanzada.pacman.dao.JugadorDAOImpl(dbUrl, dbUser, dbPassword);
                
                // Cargar usuarios desde properties
                System.out.println("Cargando usuarios desde properties...");
                try {
                    jugadorDAO.cargarUsuariosDesdeProperties(RUTA_PROPERTIES);
                    System.out.println("Usuarios cargados correctamente.");
                } catch (Exception e) {
                    System.err.println("ERROR al cargar usuarios: " + e.getMessage());
                    e.printStackTrace();
                    javax.swing.JOptionPane.showMessageDialog(vista, 
                        "Error al cargar usuarios desde la base de datos:\n" + e.getMessage(),
                        "Error de Base de Datos", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
                
                // Iniciar servidor en hilo separado
                System.out.println("Iniciando servidor de sockets...");
                new Thread(this::iniciarServerSocket).start();
                
            } catch (Exception e) {
                System.err.println("ERROR CRÍTICO al iniciar servidor: " + e.getMessage());
                e.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(null, 
                    "Error crítico al iniciar el servidor:\n" + e.getMessage(),
                    "Error Crítico", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
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
            int puerto = Integer.parseInt(Configuracion.obtenerPropiedad(props, "socket.port", "9090"));
            
            serverSocket = new ServerSocket(puerto);
            servidorActivo = true;
            
            System.out.println("Servidor iniciado en puerto " + puerto);
            
            PanelJuego gamePanel = vista.getGamePanel();
            
            while (servidorActivo) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clienteSocket.getInetAddress());
                
                // Crear hilo para manejar el cliente
                ManejadorCliente handler = new ManejadorCliente(clienteSocket, jugadorDAO, gamePanel, AREA_JUEGO);
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
            for (ManejadorCliente handler : clientesActivos) {
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

