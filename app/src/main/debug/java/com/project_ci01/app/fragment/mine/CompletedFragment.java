package com.project_ci01.app.fragment.mine;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ConvertUtils;
import com.project_ci01.app.activity.CompleteActivity;
import com.project_ci01.app.activity.PixelActivity;
import com.project_ci01.app.adapter.MineImageAdapter;
import com.project_ci01.app.adapter.mine.EmptyMineItem;
import com.project_ci01.app.adapter.mine.IMineItem;
import com.project_ci01.app.adapter.mine.ImageMineItem;
import com.project_ci01.app.config.IConfig;
import com.project_ci01.app.dao.Category;
import com.project_ci01.app.dao.ImageDbManager;
import com.project_ci01.app.dao.ImageEntityNew;
import com.project_ci01.app.dialog.MineCompleteDeleteDialog;
import com.project_ci01.app.dialog.MineCompleteDialog;
import com.project_ci01.app.dialog.MineCompleteRecolorDialog;
import com.project_ci01.app.fragment.BaseImageFragment;
import com.project_ci01.app.pixel.PixelHelper;
import com.project_ci01.app.pixel.PixelList;
import com.project_ci01.app.pixel.PixelManager;
import com.project_ci01.app.pixel.PixelUnit;
import com.project_ci01.app.base.manage.ContextManager;
import com.project_ci01.app.base.utils.FileUtils;
import com.project_ci01.app.base.utils.LogUtils;
import com.project_ci01.app.base.utils.MyTimeUtils;
import com.project_ci01.app.base.view.BaseFragment;
import com.project_ci01.app.base.view.dialog.DialogHelper;
import com.project_ci01.app.base.view.dialog.SimpleDialogListener;
import com.project_ci01.app.base.view.recyclerview.BaseHolder;
import com.project_ci01.app.base.view.recyclerview.OnItemClickListener;
import com.project_ci01.app.databinding.FragmentCompletedBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompletedFragment extends BaseImageFragment implements OnItemClickListener<BaseHolder<IMineItem>> {

    private FragmentCompletedBinding binding;
    private MineImageAdapter adapter;

    private MineCompleteDialog completeDialog;
    private MineCompleteRecolorDialog recolorDialog;
    private MineCompleteDeleteDialog deleteDialog;


    private StoreHandler storeHandler;
    private HandlerThread storeHandlerThread;

    @Override
    protected String tag() {
        return "CompletedFragment";
    }

    @Override
    protected View getRoot(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentCompletedBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected View stubBar() {
        return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // storeHandler
        storeHandlerThread = new HandlerThread("thread_complete_store");
        storeHandlerThread.start();
        storeHandler = new StoreHandler(storeHandlerThread.getLooper());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        storeHandler.removeCallbacksAndMessages(null);
        storeHandlerThread.quitSafely();
        storeHandler = null;
        storeHandlerThread = null;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter == null) {
                    return 1;
                }

                List<IMineItem> datas = adapter.getDatas();
                if (datas == null || datas.isEmpty() || position >= datas.size()) {
                    return 1;
                }

                int type = datas.get(position).type();
                if (type == IMineItem.TYPE_EMPTY) {
                    return 2; // empty item 占 2 个item的位置，即独占一行
                } else {
                    return 1;
                }
            }
        });

        // 必须设置后才能嵌套滑动
        binding.completedRv.setLayoutManager(gridLayoutManager);
        binding.completedRv.setAdapter(adapter = new MineImageAdapter(this, activity));
        binding.completedRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            final int dp16 = ConvertUtils.dp2px(16);
            final int dp20 = ConvertUtils.dp2px(20);

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

                if (adapter == null || adapter.getDatas() == null || adapter.getDatas().isEmpty()) {
                    outRect.set(0, 0, 0, 0);
                    return;
                }

                GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                int spanIndex = layoutParams.getSpanIndex();
                int spanSize = layoutParams.getSpanSize();
                List<IMineItem> datas = adapter.getDatas();
                int position = parent.getChildLayoutPosition(view);
                int type = datas.get(position).type();

                if (spanSize == 2 && type == IMineItem.TYPE_EMPTY) { // view 为 empty item
                    outRect.set(0, 0, 0, 0);
                    return;
                }

                if (spanSize == 1 && spanIndex == 0) { // view 为左侧的 image item
                    outRect.set(dp20, 0, dp16 / 2, 0);
                    return;
                }

                if (spanSize == 1 && spanIndex == 1) { // view 为右侧的 image item
                    outRect.set(dp16 / 2, 0, dp20, 0);
                }
            }
        });

        update();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void update() {
        ImageDbManager.getInstance().queryCompleted(entities -> {
            if (ContextManager.isSurvival(activity) && adapter != null) {
                List<IMineItem> items = new ArrayList<>();

                if (entities == null || entities.isEmpty()) {
                    items.add(new EmptyMineItem("No completed pictures yet"));
                } else {
                    for (ImageEntityNew entity : entities) {
                        items.add(new ImageMineItem(entity));
                    }
                }
                adapter.setDatasAndNotify(items);
            }
        });
    }

    @Override
    public void onImageUpdated(String category, int imageId) {
        sendUpdateCompletedMsg(200);
    }

    @Override
    public void onItemClick(int item, BaseHolder<IMineItem> holder) {
        if (holder instanceof MineImageAdapter.MineImageHolder) {
            ImageEntityNew entity = ((MineImageAdapter.MineImageHolder) holder).entity;
            if (entity != null) {
                showCompleteDialog(entity);
            }
        }
    }

    private void showCompleteDialog(@NonNull ImageEntityNew entity) {
        completeDialog = DialogHelper.showDialog(activity, completeDialog, MineCompleteDialog.class, new SimpleDialogListener<MineCompleteDialog>() {
            @Override
            public void onShowBefore(MineCompleteDialog dialog) {
                dialog.setImageEntity(entity);
                dialog.setOnActionListener(new MineCompleteDialog.OnActionListener() {
                    @Override
                    public void onRecolor() {
                        showRecolorDialog(completeDialog.getImageEntity());
                    }

                    @Override
                    public void onDelete() {
                        showDeleteDialog(completeDialog.getImageEntity());
                    }

                    @Override
                    public void onShare() {
                        startCompleteActivity(completeDialog.getImageEntity());
                    }
                });
            }
        });
    }

    private void showRecolorDialog(@NonNull ImageEntityNew entity) {
        recolorDialog = DialogHelper.showDialog(activity, recolorDialog, MineCompleteRecolorDialog.class, new SimpleDialogListener<MineCompleteRecolorDialog>() {

            @Override
            public void onShowBefore(MineCompleteRecolorDialog dialog) {
                dialog.setImageEntity(entity);
            }

            @Override
            public void onConfirm() {
                if (storeHandler != null) {
                    storeHandler.sendRecolorMsg(recolorDialog.getImageEntity());
                }
            }
        });
    }

    private void showDeleteDialog(@NonNull ImageEntityNew entity) {
        deleteDialog = DialogHelper.showDialog(activity, deleteDialog, MineCompleteDeleteDialog.class, new SimpleDialogListener<MineCompleteDeleteDialog>() {

            @Override
            public void onShowBefore(MineCompleteDeleteDialog dialog) {
                dialog.setImageEntity(entity);
            }

            @Override
            public void onConfirm() {
                if (storeHandler != null) {
                    storeHandler.sendDeleteMsg(deleteDialog.getImageEntity());
                }
            }
        });
    }

    private void startPixelActivity(@NonNull ImageEntityNew entity) {
        if (canTurn()) {
            Intent intent = new Intent(activity, PixelActivity.class);
            intent.putExtra(IConfig.KEY_IMAGE_ENTITY, entity);
            activity.startActivityForResult(intent, IConfig.REQUEST_PIXEL_ACTIVITY);
        }
    }

    private void startCompleteActivity(@NonNull ImageEntityNew entity) {
        if (canTurn()) {
            Intent intent = new Intent(activity, CompleteActivity.class);
            intent.putExtra(IConfig.KEY_IMAGE_ENTITY, entity);
            activity.startActivityForResult(intent, IConfig.REQUEST_COMPLETE_ACTIVITY);
        }
    }

    /*===================================*/

    private void sendUpdateCompletedMsg(long delay) {
        if (uiHandler.hasMessages(MSG_UPDATE_COMPLETED)) {
            return; // 有相同时消息不处理
        }
        uiHandler.sendEmptyMessageDelayed(MSG_UPDATE_COMPLETED, delay); // 延迟更新，避免数据库频繁操作导致的UI频繁更新
    }

    private static final int MSG_UPDATE_COMPLETED = 2005;

    @Override
    protected void handleMessage(@NonNull Message msg) {
        if (msg.what == MSG_UPDATE_COMPLETED) {
            update();
        }
    }

    /*===================================*/

    private class StoreHandler extends Handler {

        static final int MSG_RECOLOR = 101;
        static final int MSG_DELETE = 102;

        StoreHandler(Looper looper) {
            super(looper);
        }

        void sendRecolorMsg(ImageEntityNew entity) {
            Message msg = obtainMessage();
            msg.what = MSG_RECOLOR;
            msg.obj = entity;
            sendMessage(msg);
        }

        void sendDeleteMsg(ImageEntityNew entity) {
            Message msg = obtainMessage();
            msg.what = MSG_DELETE;
            msg.obj = entity;
            sendMessage(msg);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == MSG_RECOLOR && msg.obj instanceof ImageEntityNew) {
                long startTs = SystemClock.elapsedRealtime();

                ImageEntityNew entity = (ImageEntityNew) msg.obj;
                PixelList pixelList = PixelHelper.getPixelList(entity);
                if (pixelList == null) {
                    return;
                }

                PixelHelper.resetDraw(pixelList);

                // 更新 pixelList
                FileUtils.writeObjectByZipJson(pixelList, entity.pixelsObjPath); // 存在时删除重新创建
                // 更新 colorImage
                PixelHelper.writeColorImage(entity.colorImagePath, pixelList, true); // 文件存在时删除重新创建
                // 更新数据库
                entity.completed = false;
//                entity.colorTime = 0; // colorTime 不变，否则就是删除了
                ImageDbManager.getInstance().updateProgressSync(entity);

                long duration = SystemClock.elapsedRealtime() - startTs;
                LogUtils.e(TAG, "--> MSG_RECOLOR  duration=" + MyTimeUtils.millis2StringGMT(duration, "HH:mm:ss SSS"));

                if (ContextManager.isSurvival(activity) && uiHandler != null) {
                    uiHandler.post(() -> {
                       startPixelActivity(entity);
                    });
                }
                return;
            }

            if (msg.what == MSG_DELETE && msg.obj instanceof ImageEntityNew) {
                long startTs = SystemClock.elapsedRealtime();

                ImageEntityNew entity = (ImageEntityNew) msg.obj;
                PixelList pixelList = PixelHelper.getPixelList(entity);
                if (pixelList == null) {
                    return;
                }

                PixelHelper.resetDraw(pixelList);

                // 更新 pixelList
                FileUtils.writeObjectByZipJson(pixelList, entity.pixelsObjPath); // 存在时删除重新创建
                // 更新 colorImage
                PixelHelper.writeColorImage(entity.colorImagePath, pixelList, true); // 文件存在时删除重新创建
                // 更新数据库
                entity.completed = false;
                entity.colorTime = 0; // reset
                ImageDbManager.getInstance().updateProgressSync(entity);

                long duration = SystemClock.elapsedRealtime() - startTs;
                LogUtils.e(TAG, "--> MSG_DELETE  duration=" + MyTimeUtils.millis2StringGMT(duration, "HH:mm:ss SSS"));
            }
        }
    }
}
