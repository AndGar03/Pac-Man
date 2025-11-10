package com.miempresa.pacman.servidor.controlador;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Maneja la persistencia de resultados del juego usando RandomAccessFile.
 * 
 * <p>Esta clase tiene una única responsabilidad: guardar y leer resultados
 * del juego. Sigue el principio de responsabilidad única (SRP).
 * 
 * @author Equipo de Desarrollo
 * @version 1.0
 */
public class ManejadorResultados {
    
    private static final String ARCHIVO_RESULTADOS = "resultados.dat";
    private static final int TAMAÑO_REGISTRO = 100; // Tamaño fijo de cada registro
    
    /**
     * Guarda un resultado en el archivo.
     * 
     * @param nombreJugador El nombre del jugador
     * @param puntaje El puntaje obtenido
     * @param tiempoSegundos El tiempo de juego en segundos
     */
    public void guardarResultado(String nombreJugador, int puntaje, long tiempoSegundos) {
        try (RandomAccessFile raf = new RandomAccessFile(ARCHIVO_RESULTADOS, "rw")) {
            // Ir al final del archivo (append)
            raf.seek(raf.length());
            
            // Escribir el registro (formato: nombre (50 chars = 100 bytes) + puntaje (4 bytes) + tiempo (8 bytes)
            String nombre = nombreJugador.length() > 50 ? nombreJugador.substring(0, 50) : nombreJugador;
            String nombreFormateado = String.format("%-50s", nombre);
            for (int i = 0; i < 50; i++) {
                raf.writeChar(nombreFormateado.charAt(i));
            }
            raf.writeInt(puntaje);
            raf.writeLong(tiempoSegundos);
        } catch (IOException e) {
            System.err.println("Error al guardar resultado: " + e.getMessage());
        }
    }
    
    /**
     * Lee todos los resultados del archivo.
     * 
     * @return Lista de resultados
     */
    public List<ResultadoJuego> leerTodosLosResultados() {
        List<ResultadoJuego> resultados = new ArrayList<>();
        
        try (RandomAccessFile raf = new RandomAccessFile(ARCHIVO_RESULTADOS, "r")) {
            // Verificar que el archivo no esté vacío
            if (raf.length() == 0) {
                return resultados; // Archivo vacío, retornar lista vacía
            }
            
            while (raf.getFilePointer() < raf.length()) {
                // Leer nombre (50 caracteres = 100 bytes)
                StringBuilder nombre = new StringBuilder();
                for (int i = 0; i < 50; i++) {
                    char c = raf.readChar();
                    if (c != '\0' && c != ' ') {
                        nombre.append(c);
                    }
                }
                String nombreJugador = nombre.toString().trim();
                
                // Leer puntaje
                int puntaje = raf.readInt();
                
                // Leer tiempo
                long tiempo = raf.readLong();
                
                resultados.add(new ResultadoJuego(nombreJugador, puntaje, tiempo));
            }
        } catch (java.io.FileNotFoundException e) {
            // Archivo no existe aún (primera ejecución) - esto es normal, no es un error
            // No imprimir mensaje de error en este caso
        } catch (IOException e) {
            // Otro tipo de error de I/O
            System.err.println("Error al leer resultados: " + e.getMessage());
        }
        
        return resultados;
    }
    
    /**
     * Clase que representa un resultado de juego.
     */
    public static class ResultadoJuego {
        private final String nombreJugador;
        private final int puntaje;
        private final long tiempoSegundos;
        
        /**
         * Constructor de ResultadoJuego.
         * 
         * @param nombreJugador El nombre del jugador
         * @param puntaje El puntaje obtenido
         * @param tiempoSegundos El tiempo en segundos
         */
        public ResultadoJuego(String nombreJugador, int puntaje, long tiempoSegundos) {
            this.nombreJugador = nombreJugador;
            this.puntaje = puntaje;
            this.tiempoSegundos = tiempoSegundos;
        }
        
        /**
         * Obtiene el nombre del jugador.
         * 
         * @return El nombre del jugador
         */
        public String getNombreJugador() {
            return nombreJugador;
        }
        
        /**
         * Obtiene el puntaje.
         * 
         * @return El puntaje
         */
        public int getPuntaje() {
            return puntaje;
        }
        
        /**
         * Obtiene el tiempo en segundos.
         * 
         * @return El tiempo en segundos
         */
        public long getTiempoSegundos() {
            return tiempoSegundos;
        }
        
        @Override
        public String toString() {
            return "ResultadoJuego{nombre='" + nombreJugador + "', puntaje=" + puntaje + 
                   ", tiempo=" + tiempoSegundos + "s}";
        }
    }
}

