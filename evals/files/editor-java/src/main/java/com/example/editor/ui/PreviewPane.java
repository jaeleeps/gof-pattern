package com.example.editor.ui;

public class PreviewPane extends Widget {
    private String rendered = "";

    public PreviewPane(StylePanelFacade panel) { super(panel); }

    public void show(String color, boolean stroke) {
        rendered = stroke ? "stroke " + color : "no stroke";
    }

    public String rendered() { return rendered; }
}
