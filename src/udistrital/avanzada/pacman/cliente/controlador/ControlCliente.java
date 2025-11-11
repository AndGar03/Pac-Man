package udistrital.avanzada.pacman.cliente.controlador;

import udistrital.avanzada.pacman.cliente.modelo.EstadoConexion;
import udistrital.avanzada.pacman.cliente.vista.VentanaCliente;
import udistrital.avanzada.pacman.util.Configuracion;
import udistrital.avanzada.pacman.util.ManejadorSockets;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.net.Socket;
import java.util.Properties;

/**
 * Controlador principal del cliente.
 * 
 * <p>Esta clase coordina entre el modelo y la vista, y maneja la comunicación
 * con el servidor. Sigue el patrón MVC y los principios SOLID:
 * - SRP: Coordina el cliente y la comunicación
 * - DIP: Depende de abstracciones (ManejadorSockets)
 * 
 * @author SanSantax
 * @version 1.0
 */
public class ControlCliente {
    
    private final VentanaCliente vista;
    private final EstadoConexion modelo;
    private ManejadorSockets manejadorSockets;
    private Thread hiloReceptor;
    private boolean clienteActivo;
    
    /**
     * Constructor del controlador del cliente.
     */
    public ControlCliente() {
        this.vista = new VentanaCliente();
        this.modelo = new EstadoConexion();
        this.clienteActivo = false;
        configurarEventos();
    }
    
