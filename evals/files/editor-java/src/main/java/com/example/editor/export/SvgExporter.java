package com.example.editor.export;

import com.example.editor.canvas.Canvas;
import com.example.editor.canvas.SvgCanvas;
import com.example.editor.shapes.Document;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class SvgExporter extends Exporter {
    protected Canvas createCanvas(Document doc) { return new SvgCanvas(); }

    protected void writeHeader(OutputStream out) throws IOException {
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes(StandardCharsets.UTF_8));
    }
}
