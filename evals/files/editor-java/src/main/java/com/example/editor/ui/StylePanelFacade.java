package com.example.editor.ui;

public class StylePanelFacade {
    private final ColorPicker picker = new ColorPicker(this);
    private final StrokeToggle toggle = new StrokeToggle(this);
    private final PreviewPane preview = new PreviewPane(this);

    void widgetChanged(Widget source) {
        if (source == toggle) {
            picker.setEnabled(toggle.on());
        }
        preview.show(picker.color(), toggle.on());
    }

    public ColorPicker picker() { return picker; }
    public StrokeToggle toggle() { return toggle; }
    public PreviewPane preview() { return preview; }
}
