package udistrital.avanzada.pacman.servidor.vista;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Font;

/**
 * Ventana principal del servidor.
 * 
 * <p>Esta clase es una vista pura que solo maneja la interfaz gráfica.
 * No contiene lógica de negocio. Los eventos son manejados por el controlador.
 * 
 * @author And_Gar03
 * @version 1.0
 */
public class VentanaServidor extends JFrame {
    
    private final PanelJuego gamePanel;
    private final JButton btnSalir;
    
    /**
     * Constructor de la ventana del servidor.
     */
    public VentanaServidor() {
        setTitle("Pac-Man Servidor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);

        // Título retro
        JPanel top = new JPanel(new FlowLayout());
        top.setBackground(Color.BLACK);
        JLabel titulo = new JLabel("PAC-MAN SERVIDOR");
        titulo.setForeground(new Color(255, 215, 0));
        titulo.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));
        top.add(titulo);
        add(top, BorderLayout.NORTH);

        // Panel de juego con borde
        gamePanel = new PanelJuego();
        gamePanel.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 180)));
        add(gamePanel, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBackground(Color.BLACK);
        btnSalir = new JButton("Salir");
        btnSalir.setBackground(new Color(30,30,30));
        btnSalir.setForeground(new Color(255, 99, 71));
        btnSalir.setBorder(BorderFactory.createLineBorder(new Color(255, 99, 71)));
        btnSalir.setFocusPainted(false);
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
    public PanelJuego getGamePanel() {
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

