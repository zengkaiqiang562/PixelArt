package com.project_m1142.app.ui.wifi;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.project_m1142.app.R;
import com.project_m1142.app.base.permission.PermissionHelper;
import com.project_m1142.app.base.view.recyclerview.BaseAdapter;
import com.project_m1142.app.base.view.recyclerview.BaseHolder;
import com.project_m1142.app.base.view.recyclerview.OnItemClickListener;
import com.project_m1142.app.ui.activity.WiFiActivity;
import com.project_m1142.app.wifi.ext.WifiHelper;

import java.util.ArrayList;
import java.util.List;

public class WifiAdapter extends BaseAdapter<IItem, BaseHolder<IItem>> {


    public WifiAdapter(OnItemClickListener<BaseHolder<IItem>> listener, Context context) {
        super(null, listener, context);
        setItems(null);
    }

    public void setItems(@Nullable List<IItem> items) {
        if (items == null) {
            items = new ArrayList<>();
        }

        if (!items.isEmpty()) { // 分类
            List<IItem> availableItems = new ArrayList<>();
            List<IItem> passworkItems = new ArrayList<>();
            IItem connectedItem = null;
            for (IItem item : items) {
                if (item instanceof WifiItem) {
                    WifiItem wifiItem = (WifiItem) item;
                    if (WifiHelper.isWifiConnected(context, wifiItem.wifiEntity)) {
                        connectedItem = item;
                    } else if (wifiItem.wifiEntity.isSaved()) {
                        availableItems.add(item);
                    } else {
                        passworkItems.add(item);
                    }
                }
            }
            items.clear();
            items.add(new TitleItem(R.string.available));
            if (connectedItem != null) {
                items.add(connectedItem);
            }
            items.addAll(availableItems);
            items.add(new TitleItem(R.string.passwork));
            items.addAll(passworkItems);
        }

        if (items.isEmpty() && !PermissionHelper.checkLocationPermission(context)) {
            items.add(new PermissionItem());
        }

        setDatasAndNotify(items);
    }

    @NonNull
    @Override
    public BaseHolder<IItem> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case IItem.TYPE_TITLE:
                return new TitleHolder(parent);
            case IItem.TYPE_PERMISSION:
                return new PermissionHolder(parent);
            default:
                return new WifiHolder(parent);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return datas.get(position).getType();
    }

    public static class WifiHolder extends BaseHolder<IItem> {

        private final TextView tvName;
        private final TextView tvSignal;
        private final ImageView ivMore;

        public WifiItem wifiItem;

        public WifiHolder(ViewGroup parent) {
            super(parent, R.layout.item_wifi);

            tvName = getView(R.id.wifi_item_ssid);
            tvSignal = getView(R.id.wifi_item_signal);
            ivMore = getView(R.id.wifi_item_more);
        }

        @Override
        public void setData(IItem data, int position) {
            if (!(data instanceof WifiItem)) {
                return;
            }

            ivMore.setOnClickListener(null); // reset

            wifiItem = (WifiItem) data;

            tvName.setText(wifiItem.wifiEntity.ssid);
            String fitSignal = wifiItem.wifiEntity.level + "dBm";
            tvSignal.setText(fitSignal);

            // 没有连接 && 没有保存 && 加密wifi
            if (!WifiHelper.isWifiConnected(getContext(), wifiItem.wifiEntity) && !wifiItem.wifiEntity.isSaved() && wifiItem.wifiEntity.isEncrypt) {
                ivMore.setOnClickListener(v -> {
                    if (getContext() instanceof WiFiActivity) {
                        ((WiFiActivity) getContext()).showWifiDialogTwo(wifiItem.wifiEntity);
                    }
                });
            }
        }
    }

    public static class PermissionHolder extends BaseHolder<IItem> {

        private final TextView tvPermission;

        public PermissionHolder(ViewGroup parent) {
            super(parent, R.layout.item_wifi_permission);
            tvPermission = getView(R.id.wifi_item_permission);
        }

        @Override
        public void setData(IItem data, int position) {
            tvPermission.setOnClickListener(v -> {
                if (getContext() instanceof WiFiActivity) {
                    PermissionHelper.applyLocationPermission((Activity) getContext(), true, granted -> {
                        if (granted) {
                            ((WiFiActivity) getContext()).scanWifi();
                        }
                    });
                }
            });
        }
    }

    public static class TitleHolder extends BaseHolder<IItem> {

        private final TextView tvTitle;

        public TitleHolder(ViewGroup parent) {
            super(parent, R.layout.item_wifi_title);
            tvTitle = getView(R.id.wifi_item_title);
        }

        @Override
        public void setData(IItem data, int position) {
            if (data instanceof TitleItem) {
                tvTitle.setText(((TitleItem) data).titleRes);
            }
        }
    }

}
