package com.example.editor.commands;

import com.example.editor.shapes.Shape;

public class StrokeCommand implements Command {
    private final Shape shape;
    private final String before, after;

    public StrokeCommand(Shape shape, String before, String after) {
        this.shape = shape; this.before = before; this.after = after;
    }

    public void execute() { shape.setStroke(after); }

    public void undo() { shape.setStroke(before); }
}
