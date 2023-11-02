package com.project_m1142.app.ui.history;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.constant.MemoryConstants;
import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.ConvertUtils;
import com.project_m1142.app.R;
import com.project_m1142.app.base.utils.MyConvertUtils;
import com.project_m1142.app.base.utils.MyTimeUtils;
import com.project_m1142.app.base.view.recyclerview.BaseAdapter;
import com.project_m1142.app.base.view.recyclerview.BaseHolder;
import com.project_m1142.app.base.view.recyclerview.OnItemClickListener;
import com.project_m1142.app.dao.TestHistoryDbManager;
import com.project_m1142.app.ui.activity.TestHistoryActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends BaseAdapter<IItem, BaseHolder<IItem>> {


    public HistoryAdapter(OnItemClickListener<BaseHolder<IItem>> listener, Context context) {
        super(null, listener, context);
        setItems(null);
    }

    public void setItems(@Nullable List<IItem> items) {
        if (items == null) {
            items = new ArrayList<>();
        }

        if (items.isEmpty()) {
            items.add(new EmptyItem());
            items.add(new TestItem());
        } else {
            items.add(new ClearItem());
        }

        setDatasAndNotify(items);
    }

    @NonNull
    @Override
    public BaseHolder<IItem> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case IItem.TYPE_CLEAR:
                return new ClearHolder(parent);
            case IItem.TYPE_TEST:
                return new TestHolder(parent);
            case IItem.TYPE_EMPTY:
                return new EmptyHolder(parent);
            default:
                return new HistoryHolder(parent);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return datas.get(position).getType();
    }

    public static class HistoryHolder extends BaseHolder<IItem> {

        private final TextView tvTime;
        private final TextView tvNetName;
        private final SeekBar barRx;
        private final TextView tvRx;
        private final SeekBar barTx;
        private final TextView tvTx;
        private final TextView tvDevice;
        private final TextView tvDelay;

        public HistoryItem item;

        public HistoryHolder(ViewGroup parent) {
            super(parent, R.layout.item_history);

            tvTime = getView(R.id.history_item_time);
            tvNetName = getView(R.id.history_item_net_name);
            barRx = getView(R.id.history_item_rx_bar);
            tvRx = getView(R.id.history_item_rx_value);
            barTx = getView(R.id.history_item_tx_bar);
            tvTx = getView(R.id.history_item_tx_value);
            tvDevice = getView(R.id.history_item_device);
            tvDelay = getView(R.id.history_item_delay);

            barRx.setEnabled(false);
            barTx.setEnabled(false);
        }

        @Override
        public void setData(IItem data, int position) {
            if (!(data instanceof HistoryItem)) {
                return;
            }

            item = (HistoryItem) data;

            tvTime.setText(fitCreateTime(item.entity.createTime));
            tvNetName.setText(item.entity.netName);

            double rxRateMB = ConvertUtils.byte2MemorySize(item.entity.rxRate, MemoryConstants.MB);
            int rxProgress = (int) (rxRateMB < 1 ? 1 : rxRateMB > 100 ? 100 : rxRateMB);
            barRx.setProgress(rxProgress);

            double txRateMB = ConvertUtils.byte2MemorySize(item.entity.txRate, MemoryConstants.MB);
            int txProgress = (int) (txRateMB < 1 ? 1 : txRateMB > 100 ? 100 : txRateMB);
            barTx.setProgress(txProgress);

            String fitRxRate = MyConvertUtils.byte2FitMemorySize(item.entity.rxRate, 1) + "ps";
            String fitTxRate = MyConvertUtils.byte2FitMemorySize(item.entity.txRate, 1) + "ps";
            tvRx.setText(fitRxRate);
            tvTx.setText(fitTxRate);

            tvDevice.setText(getContext().getString(R.string.test_device, item.entity.device));
            String fitDelay = item.entity.delay + "ms";
            tvDelay.setText(fitDelay);
        }

        private String fitCreateTime(long createTime) {
            if (MyTimeUtils.isInOneHour(createTime)) {
                long offsetMinutes = ConvertUtils.millis2TimeSpan(System.currentTimeMillis() - createTime, TimeConstants.MIN);
                return offsetMinutes + " minutes ago," + MyTimeUtils.millis2String(createTime, "H:mm a", Locale.ENGLISH);
            } else if (MyTimeUtils.isInToday(createTime)) {
                return "today," + MyTimeUtils.millis2String(createTime, "H:mm a", Locale.ENGLISH);
            } else if (MyTimeUtils.isInYesterday(createTime)) {
                return "yesterday," + MyTimeUtils.millis2String(createTime, "H:mm a", Locale.ENGLISH);
            } else {
                return MyTimeUtils.millis2String(createTime, "MM/dd/yyyy,H:mm a", Locale.ENGLISH);
            }
        }
    }

    public static class ClearHolder extends BaseHolder<IItem> {

        private final TextView tvClear;

        public ClearHolder(ViewGroup parent) {
            super(parent, R.layout.item_clear);
            tvClear = getView(R.id.history_item_clear);
        }

        @Override
        public void setData(IItem data, int position) {
            tvClear.setOnClickListener(v -> {
                TestHistoryDbManager.getInstance().deleteAll();
                if (getContext() instanceof TestHistoryActivity) {
                    ((TestHistoryActivity) getContext()).sendUpdateHistory();
                }
            });
        }
    }

    public static class EmptyHolder extends BaseHolder<IItem> {

        public EmptyHolder(ViewGroup parent) {
            super(parent, R.layout.item_empty);
        }

        @Override
        public void setData(IItem data, int position) {

        }
    }

    public static class TestHolder extends BaseHolder<IItem> {

        private final TextView tvTest;

        public TestHolder(ViewGroup parent) {
            super(parent, R.layout.item_test);
            tvTest = getView(R.id.history_item_test);
        }

        @Override
        public void setData(IItem data, int position) {
            tvTest.setOnClickListener(v -> {
                if (getContext() instanceof TestHistoryActivity) {
                    ((TestHistoryActivity) getContext()).startTestExecActivity();
                }
            });
        }
    }
}
