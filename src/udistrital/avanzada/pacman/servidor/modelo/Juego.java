package udistrital.avanzada.pacman.servidor.modelo;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Representa el estado y lógica del juego.
 * 
 * <p>Esta clase maneja toda la lógica de negocio del juego: inicialización,
 * movimiento, colisiones, puntaje. No contiene código de I/O (SRP).
 * 
 * @author And_Gar03
 * @version 1.0
 */
public class Juego {
    
    private static final int NUMERO_ITEMS = 4;
    private static final int MARGEN = 50;
    
    private final PacMan pacMan;
    private final List<Item> items;
    private int puntaje;
    private long tiempoInicio;
    private boolean juegoTerminado;
    private final Dimension areaJuego;
    
    /**
     * Constructor del juego.
     * 
     * @param areaJuego Las dimensiones del área de juego
     */
    public Juego(Dimension areaJuego) {
        this.areaJuego = new Dimension(areaJuego);
        this.pacMan = new PacMan(areaJuego);
        this.items = new ArrayList<>();
        this.puntaje = 0;
        this.juegoTerminado = false;
        inicializarItems();
        this.tiempoInicio = System.currentTimeMillis();
    }
    
    /**
     * Inicializa los ítems aleatoriamente en el área de juego.
     */
    private void inicializarItems() {
        List<Item.TipoItem> todosLosItems = new ArrayList<>();
        Collections.addAll(todosLosItems, Item.TipoItem.values());
        Collections.shuffle(todosLosItems);
        
        Random rand = new Random();
        
        for (int i = 0; i < NUMERO_ITEMS && i < todosLosItems.size(); i++) {
            int x = MARGEN + rand.nextInt(areaJuego.width - 2 * MARGEN);
            int y = MARGEN + rand.nextInt(areaJuego.height - 2 * MARGEN);
            items.add(new Item(todosLosItems.get(i), new Point(x, y)));
        }
    }
    
    /**
     * Procesa un movimiento de Pac-Man.
     * 
     * @param direccion La dirección del movimiento
     * @return ResultadoMovimiento con información sobre el movimiento
     */
    public ResultadoMovimiento procesarMovimiento(PacMan.Direccion direccion) {
        if (juegoTerminado) {
            return new ResultadoMovimiento(false, "El juego ha terminado");
        }
        
        boolean movimientoExitoso = pacMan.mover(direccion);

        if (!movimientoExitoso) {
            // Requisito: Informar límite sin revelar cuántas casillas se movió ni sugerir dirección
            return new ResultadoMovimiento(false, "Límite alcanzado");
        }
        
        // Verificar colisiones con ítems
        Item itemColisionado = verificarColisiones();
        if (itemColisionado != null) {
            itemColisionado.marcarRecogido();
            puntaje += itemColisionado.getPuntaje();
            
            // Verificar si el juego terminó
            if (todosLosItemsRecogidos()) {
                juegoTerminado = true;
                return new ResultadoMovimiento(true, "¡Ítem recogido! Puntaje: " + puntaje + 
                    ". ¡Juego terminado! Todos los ítems recogidos.");
            }
            
            return new ResultadoMovimiento(true, "¡Ítem recogido! Puntaje: " + puntaje);
        }
        
        return new ResultadoMovimiento(true, "Movimiento exitoso");
    }
    
    /**
     * Verifica si Pac-Man colisiona con algún ítem.
     * 
     * @return El ítem con el que colisionó, o null si no hay colisión
     */
    private Item verificarColisiones() {
        for (Item item : items) {
            if (pacMan.colisionaCon(item)) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * Verifica si todos los ítems han sido recogidos.
     * 
     * @return true si todos los ítems fueron recogidos, false en caso contrario
     */
    private boolean todosLosItemsRecogidos() {
        return items.stream().allMatch(Item::estaRecogido);
    }
    
    /**
     * Obtiene Pac-Man.
     * 
     * @return El objeto PacMan
     */
    public PacMan getPacMan() {
        return pacMan;
    }
    
    /**
     * Obtiene la lista de ítems.
     * 
     * @return Una copia de la lista de ítems
     */
    public List<Item> getItems() {
        return new ArrayList<>(items);
    }
    
    /**
     * Obtiene el puntaje actual.
     * 
     * @return El puntaje
     */
    public int getPuntaje() {
        return puntaje;
    }
    
    /**
     * Obtiene el tiempo transcurrido en segundos.
     * 
     * @return El tiempo transcurrido en segundos
     */
    public long getTiempoTranscurrido() {
        return (System.currentTimeMillis() - tiempoInicio) / 1000;
    }
    
    /**
     * Verifica si el juego ha terminado.
     * 
     * @return true si el juego terminó, false en caso contrario
     */
    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }
    
    /**
     * Clase que representa el resultado de un movimiento.
     */
    public static class ResultadoMovimiento {
        private final boolean exitoso;
        private final String mensaje;
        
        /**
         * Constructor de ResultadoMovimiento.
         * 
         * @param exitoso Si el movimiento fue exitoso
         * @param mensaje El mensaje asociado
         */
        public ResultadoMovimiento(boolean exitoso, String mensaje) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
        }
        
        /**
         * Obtiene si el movimiento fue exitoso.
         * 
         * @return true si fue exitoso, false en caso contrario
         */
        public boolean isExitoso() {
            return exitoso;
        }
        
        /**
         * Obtiene el mensaje del resultado.
         * 
         * @return El mensaje
         */
        public String getMensaje() {
            return mensaje;
        }
    }
}

