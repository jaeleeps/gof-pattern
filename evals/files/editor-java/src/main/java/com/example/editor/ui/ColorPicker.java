package com.example.editor.ui;

public class ColorPicker extends Widget {
    private String color = "#000000";
    private boolean enabled = true;

    public ColorPicker(StylePanelFacade panel) { super(panel); }

    public void pick(String c) { color = c; changed(); }

    public String color() { return color; }

    public void setEnabled(boolean e) { enabled = e; }

    public boolean enabled() { return enabled; }
}
