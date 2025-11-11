package udistrital.avanzada.pacman.servidor.modelo;

import java.awt.Point;
import java.awt.Dimension;

/**
 * Representa el personaje Pac-Man en el juego.
 * 
 * <p>Esta clase maneja la lógica de posición y movimiento de Pac-Man.
 * No contiene código de I/O, solo lógica de negocio (SRP).
 * 
 * @author And_Gar03
 * @version 1.0
 */
public class PacMan {
    
    private static final int TAMAÑO = 20;
    // Cada comando debe mover 4 casillas -> 4 * TAMAÑO píxeles
    private static final int VELOCIDAD = TAMAÑO * 4;
    
    private Point posicion;
    private final Dimension areaJuego;
    
    /**
     * Constructor de PacMan.
     * 
     * @param areaJuego Las dimensiones del área de juego
     */
    public PacMan(Dimension areaJuego) {
        this.areaJuego = new Dimension(areaJuego);
        // Posición inicial en el centro
        this.posicion = new Point(
            areaJuego.width / 2 - TAMAÑO / 2,
            areaJuego.height / 2 - TAMAÑO / 2
        );
    }
    
    /**
     * Obtiene la posición actual de Pac-Man.
     * 
     * @return La posición actual
     */
    public Point getPosicion() {
        return new Point(posicion);
    }
    
    /**
     * Obtiene el tamaño de Pac-Man.
     * 
     * @return El tamaño
     */
    public int getTamaño() {
        return TAMAÑO;
    }
    
    /**
     * Intenta mover a Pac-Man en una dirección específica.
     * Se mueve píxel por píxel (4 casillas = 4 * TAMAÑO píxeles) y se detiene si alcanza un borde
     * en cualquier momento del movimiento.
     * 
     * @param direccion La dirección del movimiento
     * @return true si el movimiento fue completado (4*TAMAÑO píxeles), false si se alcanzó un límite
     */
    public boolean mover(Direccion direccion) {
        int pixelesMovidos = 0;
        Point posicionActual = new Point(posicion);
        
        // Mover píxel por píxel hasta completar VELOCIDAD píxeles o tocar un borde
        for (int i = 0; i < VELOCIDAD; i++) {
            Point nuevaPosicion = new Point(posicionActual);
            
            switch (direccion) {
                case ARRIBA:
                    nuevaPosicion.y -= 1;
                    break;
                case ABAJO:
                    nuevaPosicion.y += 1;
                    break;
                case IZQUIERDA:
                    nuevaPosicion.x -= 1;
                    break;
                case DERECHA:
                    nuevaPosicion.x += 1;
                    break;
            }
            
            // Si la nueva posición es válida, actualizar; si no, detener
            if (esPosicionValida(nuevaPosicion)) {
                posicionActual = nuevaPosicion;
                pixelesMovidos++;
            } else {
                // Se alcanzó un límite, detener el movimiento
                break;
            }
        }
        
        // Actualizar la posición final
        this.posicion = posicionActual;
        
        // Retornar true solo si se movieron los 4 píxeles completos
        return pixelesMovidos == VELOCIDAD;
    }
    
    /**
     * Verifica si una posición es válida (dentro de los límites).
     * 
     * @param pos La posición a verificar
     * @return true si la posición es válida, false en caso contrario
     */
    private boolean esPosicionValida(Point pos) {
        return pos.x >= 0 && 
               pos.y >= 0 && 
               pos.x + TAMAÑO <= areaJuego.width && 
               pos.y + TAMAÑO <= areaJuego.height;
    }
    
    /**
     * Verifica si Pac-Man colisiona con un ítem.
     * 
     * @param item El ítem a verificar
     * @return true si hay colisión, false en caso contrario
     */
    public boolean colisionaCon(Item item) {
        if (item.estaRecogido()) {
            return false;
        }
        
        Point posItem = item.getPosicion();
        // Colisión simple basada en distancia
        double distancia = posicion.distance(posItem);
        return distancia < (TAMAÑO + 15); // 15 es aproximadamente el tamaño del ítem
    }
    
    /**
     * Enum que representa las direcciones de movimiento.
     */
    public enum Direccion {
        ARRIBA,
        ABAJO,
        IZQUIERDA,
        DERECHA
    }
    
    /**
     * Convierte un string a dirección.
     * 
     * @param comando El comando de texto
     * @return La dirección correspondiente, o null si es inválido
     */
    public static Direccion parsearDireccion(String comando) {
        if (comando == null) {
            return null;
        }
        
        String cmd = comando.trim().toLowerCase();
        switch (cmd) {
            case "arriba":
            case "arr":
                return Direccion.ARRIBA;
            case "abajo":
            case "aba":
                return Direccion.ABAJO;
            case "izquierda":
            case "izq":
                return Direccion.IZQUIERDA;
            case "derecha":
            case "der":
                return Direccion.DERECHA;
            default:
                return null;
        }
    }
}

