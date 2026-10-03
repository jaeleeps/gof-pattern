package com.example.editor.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AutosaveCommand {
    private final Path target;
    private final byte[] data;

    public AutosaveCommand(Path target, byte[] data) { this.target = target; this.data = data; }

    public void execute() {
        try {
            Files.write(target, data);
        } catch (IOException e) {
            throw new IllegalStateException("autosave failed", e);
        }
    }
}
