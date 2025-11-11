package udistrital.avanzada.pacman.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Utilidad para manejar operaciones de lectura y escritura en sockets con un único
 * ObjectInputStream/ObjectOutputStream y demultiplexación de canales.
 * 
 * Protocolo binario:
 *  - TAG 1: mensaje de texto -> writeInt(1); writeUTF(mensaje)
 *  - TAG 2: frame binario    -> writeInt(2); writeInt(len); write(byte[len])
 *
 * SRP: Encapsula I/O y multiplexación. Brinda colas bloqueantes para que
 * los controladores consuman texto y frames sin interferir entre sí.
 */
public class ManejadorSockets {
    
    private static final int TAG_TEXTO = 1;
    private static final int TAG_FRAME = 2;
    
    private final Socket socket;
    private final ObjectOutputStream salida;
    private final ObjectInputStream entrada;
    
    private final BlockingQueue<String> colaTextos = new ArrayBlockingQueue<>(1024);
    private final BlockingQueue<byte[]> colaFrames = new ArrayBlockingQueue<>(64);
    
    private final Thread hiloLector;
    private volatile boolean activo = true;
    
    /**
     * Constructor que inicializa los streams de entrada y salida y el demultiplexador.
     * 
     * @param socket El socket a manejar
     * @throws IOException Si hay un error al crear los streams
     */
    public ManejadorSockets(Socket socket) throws IOException {
        this.socket = socket;
        // Importante: crear primero ObjectOutputStream y hacer flush para enviar cabecera
        this.salida = new ObjectOutputStream(socket.getOutputStream());
        this.salida.flush();
        this.entrada = new ObjectInputStream(socket.getInputStream());
        
        // Hilo dedicado que lee del input y distribuye a colas
        this.hiloLector = new Thread(this::loopLectura, "SocketDemuxReader");
        this.hiloLector.setDaemon(true);
        this.hiloLector.start();
    }
    
    private void loopLectura() {
        try {
            while (activo && !Thread.currentThread().isInterrupted()) {
                int tag;
                try {
                    tag = entrada.readInt();
                } catch (EOFException eof) {
                    break;
                }
                if (tag == TAG_TEXTO) {
                    String msg = entrada.readUTF();
                    // no bloquear indefinidamente si cola llena
                    colaTextos.offer(msg);
                } else if (tag == TAG_FRAME) {
                    int len = entrada.readInt();
                    if (len <= 0 || len > 50 * 1024 * 1024) {
                        // tamaño inválido, abortar
                        break;
                    }
                    byte[] data = new byte[len];
                    entrada.readFully(data);
                    colaFrames.offer(data);
                } else {
                    // Tag desconocido: abortar lectura por seguridad
                    break;
                }
            }
        } catch (IOException e) {
            // Silenciar si se está cerrando
        } finally {
            activo = false;
        }
    }
    
    /**
     * Lee un mensaje de texto de manera bloqueante desde la cola.
     * 
     * @return mensaje o null si el socket se cerró y no habrá más mensajes
     */
    public String leerLinea() {
        while (activo) {
            try {
                String msg = colaTextos.take();
                return msg;
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return null;
    }
    
    /**
     * Envía un mensaje de texto usando el protocolo etiquetado.
     * 
     * @param mensaje texto a enviar
     */
    public void escribirLinea(String mensaje) {
        synchronized (salida) {
            try {
                salida.writeInt(TAG_TEXTO);
                salida.writeUTF(mensaje != null ? mensaje : "");
                salida.flush();
            } catch (IOException e) {
                // Error de escritura: cerrar
                cerrar();
            }
        }
    }
    
    /**
     * Toma el siguiente frame disponible (bloqueante). Devuelve null si se cierra.
     */
    public byte[] tomarFrame() {
        while (activo) {
            try {
                byte[] f = colaFrames.take();
                return f;
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return null;
    }
    
    /**
     * Envía un frame binario etiquetado de forma thread-safe.
     */
    public void enviarFrame(byte[] data) {
        if (data == null) return;
        synchronized (salida) {
            try {
                salida.writeInt(TAG_FRAME);
                salida.writeInt(data.length);
                salida.write(data);
                salida.flush();
            } catch (IOException e) {
                cerrar();
            }
        }
    }
    
    /**
     * Cierra los streams y el socket.
     */
    public void cerrar() {
        activo = false;
        if (hiloLector != null) {
            hiloLector.interrupt();
        }
        try {
            if (entrada != null) entrada.close();
        } catch (IOException ignored) {}
        try {
            if (salida != null) salida.close();
        } catch (IOException ignored) {}
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar socket: " + e.getMessage());
        }
    }
    
    /**
     * Verifica si el socket está cerrado.
     * 
     * @return true si el socket está cerrado, false en caso contrario
     */
    public boolean estaCerrado() {
        return socket == null || socket.isClosed() || !activo;
    }
}

