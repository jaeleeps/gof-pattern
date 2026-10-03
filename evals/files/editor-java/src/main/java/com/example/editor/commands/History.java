package com.example.editor.commands;

import java.util.ArrayDeque;
import java.util.Deque;

public class History {
    private final Deque<Command> undo = new ArrayDeque<>();
    private final Deque<Command> redo = new ArrayDeque<>();

    public void run(Command c) {
        c.execute();
        undo.push(c);
        redo.clear();
    }

    public void undo() {
        if (undo.isEmpty()) return;
        Command c = undo.pop();
        c.undo();
        redo.push(c);
    }

    public void redo() {
        if (redo.isEmpty()) return;
        Command c = redo.pop();
        c.execute();
        undo.push(c);
    }
}
