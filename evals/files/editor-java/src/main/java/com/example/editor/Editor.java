package com.example.editor;

import com.example.editor.canvas.SvgCanvas;
import com.example.editor.commands.AutosaveCommand;
import com.example.editor.commands.DeleteCommand;
import com.example.editor.commands.History;
import com.example.editor.commands.MoveCommand;
import com.example.editor.commands.StrokeCommand;
import com.example.editor.shapes.Document;
import com.example.editor.shapes.Shape;
import com.example.editor.snap.GridSnap;
import com.example.editor.snap.NoSnap;
import com.example.editor.snap.SnapPolicy;
import java.nio.file.Path;

public class Editor {
    private final Document doc = new Document();
    private final History history = new History();
    private SnapPolicy snap = new NoSnap();
    private final Path autosavePath;

    public Editor(Path autosavePath) { this.autosavePath = autosavePath; }

    public Document document() { return doc; }

    public void setSnap(SnapPolicy snap) { this.snap = snap; }

    public void drag(Shape shape, double fromX, double fromY, double toX, double toY) {
        double[] target;
        if (snap instanceof GridSnap grid) {
            double half = grid.size() / 2;
            target = grid.snap(toX + half, toY + half);
            target[0] -= half;
            target[1] -= half;
        } else {
            target = snap.snap(toX, toY);
        }
        history.run(new MoveCommand(shape, target[0] - fromX, target[1] - fromY));
        save();
    }

    public void delete(Shape shape) {
        history.run(new DeleteCommand(doc, shape));
        save();
    }

    public void restroke(Shape shape, String before, String after) {
        history.run(new StrokeCommand(shape, before, after));
        save();
    }

    public void undo() { history.undo(); save(); }

    public void redo() { history.redo(); save(); }

    private void save() {
        SvgCanvas canvas = new SvgCanvas();
        doc.root().draw(canvas);
        new AutosaveCommand(autosavePath, canvas.toBytes()).execute();
    }
}
