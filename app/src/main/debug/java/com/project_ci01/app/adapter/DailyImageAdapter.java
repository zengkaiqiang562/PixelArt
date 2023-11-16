package com.project_ci01.app.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.TimeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project_ci01.app.adapter.daily.HeaderDailyItem;
import com.project_ci01.app.adapter.daily.IDailyItem;
import com.project_ci01.app.adapter.daily.ImageDailyItem;
import com.project_ci01.app.dao.ImageEntity;
import com.project_m1142.app.R;
import com.project_m1142.app.base.utils.LogUtils;
import com.project_m1142.app.base.utils.MyTimeUtils;
import com.sunfusheng.ExpandableGroupRecyclerViewAdapter;
import com.sunfusheng.GroupAdapterUtils;
import com.sunfusheng.GroupViewHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DailyImageAdapter extends ExpandableGroupRecyclerViewAdapter<IDailyItem> {

    public DailyImageAdapter(Context context) {
        super(context);
    }

    public void setImageEntities(List<ImageEntity> entities, boolean reset) { // reset : 重置折叠状态，即当月展开，其他月折叠
//        List<Integer>
        groups.clear();
        if (entities == null || entities.isEmpty()) {
            notifyDataSetChanged();
            return;
        }

        // 按年月分组
        Map<String, List<ImageEntity>> monthMap = new HashMap<>();
        for (ImageEntity entity : entities) {
            String monthWithYear = MyTimeUtils.millis2String(entity.createTime, "MMMM - yyyy", Locale.ENGLISH);
            List<ImageEntity> monthEntities = monthMap.get(monthWithYear);
            if (monthEntities == null) {
                monthEntities = new ArrayList<>();
                monthMap.put(monthWithYear, monthEntities);
            }
            monthEntities.add(entity);
        }

        List<Map.Entry<String, List<ImageEntity>>> entryList = new ArrayList<>(monthMap.entrySet());

        Collections.sort(entryList, (o1, o2) -> { // createTime 降序
            long o1Time = o1.getValue().get(0).createTime;
            long o2Time = o2.getValue().get(0).createTime;
            return Long.compare(o2Time, o1Time);
        });

        for (Map.Entry<String, List<ImageEntity>> entry : entryList) {
            String monthWithYear = entry.getKey();
            List<ImageEntity> monthEntities = entry.getValue();
            List<IDailyItem> items = new ArrayList<>();
            items.add(new HeaderDailyItem(monthWithYear, monthEntities)); // 月份 Item
            for (ImageEntity entity : monthEntities) {
                items.add(new ImageDailyItem(entity)); // Image Item
            }
            groups.add(items);
        }

//        setGroups(groups);
        GroupAdapterUtils.checkGroupsData(groups, minCountPerGroup());

        for (int i = 0; i < groupsCount(); i++) {
            if (reset) {
                if (i == 0) {
                    expandGroup(i);
                } else {
                    collapseGroup(i);
                }
            } else {
                if (!isExpand(i)) {
                    collapseGroup(i);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public boolean showHeader() {
        return true;
    }

    @Override
    public boolean showFooter() {
        return false;
    }

    @Override
    public int getHeaderLayoutId(int viewType) {
        return R.layout.item_daily_header;
    }

    @Override
    public int getChildLayoutId(int viewType) {
        return R.layout.item_daily_image;
    }

    @Override
    public int getFooterLayoutId(int viewType) {
        return R.layout.item_daily_footer;
    }

    @Override
    public void onBindHeaderViewHolder(GroupViewHolder holder, IDailyItem item, int groupPosition) {
        if (!(item instanceof HeaderDailyItem)) {
            return;
        }

        HeaderDailyItem headerItem = (HeaderDailyItem) item;

        holder.setImageDrawable(R.id.arrow, isExpand(groupPosition) ? new ColorDrawable(Color.BLACK) : new ColorDrawable(Color.RED));

        long timeForMonth = headerItem.entities.get(0).createTime;
        if (MyTimeUtils.isCurrentMonth(timeForMonth)) {
            String fitMonth = TimeUtils.millis2String(timeForMonth, "MMMM");
            holder.setText(R.id.month, fitMonth);
        } else {
            holder.setText(R.id.month, headerItem.month);
        }

        int completeCount = 0;
        for (ImageEntity entity : headerItem.entities) {
            if (entity.completed) {
                ++completeCount;
            }
        }
        String statistic = completeCount + "/" + headerItem.entities.size();
        holder.setText(R.id.statistic, statistic);

        /**
         * 重置Tgg，让 sticky view 能够更新
         * @see com.sunfusheng.StickyHeaderDecoration#onDrawOver(Canvas, RecyclerView, RecyclerView.State) 
         */
        holder.itemView.setTag(null);
    }

    @Override
    public void onBindChildViewHolder(GroupViewHolder holder, IDailyItem item, int groupPosition, int childPosition) {
        if (!(item instanceof ImageDailyItem)) {
            return;
        }

        ImageDailyItem imageItem = (ImageDailyItem) item;

        String day = TimeUtils.millis2String(imageItem.entity.createTime, "dd");
        holder.setText(R.id.day, day);

        ImageView image = holder.get(R.id.image);

        Glide.with(context)
                .load(imageItem.entity.colorImagePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不走本地缓存
                .skipMemoryCache(true) // 不走内存缓存
                .into(image);
    }

    @Override
    public void onBindFooterViewHolder(GroupViewHolder holder, IDailyItem item, int groupPosition) {}
}
