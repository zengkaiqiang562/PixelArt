package com.project_ci01.app.base.view.recyclerview;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础的Adapter
 * M : 用于该 Adapter 的列表的数据类型,即 List<M> .
 * H : 即和 Adapter 绑定的 Holder 的类型.
 */
public abstract class BaseAdapter<M, H extends BaseHolder<M>> extends RecyclerView.Adapter<H> {

    protected List<M> datas;
    protected Context context;
    protected OnItemClickListener<H> listener;
    protected OnItemLongClickListener<H> longListener;

    /**
     * 设置数据,并设置点击回调接口
     *
     * @param datas     数据集合
     * @param listener 回调接口
     */
    public BaseAdapter(@Nullable List<M> datas, OnItemClickListener<H> listener, Context context) {
        this.datas = datas;
        this.context = context;
        if (this.datas == null) {
            this.datas = new ArrayList<>();
        }

        this.listener = listener;
    }

    /**
     * 设置数据,并设置点击回调接口
     *
     * @param datas         数据集合
     * @param listener     回调接口
     * @param longListener 回调接口
     */
    public BaseAdapter(List<M> datas, OnItemClickListener<H> listener, OnItemLongClickListener<H> longListener, Context context) {
        this(datas, listener, context);
        this.longListener = longListener;
    }

    @Override
    public void onBindViewHolder(final H holder, final int position) {
        if (datas != null && datas.size() > 0 && datas.size() >= position + 1)
            holder.setData(datas.get(position), position);

        if (listener != null) {
            holder.itemView.setOnClickListener(v -> {
                listener.onItemClick(position, holder);
            });
        }

        if (longListener != null) {
            holder.itemView.setOnLongClickListener(v -> {
                longListener.onItemLongClick(position, holder);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public Context getContext(){
        return context;
    }

    public void clear() {
        if (datas != null) datas.clear();
    }

    public List<M> getDatas() {
        return datas;
    }

    public void setDatas(List<M> datas) {
        this.datas = datas;
    }

    /**
     * 填充数据,此方法会清空以前的数据
     *
     * @param datas 需要显示的数据
     */
    public void setDatasAndNotify(List<M> datas) {
        this.datas = datas;
        notifyDataSetChanged();

    }

    /**
     * 获取一条数据
     *
     * @param holder item对应的holder
     * @return 该item对应的数据
     */
    public M getItem(H holder) {
        return datas.get(holder.getLayoutPosition());
    }

    /**
     * 获取一条数据
     *
     * @param position item的位置
     * @return item对应的数据
     */
    public M getItem(int position) {
        return datas.get(position);
    }
}