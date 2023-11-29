package com.project_ci01.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.project_ci01.app.adapter.PaletteAdapter;
import com.project_ci01.app.adapter.palette.PaletteItem;
import com.project_ci01.app.base.utils.LogUtils;
import com.project_ci01.app.base.view.dialog.DialogHelper;
import com.project_ci01.app.base.view.recyclerview.OnItemClickListener;
import com.project_ci01.app.config.IConfig;
import com.project_ci01.app.dao.ImageDbManager;
import com.project_ci01.app.dao.ImageEntityNew;
import com.project_ci01.app.dialog.PixelGuideDialog;
import com.project_ci01.app.pixel.PixelView;
import com.project_ci01.app.pixel.Props;
import com.project_ci01.app.base.view.BaseActivity;
import com.project_ci01.app.databinding.ActivityPixelBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PixelActivity extends BaseActivity implements OnItemClickListener<PaletteAdapter.PaletteHolder>, PixelView.OnPixelViewCallback {

    ActivityPixelBinding binding;

    private PaletteAdapter adapter;

    private ImageEntityNew entity;

    private PixelGuideDialog pixelGuideDialog;

    @Override
    protected String tag() {
        return "PixelActivity";
    }

    @Override
    protected void setContentView() {
        binding = ActivityPixelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected View stubBar() {
        return binding.stubBar;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init(getIntent());

        showPixelGuideDialog();
    }

    private void init(Intent intent) {

        if (intent != null) {
            entity = intent.getParcelableExtra(IConfig.KEY_IMAGE_ENTITY);
        }

        binding.paletteRv.setAdapter(adapter = new PaletteAdapter(this, this));
        binding.viewPixel.setOnPixelViewCallback(this);

        if (entity != null) {
            entity.colorTime = System.currentTimeMillis(); // 更新进入填色页的时间
            ImageDbManager.getInstance().updateColorTime(entity);

            binding.viewPixel.loadPixels(entity); // 加载像素点数据
        }

        binding.topBack.setOnClickListener(v -> {
            back();
        });

        binding.btnBucket.setOnClickListener(v -> {
            binding.viewPixel.setProps(Props.BUCKET);
            updatePropsView(Props.BUCKET);
        });

        binding.btnWand.setOnClickListener(v -> {
            binding.viewPixel.setProps(Props.WAND);
            updatePropsView(Props.WAND);
        });

        binding.btnBrush.setOnClickListener(v -> {
            binding.viewPixel.setProps(Props.BRUSH);
            updatePropsView(Props.BRUSH);
        });

        binding.btnTip.setOnClickListener(v -> {
            binding.viewPixel.centerUndrawPixel();
        });
    }

    @Override
    public void onItemClick(int position, PaletteAdapter.PaletteHolder holder) {
        if (!holder.item.completed && binding.viewPixel.setSelColor(holder.item.color)) {
            binding.viewPixel.invalidate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.viewPixel.release();
    }

    private void updatePropsView(@NonNull Props props) {
        binding.btnBucket.setSelected(props == Props.BUCKET);
        binding.btnWand.setSelected(props == Props.WAND);
        binding.btnBrush.setSelected(props == Props.BRUSH);
    }

    private void showPixelGuideDialog() {
        pixelGuideDialog = DialogHelper.showDialog(this, pixelGuideDialog, PixelGuideDialog.class, null);
    }

    private void startCompleteDisplayActivity(@NonNull ImageEntityNew entity) {
        if (canTurn()) {
            Intent intent = new Intent(this, CompleteDisplayActivity.class);
            intent.putExtra(IConfig.KEY_IMAGE_ENTITY, entity);
            startActivity(intent);
            finish();
        }
    }

    /*===================  PixelView.OnPixelViewCallback  =====================*/

    @Override
    public void onInited() {
        LogUtils.e(TAG, "--> onInited()");
        Map<Integer, String> numberMap = binding.viewPixel.getNumberMap();
        List<Map.Entry<Integer, String>> numberEntries = new ArrayList<>(numberMap.entrySet());
        Collections.sort(numberEntries, (o1, o2) -> { // number 小的排前面
            return Integer.parseInt(o1.getValue()) - Integer.parseInt(o2.getValue());
        });
        List<PaletteItem> items = new ArrayList<>();
        for (Map.Entry<Integer, String> entry: numberEntries) {
            items.add(new PaletteItem(entry.getValue(), entry.getKey()));
        }

        if (adapter != null) {
            adapter.setDatasAndNotify(items);
        }

        if (binding.viewPixel.setSelColor(numberEntries.get(0).getKey())) { // 默认选中数字为 1 的颜色
            binding.viewPixel.invalidate();
        }
    }

    @Override
    public void onSelColorChanged(int selColor) {
        LogUtils.e(TAG, "--> onSelColorChanged()  selColor=" + selColor);
        if (adapter != null) {
            adapter.updateSelColor(selColor);
        }
    }

    @Override
    public void onColorCompleted(@NonNull List<Integer> colors) {
        LogUtils.e(TAG, "--> onColorCompleted()  colors=" + colors);
        if (adapter != null) {
            adapter.updateCompletedColor(colors);
        }
    }

    @Override
    public void onAllCompleted() {
        LogUtils.e(TAG, "--> onAllCompleted()");
        startCompleteDisplayActivity(entity);
    }

    @Override
    public void onPropsEnd(Props props) {
        LogUtils.e(TAG, "--> onPropsEnd()  props=" + props);
        updatePropsView(Props.NONE);
        // TODO 道具数量减一（第一版无限道具）
    }

}
