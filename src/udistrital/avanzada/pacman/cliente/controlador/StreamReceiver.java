package udistrital.avanzada.pacman.cliente.controlador;

import udistrital.avanzada.pacman.util.ManejadorSockets;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public class StreamReceiver extends Thread {

    private final ManejadorSockets sockets;
    private final JLabel targetLabel;
    private volatile boolean running = true;

    public StreamReceiver(ManejadorSockets sockets, JLabel targetLabel) {
        if (sockets == null || targetLabel == null) throw new IllegalArgumentException("Parámetros inválidos");
        this.sockets = sockets;
        this.targetLabel = targetLabel;
        setName("StreamReceiver");
        setDaemon(true);
    }

    public void stopStreaming() {
        running = false;
        interrupt();
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted() && !sockets.estaCerrado()) {
            try {
                byte[] data = sockets.tomarFrame();
                if (data == null) break;
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));
                if (img == null) continue;
                ImageIcon icon = new ImageIcon(img);
                SwingUtilities.invokeLater(() -> targetLabel.setIcon(icon));
            } catch (Exception e) {
                break;
            }
        }
    }
}
