package com.project_ci01.app.base.view.recyclerview;

/**
 * 监听 RecyclerView item 的长按
 */
public interface OnItemLongClickListener<T>  {
    void onItemLongClick(int item, T object);

}
