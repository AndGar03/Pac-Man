package udistrital.avanzada.pacman.cliente.modelo;

/**
 * Representa el estado de la conexión del cliente.
 * 
 * <p>Esta clase es un modelo de datos puro que encapsula el estado
 * de la conexión. No contiene lógica de negocio ni I/O (SRP).
 * 
 * @author SanSantax
 * @version 1.0
 */
public class EstadoConexion {
    
    private boolean conectado;
    private boolean autenticado;
    
    /**
     * Constructor del estado de conexión.
     */
    public EstadoConexion() {
        this.conectado = false;
        this.autenticado = false;
    }
    
    /**
     * Verifica si el cliente está conectado.
     * 
     * @return true si está conectado, false en caso contrario
     */
    public boolean isConectado() {
        return conectado;
    }
    
    /**
     * Establece el estado de conexión.
     * 
     * @param conectado El nuevo estado de conexión
     */
    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }
    
    /**
     * Verifica si el cliente está autenticado.
     * 
     * @return true si está autenticado, false en caso contrario
     */
    public boolean isAutenticado() {
        return autenticado;
    }
    
    /**
     * Establece el estado de autenticación.
     * 
     * @param autenticado El nuevo estado de autenticación
     */
    public void setAutenticado(boolean autenticado) {
        this.autenticado = autenticado;
    }
}

