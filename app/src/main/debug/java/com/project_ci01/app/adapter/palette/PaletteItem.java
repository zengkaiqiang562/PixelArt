package com.project_ci01.app.adapter.palette;

public class PaletteItem {
    public final String number;
    public final int color;
    public boolean selected;
    public boolean completed;

    public PaletteItem(String number, int color) {
        this.number = number;
        this.color = color;
    }
}
