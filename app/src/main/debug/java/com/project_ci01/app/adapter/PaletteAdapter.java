package com.project_ci01.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ConvertUtils;
import com.project_ci01.app.R;
import com.project_ci01.app.adapter.palette.PaletteItem;
import com.project_ci01.app.base.view.recyclerview.BaseAdapter;
import com.project_ci01.app.base.view.recyclerview.BaseHolder;
import com.project_ci01.app.base.view.recyclerview.OnItemClickListener;

import java.util.List;

public class PaletteAdapter extends BaseAdapter<PaletteItem, PaletteAdapter.PaletteHolder> {

    private final ShapeDrawable strokeDrawable;
    public PaletteAdapter(OnItemClickListener<PaletteHolder> listener, Context context) {
        super(null, listener, context);

        OvalShape strokeOvalShape = new OvalShape();
        strokeDrawable = new ShapeDrawable(strokeOvalShape);
        strokeDrawable.getPaint().setStyle(Paint.Style.STROKE);
        strokeDrawable.getPaint().setStrokeWidth(ConvertUtils.dp2px(2));
        strokeDrawable.getPaint().setColor(Color.parseColor("#FF2E2A2B"));
    }

    public void updateSelColor(int selColor) {
        if (datas == null) {
            return;
        }
        for (PaletteItem item : datas) {
            item.selected = item.color == selColor;
        }
        notifyDataSetChanged();
    }

    public void updateCompletedColor(@NonNull List<Integer> colors) {
        if (datas == null) {
            return;
        }
        for (PaletteItem item : datas) {
            item.completed = colors.contains(item.color);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PaletteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PaletteHolder(parent);
    }

    public class PaletteHolder extends BaseHolder<PaletteItem> {

        private final ImageView ivComplete;
        private final ImageView ivIndicator;
        private final ImageView ivSelect;
        private final TextView tvNumber;
        private final ShapeDrawable shapeDrawable;

        public PaletteItem item;

        public PaletteHolder(ViewGroup parent) {
            super(parent, R.layout.item_color_palette);
            ivComplete = getView(R.id.palette_complete);
            ivIndicator = getView(R.id.palette_indicator);
            ivSelect = getView(R.id.palette_select);
            tvNumber = getView(R.id.palette_number);

            shapeDrawable = new ShapeDrawable(new OvalShape());
        }

        @Override
        public void setData(PaletteItem data, int position) {
            if (data == null) {
                return;
            }

            item = data;

            shapeDrawable.getPaint().setColor(data.color);
            tvNumber.setBackground(shapeDrawable);
            tvNumber.setText(data.number);

            ivComplete.setVisibility(data.completed ? View.VISIBLE : View.GONE);
            ivSelect.setImageResource(data.selected ? R.drawable.bg_palette_sel : R.drawable.bg_palette_unsel);
            ivIndicator.setVisibility(data.selected ? View.VISIBLE : View.GONE);

        }
    }
}
