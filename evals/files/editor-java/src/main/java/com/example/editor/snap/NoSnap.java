package com.example.editor.snap;

public class NoSnap implements SnapPolicy {
    public double[] snap(double x, double y) { return new double[] { x, y }; }
}
