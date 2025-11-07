package com.miempresa.pacman.servidor.vista;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * Ventana principal del servidor.
 * 
 * <p>Esta clase es una vista pura que solo maneja la interfaz gráfica.
 * No contiene lógica de negocio. Los eventos son manejados por el controlador.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0
 */
public class ServerWindow extends JFrame {
    
    private final GamePanel gamePanel;
    private final JButton btnSalir;
    
    /**
     * Constructor de la ventana del servidor.
     */
    public ServerWindow() {
        setTitle("Pac-Man Servidor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnSalir = new JButton("Salir");
        panelBotones.add(btnSalir);
        add(panelBotones, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    /**
     * Obtiene el panel de juego.
     * 
     * @return El panel de juego
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }
    
    /**
     * Obtiene el botón de salir.
     * 
     * @return El botón de salir
     */
    public JButton getBtnSalir() {
        return btnSalir;
    }
    
    /**
     * Muestra la ventana.
     */
    public void mostrar() {
        setVisible(true);
    }
}

