package com.example.editor.shapes;

import com.example.editor.canvas.Canvas;

public class Rect extends Shape {
    private double x, y;
    private final double w, h;

    public Rect(String id, double x, double y, double w, double h) {
        super(id);
        this.x = x; this.y = y; this.w = w; this.h = h;
    }

    public void draw(Canvas canvas) { canvas.drawRect(x, y, w, h, stroke); }

    public void move(double dx, double dy) { x += dx; y += dy; }

    public double[] bounds() { return new double[] { x, y, x + w, y + h }; }
}
