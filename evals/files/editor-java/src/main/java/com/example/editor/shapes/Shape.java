package com.example.editor.shapes;

import com.example.editor.canvas.Canvas;

public abstract class Shape {
    protected final String id;
    protected String stroke = "#000000";

    protected Shape(String id) { this.id = id; }

    public String id() { return id; }

    public void setStroke(String stroke) { this.stroke = stroke; }

    public abstract void draw(Canvas canvas);

    public abstract void move(double dx, double dy);

    public abstract double[] bounds();
}
