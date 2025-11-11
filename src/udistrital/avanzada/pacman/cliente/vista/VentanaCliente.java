package udistrital.avanzada.pacman.cliente.vista;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;

/**
 * Ventana principal del cliente.
 * 
 * <p>Esta clase es una vista pura que solo maneja la interfaz gráfica.
 * No contiene lógica de negocio. Los eventos son manejados por el controlador.
 * 
 * @author SanSantax
 * @version 1.0
 */
public class VentanaCliente extends JFrame {
    
    private final JTextField campoComando;
    private final JTextArea areaMensajes;
    private final JButton btnConectar;
    private final JButton btnEnviar;
    
    /**
     * Constructor de la ventana del cliente.
     */
    public VentanaCliente() {
        setTitle("Pac-Man Cliente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);
        
        // Panel superior: título y botón conectar
        JPanel panelSuperior = new JPanel();
        panelSuperior.setBackground(Color.BLACK);
        JLabel titulo = new JLabel("PAC-MAN REMOTO");
        titulo.setForeground(new Color(255, 215, 0)); // dorado
        titulo.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));
        btnConectar = new JButton("Conectar");
        btnConectar.setBackground(new Color(30, 30, 30));
        btnConectar.setForeground(new Color(50, 205, 50));
        btnConectar.setBorder(BorderFactory.createLineBorder(new Color(50, 205, 50)));
        btnConectar.setFocusPainted(false);
        panelSuperior.add(titulo);
        panelSuperior.add(btnConectar);
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central: área de mensajes
        areaMensajes = new JTextArea(15, 40);
        areaMensajes.setEditable(false);
        areaMensajes.setBackground(Color.BLACK);
        areaMensajes.setForeground(new Color(0, 255, 180));
        areaMensajes.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(areaMensajes);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 180)));
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior: entrada de comandos
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(Color.BLACK);
        JLabel etiquetaComando = new JLabel("Comando:");
        etiquetaComando.setForeground(new Color(255, 215, 0));
        etiquetaComando.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        campoComando = new JTextField(20);
        campoComando.setBackground(new Color(20, 20, 20));
        campoComando.setForeground(Color.WHITE);
        campoComando.setCaretColor(new Color(0, 255, 180));
        campoComando.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0)));
        btnEnviar = new JButton("Enviar");
        btnEnviar.setBackground(new Color(30, 30, 30));
        btnEnviar.setForeground(new Color(0, 191, 255));
        btnEnviar.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255)));
        btnEnviar.setFocusPainted(false);
        
        JPanel panelComando = new JPanel();
        panelComando.setBackground(Color.BLACK);
        panelComando.add(etiquetaComando);
        panelComando.add(campoComando);
        panelComando.add(btnEnviar);
        
        panelInferior.add(panelComando, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    /**
     * Obtiene el campo de comando.
     * 
     * @return El campo de texto para comandos
     */
    public JTextField getCampoComando() {
        return campoComando;
    }
    
    /**
     * Obtiene el área de mensajes.
     * 
     * @return El área de texto para mensajes
     */
    public JTextArea getAreaMensajes() {
        return areaMensajes;
    }
    
    /**
     * Obtiene el botón de conectar.
     * 
     * @return El botón de conectar
     */
    public JButton getBtnConectar() {
        return btnConectar;
    }
    
    /**
     * Obtiene el botón de enviar.
     * 
     * @return El botón de enviar
     */
    public JButton getBtnEnviar() {
        return btnEnviar;
    }
    
    /**
     * Agrega un mensaje al área de mensajes.
     * 
     * @param mensaje El mensaje a agregar
     */
    public void agregarMensaje(String mensaje) {
        areaMensajes.append(mensaje + "\n");
        areaMensajes.setCaretPosition(areaMensajes.getDocument().getLength());
    }
    
    /**
     * Limpia el área de mensajes.
     */
    public void limpiarMensajes() {
        areaMensajes.setText("");
    }
    
    /**
     * Habilita o deshabilita los controles de juego.
     * 
     * @param habilitado true para habilitar, false para deshabilitar
     */
    public void habilitarControles(boolean habilitado) {
        campoComando.setEnabled(habilitado);
        btnEnviar.setEnabled(habilitado);
    }
    
    /**
     * Muestra la ventana.
     */
    public void mostrar() {
        setVisible(true);
    }
}

