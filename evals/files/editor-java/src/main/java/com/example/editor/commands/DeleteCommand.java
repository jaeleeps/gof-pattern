package com.example.editor.commands;

import com.example.editor.shapes.Document;
import com.example.editor.shapes.Shape;

public class DeleteCommand implements Command {
    private final Document doc;
    private final Shape shape;

    public DeleteCommand(Document doc, Shape shape) { this.doc = doc; this.shape = shape; }

    public void execute() { doc.remove(shape); }

    public void undo() { doc.add(shape); }
}
