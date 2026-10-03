package com.example.editor.commands;

import com.example.editor.shapes.Shape;

public class MoveCommand implements Command {
    private final Shape shape;
    private final double dx, dy;

    public MoveCommand(Shape shape, double dx, double dy) {
        this.shape = shape; this.dx = dx; this.dy = dy;
    }

    public void execute() { shape.move(dx, dy); }

    public void undo() { shape.move(dx, dy); }
}
