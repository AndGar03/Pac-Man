package com.miempresa.pacman.cliente.vista;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.GridLayout;

/**
 * Ventana principal del cliente.
 * 
 * <p>Esta clase es una vista pura que solo maneja la interfaz gráfica.
 * No contiene lógica de negocio. Los eventos son manejados por el controlador.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0
 */
public class ClientWindow extends JFrame {
    
    private final JTextField campoComando;
    private final JTextArea areaMensajes;
    private final JButton btnConectar;
    private final JButton btnEnviar;
    
    /**
     * Constructor de la ventana del cliente.
     */
    public ClientWindow() {
        setTitle("Pac-Man Cliente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Panel superior: botón conectar
        JPanel panelSuperior = new JPanel();
        btnConectar = new JButton("Conectar");
        panelSuperior.add(btnConectar);
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central: área de mensajes
        areaMensajes = new JTextArea(15, 40);
        areaMensajes.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaMensajes);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior: entrada de comandos
        JPanel panelInferior = new JPanel(new BorderLayout());
        JLabel etiquetaComando = new JLabel("Comando:");
        campoComando = new JTextField(20);
        btnEnviar = new JButton("Enviar");
        
        JPanel panelComando = new JPanel();
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

