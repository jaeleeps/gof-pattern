package com.example.editor.canvas;

public interface Canvas {
    void drawEllipse(double cx, double cy, double rx, double ry, String stroke);
    void drawRect(double x, double y, double w, double h, String stroke);
    void beginGroup(String id);
    void endGroup();
    byte[] toBytes();
}
