package com.miempresa.pacman.dao;

import com.miempresa.pacman.servidor.modelo.Jugador;

/**
 * Interfaz para el patrón DAO que define las operaciones de acceso a datos
 * para la entidad Jugador.
 * 
 * <p>Esta interfaz sigue el principio de segregación de interfaces (ISP)
 * y el principio de inversión de dependencias (DIP) de SOLID.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0
 */
public interface IJugadorDAO {
    
    /**
     * Valida las credenciales de un jugador en la base de datos.
     * 
     * @param usuario El nombre de usuario a validar
     * @param contraseña La contraseña a validar
     * @return El objeto Jugador si las credenciales son válidas, null en caso contrario
     */
    Jugador validarCredenciales(String usuario, String contraseña);
    
    /**
     * Carga usuarios desde un archivo de propiedades a la base de datos.
     * 
     * @param rutaArchivo La ruta al archivo de propiedades con los usuarios
     */
    void cargarUsuariosDesdeProperties(String rutaArchivo);
}