    /**
     * Configura los eventos de la vista.
     */
    private void configurarEventos() {
        // Botón conectar
        vista.getBtnConectar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!modelo.isConectado()) {
                    conectarAlServidor();
                } else {
                    desconectar();
                }
            }
        });
        
        // Botón enviar
        vista.getBtnEnviar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarComando();
            }
        });
        
        // Enter en el campo de comando
        vista.getCampoComando().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enviarComando();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }
    
    /**
     * Conecta al servidor usando un archivo de propiedades seleccionado por el usuario.
     */
    private void conectarAlServidor() {
        // Usar JFileChooser para seleccionar el archivo de propiedades
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo de configuración del cliente");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Archivos Properties", "properties"));
        fileChooser.setCurrentDirectory(new File("src/main/resources/data"));
        
        int resultado = fileChooser.showOpenDialog(vista);
        
        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        File archivoProperties = fileChooser.getSelectedFile();
        Properties props = Configuracion.cargarPropiedades(archivoProperties.getAbsolutePath());
        
        String serverIp = Configuracion.obtenerPropiedad(props, "server.ip", "localhost");
        String serverPortStr = Configuracion.obtenerPropiedad(props, "socket.port", "9090");
        
        try {
            int serverPort = Integer.parseInt(serverPortStr);
            
            // Conectar al servidor
            Socket socket = new Socket(serverIp, serverPort);
            manejadorSockets = new ManejadorSockets(socket);
            
            modelo.setConectado(true);
            vista.getBtnConectar().setText("Desconectar");
            vista.habilitarControles(false);
            vista.agregarMensaje("Conectado al servidor: " + serverIp + ":" + serverPort);
            
            // Iniciar hilo receptor único que maneja autenticación y juego
            clienteActivo = true;
            hiloReceptor = new Thread(this::procesarMensajesServidor);
            hiloReceptor.start();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                vista,
                "Error al conectar al servidor: " + e.getMessage(),
                "Error de Conexión",
                JOptionPane.ERROR_MESSAGE
            );
            vista.agregarMensaje("Error: " + e.getMessage());
        }
    }
    
    /**
     * Procesa todos los mensajes del servidor (autenticación y juego).
     * Este método se ejecuta en un hilo separado.
     */
    private void procesarMensajesServidor() {
        try {
            String mensaje;
            while ((mensaje = manejadorSockets.leerLinea()) != null && clienteActivo) {
                final String msg = mensaje;
                
                // Procesar mensajes de autenticación
                if (msg.startsWith("Usuario:") || msg.startsWith("Contraseña:")) {
                    try {
                        SwingUtilities.invokeAndWait(() -> vista.agregarMensaje("Servidor: " + msg));
                        
                        // Solicitar credenciales al usuario (bloquea hasta que el usuario responda)
                        final String[] credencial = new String[1];
                        SwingUtilities.invokeAndWait(() -> {
                            credencial[0] = JOptionPane.showInputDialog(vista, msg);
                        });
                        
                        if (credencial[0] != null && !credencial[0].isEmpty()) {
                            manejadorSockets.escribirLinea(credencial[0]);
                        } else {
                            desconectar();
                            break;
                        }
                    } catch (Exception e) {
                        vista.agregarMensaje("Error en autenticación: " + e.getMessage());
                        desconectar();
                        break;
                    }
                    continue;
                }
                
                // Procesar resultado de autenticación
                if (msg.contains("Autenticación exitosa")) {
                    SwingUtilities.invokeLater(() -> {
                        modelo.setAutenticado(true);
                        vista.agregarMensaje("Servidor: " + msg);
                        vista.habilitarControles(true);
                        vista.getCampoComando().requestFocus();
                    });
                    continue;
                }
                
                if (msg.contains("Credenciales inválidas")) {
                    SwingUtilities.invokeLater(() -> {
                        modelo.setAutenticado(false);
                        vista.agregarMensaje("Servidor: " + msg);
                        JOptionPane.showMessageDialog(
                            vista,
                            "Credenciales inválidas. La conexión se cerrará.",
                            "Error de Autenticación",
                            JOptionPane.ERROR_MESSAGE
                        );
                        desconectar();
                    });
                    break;
                }
                
                // Procesar mensajes del juego
                if (msg.startsWith("FIN_JUEGO:")) {
                    SwingUtilities.invokeLater(() -> {
                        String info = msg.substring(10);
                        vista.agregarMensaje("Servidor: ¡Juego terminado! " + info);
                        vista.habilitarControles(false);
                    });
                    desconectar();
                    break;
                }
                
                // Mostrar cualquier otro mensaje
                SwingUtilities.invokeLater(() -> vista.agregarMensaje("Servidor: " + msg));
            }
        } catch (Exception e) {
            if (clienteActivo) {
                SwingUtilities.invokeLater(() -> 
                    vista.agregarMensaje("Error en comunicación: " + e.getMessage()));
            }
        }
    }
    
    /**
     * Envía un comando al servidor.
     */
    private void enviarComando() {
        if (!modelo.isConectado() || !modelo.isAutenticado()) {
            vista.agregarMensaje("No estás conectado o autenticado");
            return;
        }
        
        String comando = vista.getCampoComando().getText().trim();
        
        if (comando.isEmpty()) {
            return;
        }
        
        if (manejadorSockets != null && !manejadorSockets.estaCerrado()) {
            manejadorSockets.escribirLinea(comando);
            vista.agregarMensaje("Cliente: " + comando);
            vista.getCampoComando().setText("");
        } else {
            vista.agregarMensaje("Error: No hay conexión con el servidor");
        }
    }
    
    /**
     * Desconecta del servidor.
     */
    private void desconectar() {
        clienteActivo = false;
        modelo.setConectado(false);
        modelo.setAutenticado(false);
        
        if (hiloReceptor != null && hiloReceptor.isAlive()) {
            hiloReceptor.interrupt();
        }
        
        if (manejadorSockets != null) {
            manejadorSockets.cerrar();
        }
        
        vista.getBtnConectar().setText("Conectar");
        vista.habilitarControles(false);
        vista.agregarMensaje("Desconectado del servidor");
    }
    
    /**
     * Inicia la aplicación del cliente.
     */
    public void iniciarCliente() {
        SwingUtilities.invokeLater(() -> {
            vista.mostrar();
            vista.agregarMensaje("=== Bienvenido a Pac-Man Remoto ===");
            vista.agregarMensaje("Objetivo: Encuentra las 4 frutas para ganar.\n");
            vista.agregarMensaje("Controles (escribe y presiona Enter):");
            vista.agregarMensaje("  - arriba");
            vista.agregarMensaje("  - abajo");
            vista.agregarMensaje("  - izquierda");
            vista.agregarMensaje("  - derecha");
            vista.agregarMensaje("");
            vista.agregarMensaje("Notas:");
            vista.agregarMensaje("  - El juego corre en el servidor. Aquí sólo envías comandos y ves mensajes.");
            vista.agregarMensaje("  - Si alcanzas un borde, se te informará y deberás intentar otra dirección.");
            vista.agregarMensaje("======================================\n");
        });
    }
}

