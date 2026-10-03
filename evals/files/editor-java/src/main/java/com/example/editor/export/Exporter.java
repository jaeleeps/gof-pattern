package com.example.editor.export;

import com.example.editor.canvas.Canvas;
import com.example.editor.shapes.Document;
import java.io.IOException;
import java.io.OutputStream;

public abstract class Exporter {
    public void export(Document doc, OutputStream out) throws IOException {
        Canvas canvas = createCanvas(doc);
        writeHeader(out);
        doc.root().draw(canvas);
        out.write(canvas.toBytes());
        writeFooter(out);
    }

    protected abstract Canvas createCanvas(Document doc);

    protected void writeHeader(OutputStream out) throws IOException { }

    protected void writeFooter(OutputStream out) throws IOException { }
}
