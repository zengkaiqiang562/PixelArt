package com.project_ci01.app.activity;

import android.content.Intent;
import android.graphics.Color;
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
import com.project_ci01.app.pixel.PixelHelper;
import com.project_ci01.app.pixel.PixelList;
import com.project_ci01.app.pixel.PixelView;
import com.project_ci01.app.pixel.Props;
import com.project_ci01.app.base.view.BaseActivity;
import com.project_ci01.app.databinding.ActivityPixelBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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

        // 初始化选中颜色
        PixelList pixelList = binding.viewPixel.getPixelList();
        int selColor = numberEntries.get(0).getKey(); // 默认取数字1为选中颜色
        if (pixelList != null) {
            Map<Integer, int[]> mapResult = new HashMap<>();
            PixelHelper.countDrawnPixels(pixelList, mapResult); // 计算某种颜色的填色进度
            for (Map.Entry<Integer, String> entry: numberEntries) {
                int color = entry.getKey();
                int[] colorResult = mapResult.get(color); // 取颜色 color 的填色进度
                if (colorResult == null) continue;
                if (colorResult[0] != colorResult[1]) { // 优先取 numberEntries 集合中第一个未填色完的颜色作为选中颜色
                    selColor = color;
                    break;
                }
            }
        }

        if (binding.viewPixel.setSelColor(selColor)) {
            binding.viewPixel.invalidate();
        }
    }

    @Override
    public void onSelColorChanged(int selColor) {
        LogUtils.e(TAG, "--> onSelColorChanged()  selColor=" + selColor);
        if (adapter != null) {
            int selColorIndex = adapter.updateSelColor(selColor);
            if (selColorIndex != -1) {
                binding.paletteRv.postDelayed(() -> {
                    binding.paletteRv.smoothScrollToPosition(selColorIndex); // 滚动到选中颜色处
                }, 100);
            }
        }
    }

    @Override
    public void onProgressChanged(@NonNull List<Integer> colors, Map<Integer, int[]> mapResult, int[] countResult) {
        LogUtils.e(TAG, "--> onProgressChanged()  colors=" + colors + "   countResult=" + Arrays.toString(countResult));
        if (!colors.isEmpty() && adapter != null) { // 有颜色填完了
            adapter.updateCompletedColor(colors);
        }

        if (countResult[0] == countResult[1]) { // 全部颜色都填完了
            startCompleteDisplayActivity(entity);
            return;
        }

        /* 未填完全部颜色，但当前颜色填完时，需要自动切换到颜色盘中的下一个未填完的颜色 */
        int selColor = binding.viewPixel.getSelColor();
        if (!colors.contains(selColor)) { // 当前颜色没有填完
            return;
        }
        if (adapter == null || adapter.getDatas() == null || adapter.getDatas().isEmpty()) {
            return;
        }

        List<PaletteItem> paletteItems = adapter.getDatas();
        int selColorIndex = -1; // 当前选中颜色在 numberEntries 中的索引
        for (int index = 0; index < paletteItems.size(); index++) {
            PaletteItem paletteItem = paletteItems.get(index);
            if (selColor == paletteItem.color) {
                selColorIndex = index;
                break;
            }
        }

        if (selColorIndex == -1) {
            return;
        }

        int nextColor = Color.TRANSPARENT;
        int nextIndex = (selColorIndex + 1) % paletteItems.size();
        while (nextIndex != selColorIndex) {
            int color = paletteItems.get(nextIndex).color;
            if (!colors.contains(color)) {
                nextColor = color;
                break;
            }
            nextIndex = (nextIndex + 1) % paletteItems.size();
        }

        if (nextColor != Color.TRANSPARENT && binding.viewPixel.setSelColor(nextColor)) {
            binding.viewPixel.invalidate();
        }
    }

    @Override
    public void onPropsEnd(Props props) {
        LogUtils.e(TAG, "--> onPropsEnd()  props=" + props);
        updatePropsView(Props.NONE);
        // TODO 道具数量减一（第一版无限道具）
    }

}
