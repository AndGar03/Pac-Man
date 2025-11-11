package udistrital.avanzada.pacman.util;

import java.io.File;
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
 * @author And_Gar03, SanSantax
 * @version 1.0
 */
public class Configuracion {
    
    /**
     * Carga propiedades desde un archivo.
     * Intenta cargar desde múltiples ubicaciones posibles.
     * 
     * @param rutaArchivo La ruta al archivo de propiedades
     * @return Un objeto Properties con las propiedades cargadas
     */
    public static Properties cargarPropiedades(String rutaArchivo) {
        Properties props = new Properties();
        
        // Intentar cargar desde diferentes ubicaciones
        String[] rutas = {
            rutaArchivo,  // Ruta original
            "../" + rutaArchivo,  // Desde build/classes
            "../../" + rutaArchivo,  // Desde build/classes/com/...
            "src/main/resources/data/" + new File(rutaArchivo).getName(),  // Nombre del archivo desde raíz
            System.getProperty("user.dir") + File.separator + rutaArchivo  // Desde directorio de trabajo
        };
        
        boolean cargado = false;
        for (String ruta : rutas) {
            try {
                File archivo = new File(ruta);
                if (archivo.exists() && archivo.isFile()) {
                    try (InputStream input = new FileInputStream(archivo)) {
                        props.load(input);
                        cargado = true;
                        break;
                    }
                }
            } catch (IOException e) {
                // Continuar con la siguiente ruta
            }
        }
        
        // Si no se pudo cargar desde archivo, intentar desde recursos
        if (!cargado) {
            try {
                InputStream input = Configuracion.class.getClassLoader()
                    .getResourceAsStream("data/" + new File(rutaArchivo).getName());
                if (input != null) {
                    props.load(input);
                    cargado = true;
                }
            } catch (IOException e) {
                // Ignorar
            }
        }
        
        if (!cargado) {
            System.err.println("Error: No se pudo cargar el archivo de propiedades: " + rutaArchivo);
            System.err.println("Intentó buscar en: " + String.join(", ", rutas));
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

