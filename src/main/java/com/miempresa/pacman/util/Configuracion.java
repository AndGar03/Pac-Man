package com.miempresa.pacman.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utilidad para cargar y gestionar configuraciones desde archivos properties.
 * 
 * <p>Esta clase tiene una única responsabilidad: cargar propiedades desde archivos.
 * Sigue el principio de responsabilidad única (SRP).
 * 
 * @author Equipo de Desarrollo
 * @version 1.0
 */
public class Configuracion {
    
    /**
     * Carga propiedades desde un archivo.
     * 
     * @param rutaArchivo La ruta al archivo de propiedades
     * @return Un objeto Properties con las propiedades cargadas
     */
    public static Properties cargarPropiedades(String rutaArchivo) {
        Properties props = new Properties();
        
        try (InputStream input = new FileInputStream(rutaArchivo)) {
            props.load(input);
        } catch (IOException e) {
            System.err.println("Error al cargar archivo de propiedades: " + e.getMessage());
        }
        
        return props;
    }
    
    /**
     * Obtiene una propiedad con un valor por defecto.
     * 
     * @param props El objeto Properties
     * @param clave La clave de la propiedad
     * @param valorPorDefecto El valor por defecto si la clave no existe
     * @return El valor de la propiedad o el valor por defecto
     */
    public static String obtenerPropiedad(Properties props, String clave, String valorPorDefecto) {
        return props.getProperty(clave, valorPorDefecto);
    }
}

