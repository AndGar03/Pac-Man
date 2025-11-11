package udistrital.avanzada.pacman.servidor.controlador;

import udistrital.avanzada.pacman.servidor.vista.PanelJuego;
import udistrital.avanzada.pacman.shared.util.FrameCapturer;
import udistrital.avanzada.pacman.util.ManejadorSockets;

public class StreamSender extends Thread {

    private final PanelJuego gamePanel;
    private final ManejadorSockets sockets;
    private final long frameDelayMillis;
    private volatile boolean running = true;

    public StreamSender(ManejadorSockets sockets, PanelJuego gamePanel, long frameDelayMillis) {
        if (sockets == null || gamePanel == null) throw new IllegalArgumentException("Parámetros inválidos");
        this.sockets = sockets;
        this.gamePanel = gamePanel;
        this.frameDelayMillis = frameDelayMillis;
        setName("StreamSender");
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
                byte[] jpeg = FrameCapturer.captureToJpegBytes(gamePanel);
                sockets.enviarFrame(jpeg);
                try {
                    Thread.sleep(frameDelayMillis);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                break;
            }
        }
    }
}
