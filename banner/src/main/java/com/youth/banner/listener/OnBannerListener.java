package com.youth.banner.listener;

import androidx.recyclerview.widget.RecyclerView;

public interface OnBannerListener<T> {

    /**
     * 点击事件
     *
     * @param data     数据实体
     * @param position 当前位置
     */
    void onBannerClick(RecyclerView.ViewHolder holder, T data, int position);

}
