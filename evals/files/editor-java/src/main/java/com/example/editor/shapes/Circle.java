package com.example.editor.shapes;

import com.example.editor.canvas.Canvas;

public class Circle extends Shape {
    private double cx, cy;
    private final double r;

    public Circle(String id, double cx, double cy, double r) {
        super(id);
        this.cx = cx; this.cy = cy; this.r = r;
    }

    public void draw(Canvas canvas) { canvas.drawEllipse(cx, cy, r, r, stroke); }

    public void move(double dx, double dy) { cx += dx; cy += dy; }

    public double[] bounds() { return new double[] { cx - r, cy - r, cx + r, cy + r }; }
}
