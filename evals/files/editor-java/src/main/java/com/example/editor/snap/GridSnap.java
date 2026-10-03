package com.example.editor.snap;

public class GridSnap implements SnapPolicy {
    private final double size;

    public GridSnap(double size) { this.size = size; }

    public double size() { return size; }

    public double[] snap(double x, double y) {
        return new double[] { Math.round(x / size) * size, Math.round(y / size) * size };
    }
}
