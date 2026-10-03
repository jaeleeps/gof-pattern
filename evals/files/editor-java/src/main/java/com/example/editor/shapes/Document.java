package com.example.editor.shapes;

import java.util.LinkedHashMap;
import java.util.Map;

public class Document {
    private final Group root = new Group("root");
    private final Map<String, Shape> index = new LinkedHashMap<>();

    public void add(Shape s) { root.add(s); index.put(s.id(), s); }

    public void remove(Shape s) { root.remove(s); index.remove(s.id()); }

    public Shape find(String id) { return index.get(id); }

    public Group root() { return root; }
}
