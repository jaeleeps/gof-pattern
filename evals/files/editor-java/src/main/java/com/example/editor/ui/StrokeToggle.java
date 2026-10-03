package com.example.editor.ui;

public class StrokeToggle extends Widget {
    private boolean on = true;

    public StrokeToggle(StylePanelFacade panel) { super(panel); }

    public void toggle() { on = !on; changed(); }

    public boolean on() { return on; }
}
