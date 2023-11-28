package com.project_ci01.app.base.view;

import com.project_ci01.app.base.utils.LogUtils;

import java.util.ArrayList;

public abstract class RefreshPresenter<Data> {

    protected String TAG = "RefreshPresenter";

    /**
     * 分页请求时，每页的item数
     */
    protected static final int ITEM_COUNT = 10;

    /**
     * 分页请求时的当前页码
     */
    protected int pageNum;

    /**
     * 所有页加起来的总条数
     */
    protected int totalSize;

    /**
     * 列表项数据
     */
    protected final ArrayList<Data> data = new ArrayList<>();

    protected OnDataChangedListener<Data> onDataChangedListener;

    /**
     * 正在更新数据
     */
    protected boolean isUpdating = false;


    /**
     * true表示 分页加载 已全部加载完成
     */
    protected boolean noMore = false;

    public void setOnDataChangedListener(OnDataChangedListener<Data> onDataChangedListener) {
        this.onDataChangedListener = onDataChangedListener;
    }

    public boolean hasMoreData() {
        return !noMore;
    }

    /**
     * 上拉刷新，重新加载数据
     */
    public void refreshData() {
        LogUtils.e(TAG, "-->  refreshData()  isUpdating=" + isUpdating);
        if (isUpdating) {
            return;
        }
        isUpdating = true;
        data.clear();

        pageNum = 1;
        totalSize = 0;
        noMore = false;

        requestData(pageNum, ITEM_COUNT);
    }

    /**
     * 下拉刷新，加载更多数据
     */
    public void loadMoreData() {
        LogUtils.e(TAG, "-->  loadMoreData()   isUpdating=" + isUpdating + "  noMore=" + noMore);
        if (noMore) {
            return;
        }
        if (isUpdating) {
            return;
        }
        isUpdating = true;
        pageNum++;

        requestData(pageNum, ITEM_COUNT);
    }



    protected void requestData(int pageNum, int itemCount) {
        /*
         1. 异步请求
         2. 请求失败 callback.onFaild(code, msg)
         3. 请求成功 callback.onSuccess(bean)
         */
    }

    protected void notifyDataChanged() {
        if (onDataChangedListener != null) {
            ArrayList<Data> cloneData = (ArrayList<Data>) data.clone();
            onDataChangedListener.onDataChanged(cloneData);
        }
    }

    public boolean checkAutoRefresh() {
        return data.size() < ITEM_COUNT; // 小于 1 页数据时允许自动刷新
    }

    public interface OnDataChangedListener<Data> {
        void onDataChanged(ArrayList<Data> data);
    }
}
