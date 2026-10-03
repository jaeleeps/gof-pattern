package com.example.editor.canvas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class RasterCanvas implements Canvas {
    private final BufferedImage image;
    private final Graphics2D g;

    public RasterCanvas(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g = image.createGraphics();
        g.setStroke(new BasicStroke(1.5f));
    }

    public void drawEllipse(double cx, double cy, double rx, double ry, String stroke) {
        g.setColor(Color.decode(stroke));
        g.draw(new Ellipse2D.Double(cx - rx, cy - ry, rx * 2, ry * 2));
    }

    public void drawRect(double x, double y, double w, double h, String stroke) {
        g.setColor(Color.decode(stroke));
        g.draw(new Rectangle2D.Double(x, y, w, h));
    }

    public void beginGroup(String id) { }

    public void endGroup() { }

    public byte[] toBytes() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
