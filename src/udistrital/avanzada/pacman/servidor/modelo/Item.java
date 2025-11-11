package udistrital.avanzada.pacman.servidor.modelo;

import java.awt.Point;

/**
 * Representa un ítem (fruta) en el juego.
 * 
 * <p>Esta clase encapsula la información de un ítem: su tipo, posición y puntaje.
 * Sigue el principio de responsabilidad única (SRP).
 * 
 * @author And_Gar03
 * @version 1.0
 */
public class Item {
    
    private final TipoItem tipo;
    private Point posicion;
    private boolean recogido;
    
    /**
     * Constructor de Item.
     * 
     * @param tipo El tipo de ítem
     * @param posicion La posición del ítem en el área de juego
     */
    public Item(TipoItem tipo, Point posicion) {
        this.tipo = tipo;
        this.posicion = new Point(posicion);
        this.recogido = false;
    }
    
    /**
     * Obtiene el tipo del ítem.
     * 
     * @return El tipo de ítem
     */
    public TipoItem getTipo() {
        return tipo;
    }
    
    /**
     * Obtiene la posición del ítem.
     * 
     * @return La posición del ítem
     */
    public Point getPosicion() {
        return new Point(posicion);
    }
    
    /**
     * Establece la posición del ítem.
     * 
     * @param posicion La nueva posición
     */
    public void setPosicion(Point posicion) {
        this.posicion = new Point(posicion);
    }
    
    /**
     * Verifica si el ítem ha sido recogido.
     * 
     * @return true si el ítem fue recogido, false en caso contrario
     */
    public boolean estaRecogido() {
        return recogido;
    }
    
    /**
     * Marca el ítem como recogido.
     */
    public void marcarRecogido() {
        this.recogido = true;
    }
    
    /**
     * Obtiene el puntaje del ítem.
     * 
     * @return El puntaje del ítem
     */
    public int getPuntaje() {
        return tipo.getPuntaje();
    }
    
    /**
     * Enum que representa los tipos de ítems disponibles en el juego.
     */
    public enum TipoItem {
        CEREZA(100, "Cereza"),
        FRESA(300, "Fresa"),
        NARANJA(500, "Naranja"),
        MANZANA(700, "Manzana"),
        MELON(1000, "Melón"),
        GALAXIAN(2000, "Galaxian"),
        CAMPANA(3000, "Campana"),
        LLAVE(5000, "Llave");
        
        private final int puntaje;
        private final String nombre;
        
        TipoItem(int puntaje, String nombre) {
            this.puntaje = puntaje;
            this.nombre = nombre;
        }
        
        /**
         * Obtiene el puntaje del tipo de ítem.
         * 
         * @return El puntaje
         */
        public int getPuntaje() {
            return puntaje;
        }
        
        /**
         * Obtiene el nombre del tipo de ítem.
         * 
         * @return El nombre
         */
        public String getNombre() {
            return nombre;
        }
    }
}

