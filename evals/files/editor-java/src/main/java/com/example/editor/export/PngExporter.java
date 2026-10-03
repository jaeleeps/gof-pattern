package com.example.editor.export;

import com.example.editor.canvas.Canvas;
import com.example.editor.canvas.RasterCanvas;
import com.example.editor.shapes.Document;
import java.io.IOException;
import java.io.OutputStream;

public class PngExporter extends Exporter {
    private final int width, height;

    public PngExporter(int width, int height) { this.width = width; this.height = height; }

    protected Canvas createCanvas(Document doc) { return new RasterCanvas(width, height); }

    @Override
    public void export(Document doc, OutputStream out) throws IOException {
        double[] b = doc.root().bounds();
        RasterCanvas canvas = new RasterCanvas((int) Math.ceil(b[2]), (int) Math.ceil(b[3]));
        doc.root().draw(canvas);
        out.write(canvas.toBytes());
    }
}
