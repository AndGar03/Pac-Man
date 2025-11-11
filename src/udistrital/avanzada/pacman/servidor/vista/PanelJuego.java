package udistrital.avanzada.pacman.servidor.vista;

import udistrital.avanzada.pacman.servidor.modelo.Item;
import udistrital.avanzada.pacman.servidor.modelo.PacMan;

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
 * @author And_Gar03
 * @version 1.0
 */
public class PanelJuego extends JPanel {
    
    private static final int ANCHO_DEFAULT = 800;
    private static final int ALTO_DEFAULT = 600;
    
    private PacMan pacMan;
    private List<Item> items;
    
    /**
     * Constructor del panel de juego.
     */
    public PanelJuego() {
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
        
        // Si no hay juego activo, mostrar mensaje de espera
        if (pacMan == null && items == null) {
            dibujarMensajeEspera(g2d);
            return;
        }
        
        if (pacMan != null) {
            dibujarPacMan(g2d);
        }
        
        if (items != null) {
            dibujarItems(g2d);
        }
    }
    
    /**
     * Dibuja un mensaje indicando que el servidor está esperando conexiones.
     * 
     * @param g2d El contexto gráfico
     */
    private void dibujarMensajeEspera(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(g2d.getFont().deriveFont(24f));
        
        String mensaje = "Servidor Pac-Man";
        String mensaje2 = "Esperando conexiones de clientes...";
        String mensaje3 = "Puerto: 9090";
        
        // Centrar el texto
        int ancho = getWidth();
        int alto = getHeight();
        
        java.awt.FontMetrics fm = g2d.getFontMetrics();
        int x1 = (ancho - fm.stringWidth(mensaje)) / 2;
        int x2 = (ancho - fm.stringWidth(mensaje2)) / 2;
        int x3 = (ancho - fm.stringWidth(mensaje3)) / 2;
        
        int y = alto / 2 - 30;
        g2d.drawString(mensaje, x1, y);
        g2d.setFont(g2d.getFont().deriveFont(16f));
        fm = g2d.getFontMetrics();
        y += 40;
        g2d.drawString(mensaje2, x2, y);
        y += 30;
        g2d.drawString(mensaje3, x3, y);
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
        java.awt.Font fuenteOriginal = g2d.getFont();
        java.awt.Font fuenteRetro = new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.BOLD, 14);
        for (Item item : items) {
            if (!item.estaRecogido()) {
                Point pos = item.getPosicion();
                g2d.setColor(obtenerColorItem(item.getTipo()));
                g2d.fillOval(pos.x, pos.y, 15, 15);
                
                // Etiqueta con el nombre
                g2d.setColor(Color.WHITE);
                g2d.setFont(fuenteRetro);
                g2d.drawString(item.getTipo().getNombre(), pos.x - 10, pos.y - 5);
            }
        }
        g2d.setFont(fuenteOriginal);
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

