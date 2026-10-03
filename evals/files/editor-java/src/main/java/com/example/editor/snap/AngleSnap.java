package com.example.editor.snap;

public class AngleSnap implements SnapPolicy {
    private final double originX, originY, stepDegrees;

    public AngleSnap(double originX, double originY, double stepDegrees) {
        this.originX = originX; this.originY = originY; this.stepDegrees = stepDegrees;
    }

    public double[] snap(double x, double y) {
        double dx = x - originX, dy = y - originY;
        double len = Math.hypot(dx, dy);
        double step = Math.toRadians(stepDegrees);
        double angle = Math.round(Math.atan2(dy, dx) / step) * step;
        return new double[] { originX + len * Math.cos(angle), originY + len * Math.sin(angle) };
    }
}
