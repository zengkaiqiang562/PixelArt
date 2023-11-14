package com.project_ci01.app.adapter.mine;

public class EmptyMineItem implements IMineItem {

    public final String text;

    public EmptyMineItem(String text) {
        this.text = text;
    }

    @Override
    public int type() {
        return TYPE_EMPTY;
    }
}
