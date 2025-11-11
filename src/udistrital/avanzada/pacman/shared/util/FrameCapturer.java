package udistrital.avanzada.pacman.shared.util;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class FrameCapturer {

    private FrameCapturer() {}

    public static byte[] captureToJpegBytes(Component component) throws IOException {
        if (component == null) {
            throw new IllegalArgumentException("Component no debe ser null");
        }

        final int w = Math.max(1, component.getWidth());
        final int h = Math.max(1, component.getHeight());

        final BufferedImage[] holder = new BufferedImage[1];
        Runnable paintTask = () -> {
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            try {
                component.printAll(g2);
            } finally {
                g2.dispose();
            }
            holder[0] = img;
        };

        try {
            if (SwingUtilities.isEventDispatchThread()) {
                paintTask.run();
            } else {
                SwingUtilities.invokeAndWait(paintTask);
            }
        } catch (Exception e) {
            IOException ioe = new IOException("Error capturando componente en EDT", e);
            throw ioe;
        }

        BufferedImage frame = holder[0];
        ByteArrayOutputStream baos = new ByteArrayOutputStream(64 * 1024);
        if (!ImageIO.write(frame, "jpg", baos)) {
            throw new IOException("No hay writer para formato JPEG");
        }
        return baos.toByteArray();
    }
}
