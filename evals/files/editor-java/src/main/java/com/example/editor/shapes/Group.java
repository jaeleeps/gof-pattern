package com.example.editor.shapes;

import com.example.editor.canvas.Canvas;
import java.util.ArrayList;
import java.util.List;

public class Group extends Shape {
    private final List<Shape> children = new ArrayList<>();

    public Group(String id) { super(id); }

    public void add(Shape s) { children.add(s); }

    public boolean remove(Shape s) { return children.remove(s); }

    public List<Shape> children() { return List.copyOf(children); }

    public void draw(Canvas canvas) {
        canvas.beginGroup(id);
        for (Shape s : children) s.draw(canvas);
        canvas.endGroup();
    }

    public void move(double dx, double dy) {
        for (Shape s : children) s.move(dx, dy);
    }

    public double[] bounds() {
        double[] b = { Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE };
        for (Shape s : children) {
            double[] c = s.bounds();
            b[0] = Math.min(b[0], c[0]); b[1] = Math.min(b[1], c[1]);
            b[2] = Math.max(b[2], c[2]); b[3] = Math.max(b[3], c[3]);
        }
        return b;
    }
}
