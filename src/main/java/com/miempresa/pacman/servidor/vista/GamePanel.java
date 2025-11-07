package com.miempresa.pacman.servidor.vista;

import com.miempresa.pacman.servidor.modelo.Item;
import com.miempresa.pacman.servidor.modelo.PacMan;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import javax.swing.JPanel;

/**
 * Panel que representa visualmente el juego.
 * 
 * <p>Esta clase es una vista pura que solo se encarga de renderizar el estado
 * del juego. No contiene lógica de negocio ni manejo de eventos (SRP).
 * 
 * @author Equipo de Desarrollo
 * @version 1.0
 */
public class GamePanel extends JPanel {
    
    private static final int ANCHO_DEFAULT = 800;
    private static final int ALTO_DEFAULT = 600;
    
    private PacMan pacMan;
    private List<Item> items;
    
    /**
     * Constructor del panel de juego.
     */
    public GamePanel() {
        setPreferredSize(new Dimension(ANCHO_DEFAULT, ALTO_DEFAULT));
        setBackground(Color.BLACK);
        setFocusable(true);
    }
    
    /**
     * Establece el estado del juego para renderizar.
     * 
     * @param pacMan El objeto PacMan
     * @param items La lista de ítems
     */
    public void actualizarEstado(PacMan pacMan, List<Item> items) {
        this.pacMan = pacMan;
        this.items = items;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        if (pacMan != null) {
            dibujarPacMan(g2d);
        }
        
        if (items != null) {
            dibujarItems(g2d);
        }
    }
    
    /**
     * Dibuja a Pac-Man en el panel.
     * 
     * @param g2d El contexto gráfico
     */
    private void dibujarPacMan(Graphics2D g2d) {
        Point pos = pacMan.getPosicion();
        int tamaño = pacMan.getTamaño();
        
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(pos.x, pos.y, tamaño, tamaño);
        
        // Ojos
        g2d.setColor(Color.BLACK);
        g2d.fillOval(pos.x + 6, pos.y + 6, 4, 4);
    }
    
    /**
     * Dibuja los ítems en el panel.
     * 
     * @param g2d El contexto gráfico
     */
    private void dibujarItems(Graphics2D g2d) {
        for (Item item : items) {
            if (!item.estaRecogido()) {
                Point pos = item.getPosicion();
                g2d.setColor(obtenerColorItem(item.getTipo()));
                g2d.fillOval(pos.x, pos.y, 15, 15);
                
                // Etiqueta con el nombre
                g2d.setColor(Color.WHITE);
                g2d.drawString(item.getTipo().getNombre(), pos.x - 10, pos.y - 5);
            }
        }
    }
    
    /**
     * Obtiene el color según el tipo de ítem.
     * 
     * @param tipo El tipo de ítem
     * @return El color correspondiente
     */
    private Color obtenerColorItem(Item.TipoItem tipo) {
        switch (tipo) {
            case CEREZA:
                return Color.RED;
            case FRESA:
                return new Color(255, 20, 147);
            case NARANJA:
                return Color.ORANGE;
            case MANZANA:
                return new Color(255, 0, 0);
            case MELON:
                return Color.GREEN;
            case GALAXIAN:
                return Color.CYAN;
            case CAMPANA:
                return Color.YELLOW;
            case LLAVE:
                return new Color(184, 134, 11);
            default:
                return Color.WHITE;
        }
    }
}

