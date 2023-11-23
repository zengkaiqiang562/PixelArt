package com.project_ci01.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project_ci01.app.R;
import com.project_ci01.app.base.common.CommonCallback;
import com.project_ci01.app.base.common.CompleteCallback;
import com.project_ci01.app.base.manage.ContextManager;
import com.project_ci01.app.base.permission.PermissionHelper;
import com.project_ci01.app.base.utils.MyImageUtils;
import com.project_ci01.app.base.utils.MyTimeUtils;
import com.project_ci01.app.base.view.dialog.DialogHelper;
import com.project_ci01.app.base.view.dialog.SimpleDialogListener;
import com.project_ci01.app.config.IConfig;
import com.project_ci01.app.dao.ImageDbManager;
import com.project_ci01.app.dao.ImageEntityNew;
import com.project_ci01.app.base.view.BaseActivity;
import com.project_ci01.app.databinding.ActivityCompleteBinding;
import com.project_ci01.app.dialog.CompleteSaveDialog;
import com.project_ci01.app.dialog.CompleteShareDialog;
import com.project_ci01.app.dialog.MineCompleteRecolorDialog;

import java.io.File;
import java.util.Collections;
import java.util.Random;

public class CompleteActivity extends BaseActivity {

    private ActivityCompleteBinding binding;

    private ImageEntityNew entity;

    private ImageEntityNew uncolorEntityOne;
    private ImageEntityNew uncolorEntityTwo;

    private CompleteSaveDialog saveDialog;

    private CompleteShareDialog shareDialog;

    @Override
    protected String tag() {
        return "CompleteActivity";
    }

    @Override
    protected void setContentView() {
        binding = ActivityCompleteBinding.inflate(getLayoutInflater());
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
    }

    private void init(Intent intent) {
        if (intent != null) {
            entity = intent.getParcelableExtra(IConfig.KEY_IMAGE_ENTITY);
        }

        if (entity != null) {
            Glide.with(this)
                    .load(entity.colorImagePath)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 不走本地缓存
                    .skipMemoryCache(true) // 不走内存缓存
                    .into(binding.completeImage);
        }

        findUncolorEntities();


        binding.morePic.setOnClickListener(v -> {
            startMainActivity();
        });

        binding.uncolor1.setOnClickListener(v -> {
            if (uncolorEntityOne != null) {
                startPixelActivity(uncolorEntityOne);
            }
        });

        binding.uncolor2.setOnClickListener(v -> {
            if (uncolorEntityTwo != null) {
                startPixelActivity(uncolorEntityTwo);
            }
        });

        binding.llSave.setOnClickListener(v -> {
            showSaveDialog(entity);
        });

        binding.llShare.setOnClickListener(v -> {
            showShareDialog(entity);
        });
    }

    public void startMainActivity() {
        if (canTurn()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void findUncolorEntities() {
        ImageDbManager.getInstance().queryWithoutColor(entities -> {
            if (!ContextManager.isSurvival(this)) {
                return;
            }

            Collections.shuffle(entities, new Random());
            if (entities.size() > 1) {
                uncolorEntityOne = entities.get(0);
                uncolorEntityTwo = entities.get(1);

                Glide.with(this)
                        .load(uncolorEntityOne.colorImagePath)
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // 不走本地缓存
                        .skipMemoryCache(true) // 不走内存缓存
                        .into(binding.uncolor1);

                Glide.with(this)
                        .load(uncolorEntityTwo.colorImagePath)
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // 不走本地缓存
                        .skipMemoryCache(true) // 不走内存缓存
                        .into(binding.uncolor2);
            }
        });
    }

    private void startPixelActivity(@NonNull ImageEntityNew entity) {
        if (canTurn()) {
            Intent intent = new Intent(this, PixelActivity.class);
            intent.putExtra(IConfig.KEY_IMAGE_ENTITY, entity);
            startActivityForResult(intent, IConfig.REQUEST_PIXEL_ACTIVITY);
        }
    }

    private void showSaveDialog(@NonNull ImageEntityNew entity) {
        saveDialog = DialogHelper.showDialog(this, saveDialog, CompleteSaveDialog.class, new SimpleDialogListener<CompleteSaveDialog>() {

            @Override
            public void onShowBefore(CompleteSaveDialog dialog) {
                dialog.setImageEntity(entity);
            }

            @Override
            public void onConfirm() {
                saveImage(saveDialog.getImageEntity(), true ,null);
            }
        });
    }

    private void showShareDialog(@NonNull ImageEntityNew entity) {
        shareDialog = DialogHelper.showDialog(this, shareDialog, CompleteShareDialog.class, new SimpleDialogListener<CompleteShareDialog>() {

            @Override
            public void onShowBefore(CompleteShareDialog dialog) {
                dialog.setImageEntity(entity);
            }

            @Override
            public void onConfirm() {
                saveImage(shareDialog.getImageEntity(), false, saveFile -> {
                    if (saveFile != null) {
                        Intent shareImageIntent = IntentUtils.getShareImageIntent(saveFile);
                        startActivity(shareImageIntent);
                    }
                });
            }
        });
    }

    private void saveImage(@NonNull ImageEntityNew imageEntity, boolean toast, CompleteCallback<File> callback) {
        PermissionHelper.applyStorage13Permission(CompleteActivity.this, granted -> {
            if (granted) {
                String saveImagePath = imageEntity.saveImagePath;
                File saveFile;
                if (TextUtils.isEmpty(saveImagePath) || !ImageUtils.isImage(saveFile = new File(saveImagePath))) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inMutable = true;
                    Bitmap bitmap = BitmapFactory.decodeFile(imageEntity.colorImagePath, options);
                    Bitmap dstBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(dstBitmap);
                    canvas.drawColor(Color.WHITE);
                    canvas.drawBitmap(bitmap, 0, 0, null);
                    Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
                    String fileName = TimeUtils.millis2String(System.currentTimeMillis(), "yyyMMdd_HHmmss") + "_" + imageEntity.imageId + "." + format.name();
                    saveFile = MyImageUtils.save2Album(dstBitmap, getString(R.string.app_name), fileName, Bitmap.CompressFormat.PNG, 100, true);
                    canvas.setBitmap(null);
                    bitmap.recycle();
                    if (saveFile != null) {
                        imageEntity.saveImagePath = saveFile.getAbsolutePath();
                        ImageDbManager.getInstance().updateSaveImagePath(imageEntity);
                        if (toast) {
                            ToastUtils.showShort("Saved");
                        }
                    }
                } else {
                    if (toast) {
                        ToastUtils.showShort("Already saved");
                    }
                }

                if (callback != null) {
                    callback.onCompleted(saveFile);
                }
            }
        });
    }
}
