package com.example.editor.util;

public final class Geometry {
    private Geometry() { }

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.hypot(x2 - x1, y2 - y1);
    }

    public static boolean contains(double[] bounds, double x, double y) {
        return x >= bounds[0] && y >= bounds[1] && x <= bounds[2] && y <= bounds[3];
    }
}
