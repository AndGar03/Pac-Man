package com.miempresa.pacman.dao;

import com.miempresa.pacman.servidor.modelo.Jugador;
import com.miempresa.pacman.util.Configuracion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Implementación concreta del patrón DAO para la entidad Jugador.
 * 
 * <p>Esta clase es responsable únicamente del acceso a datos (Single Responsibility Principle).
 * Maneja todas las operaciones de base de datos relacionadas con Jugador.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0
 */
public class JugadorDAOImpl implements IJugadorDAO {
    
    private final String url;
    private final String usuario;
    private final String contraseña;
    
    /**
     * Constructor que recibe la configuración de la base de datos.
     * 
     * @param url La URL de conexión a la base de datos
     * @param usuario El usuario de la base de datos
     * @param contraseña La contraseña de la base de datos
     */
    public JugadorDAOImpl(String url, String usuario, String contraseña) {
        this.url = url;
        this.usuario = usuario;
        this.contraseña = contraseña;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Jugador validarCredenciales(String usuario, String contraseña) {
        String sql = "SELECT id, usuario FROM usuarios WHERE usuario = ? AND contraseña = ?";
        
        try (Connection conn = DriverManager.getConnection(url, this.usuario, this.contraseña);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario);
            stmt.setString(2, contraseña);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String nombreUsuario = rs.getString("usuario");
                    return new Jugador(id, nombreUsuario);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar credenciales: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void cargarUsuariosDesdeProperties(String rutaArchivo) {
        Properties props = Configuracion.cargarPropiedades(rutaArchivo);
        String usuariosStr = props.getProperty("users.to.load", "");
        
        if (usuariosStr.isEmpty()) {
            return;
        }
        
        String[] usuarios = usuariosStr.split(",");
        String sql = "INSERT INTO usuarios (usuario, contraseña) VALUES (?, ?) "
                   + "ON DUPLICATE KEY UPDATE usuario = usuario";
        
        try (Connection conn = DriverManager.getConnection(url, this.usuario, this.contraseña);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (String usuarioEntry : usuarios) {
                String[] partes = usuarioEntry.trim().split(":");
                if (partes.length == 2) {
                    String nombreUsuario = partes[0].trim();
                    String contraseñaUsuario = partes[1].trim();
                    
                    stmt.setString(1, nombreUsuario);
                    stmt.setString(2, contraseñaUsuario);
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar usuarios desde properties: " + e.getMessage());
        }
    }
}

