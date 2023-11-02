package com.project_m1142.app.base.view.recyclerview;

import android.content.Context;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 基础的 ViewHolder
 * M : 用于该 Adapter 的列表的数据类型,即 List<M> .
 * H : 即和 Adapter 绑定的 Holder 的类型.
 */
public abstract class BaseHolder<M> extends RecyclerView.ViewHolder {

    public BaseHolder(ViewGroup parent, @LayoutRes int resId) {
        super(LayoutInflater.from(parent.getContext()).inflate(resId, parent, false));
    }

    public BaseHolder(ViewGroup parent, View view) {
        super(view);
    }

    /**
     * 获取布局中的View
     * @param viewId view的Id
     * @param <T> View的类型
     * @return view
     */
    protected <T extends View> T getView(@IdRes int viewId){
        return itemView.findViewById(viewId);
    }

    /**
     * 获取Context实例
     * @return context
     */
    protected Context getContext() {
        return itemView.getContext();
    }

    /**
     * 设置数据
     * @param data 要显示的数据对象
     */
    public abstract void setData(M data,int position);
}