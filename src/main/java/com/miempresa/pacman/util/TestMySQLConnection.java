package com.miempresa.pacman.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utilidad para probar la conexión a MySQL.
 * Este script ayuda a verificar la configuración de MySQL en el sistema.
 */
public class TestMySQLConnection {
    
    public static void main(String[] args) {
        // Configuraciones comunes a probar
        String[] usuarios = {"root", "admin", "mysql"};
        String[] contraseñas = {"", "root", "admin", "password", "1234", "mysql", "root123"};
        String host = "localhost";
        String puerto = "3306";
        String database = "pacman_db";
        
        System.out.println("=== Probando conexión a MySQL ===");
        System.out.println("Host: " + host);
        System.out.println("Puerto: " + puerto);
        System.out.println("Base de datos: " + database);
        System.out.println();
        
        // Cargar driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver MySQL cargado correctamente.");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: No se encontró el driver de MySQL.");
            System.err.println("Asegúrate de tener mysql-connector-j en el classpath.");
            return;
        }
        
        // Probar conexiones
        boolean conectado = false;
        for (String usuario : usuarios) {
            for (String contraseña : contraseñas) {
                String url = "jdbc:mysql://" + host + ":" + puerto + "/" + database;
                
                try (Connection conn = DriverManager.getConnection(url, usuario, contraseña)) {
                    System.out.println("✓ CONEXIÓN EXITOSA!");
                    System.out.println("Usuario: " + usuario);
                    System.out.println("Contraseña: " + (contraseña.isEmpty() ? "(vacía)" : contraseña));
                    System.out.println("URL: " + url);
                    conectado = true;
                    
                    // Verificar si la base de datos existe
                    try {
                        conn.createStatement().executeQuery("SELECT 1");
                        System.out.println("Base de datos accesible correctamente.");
                    } catch (SQLException e) {
                        System.out.println("ADVERTENCIA: La base de datos podría no existir aún.");
                        System.out.println("Ejecuta el script: src/main/resources/data/db_script.sql");
                    }
                    
                    break;
                } catch (SQLException e) {
                    // Ignorar errores de autenticación, continuar probando
                    if (e.getErrorCode() == 1045) {
                        // Error de acceso denegado - continuar
                        continue;
                    } else if (e.getErrorCode() == 1049) {
                        // Base de datos no existe
                        System.out.println("ADVERTENCIA: La base de datos '" + database + "' no existe.");
                        System.out.println("Usuario: " + usuario);
                        System.out.println("Contraseña: " + (contraseña.isEmpty() ? "(vacía)" : contraseña));
                        System.out.println("Ejecuta el script: src/main/resources/data/db_script.sql");
                        conectado = true;
                        break;
                    }
                }
            }
            if (conectado) break;
        }
        
        if (!conectado) {
            System.out.println("✗ No se pudo conectar con las credenciales comunes.");
            System.out.println();
            System.out.println("Por favor, verifica manualmente:");
            System.out.println("1. Que MySQL esté corriendo");
            System.out.println("2. Las credenciales en server.properties");
            System.out.println("3. Que el usuario tenga permisos para acceder a la base de datos");
        }
    }
}

