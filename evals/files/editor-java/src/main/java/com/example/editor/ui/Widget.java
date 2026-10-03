package com.example.editor.ui;

public abstract class Widget {
    protected final StylePanelFacade panel;

    protected Widget(StylePanelFacade panel) { this.panel = panel; }

    protected void changed() { panel.widgetChanged(this); }
}
