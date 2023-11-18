package com.project_ci01.app.base.view.recyclerview;

/**
 * 监听 RecyclerView item 的点击
 */
public interface OnItemClickListener<T>  {
    void onItemClick(int item, T object);
}
