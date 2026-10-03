package com.example.editor.canvas;

import java.nio.charset.StandardCharsets;

public class SvgCanvas implements Canvas {
    private final StringBuilder out = new StringBuilder("<svg xmlns=\"http://www.w3.org/2000/svg\">");

    public void drawEllipse(double cx, double cy, double rx, double ry, String stroke) {
        out.append(String.format("<ellipse cx=\"%s\" cy=\"%s\" rx=\"%s\" ry=\"%s\" stroke=\"%s\"/>", cx, cy, rx, ry, stroke));
    }

    public void drawRect(double x, double y, double w, double h, String stroke) {
        out.append(String.format("<rect x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" stroke=\"%s\"/>", x, y, w, h, stroke));
    }

    public void beginGroup(String id) { out.append("<g id=\"").append(id).append("\">"); }

    public void endGroup() { out.append("</g>"); }

    public byte[] toBytes() { return (out + "</svg>").getBytes(StandardCharsets.UTF_8); }
}
