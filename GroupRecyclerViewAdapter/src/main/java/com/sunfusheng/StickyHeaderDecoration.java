package com.sunfusheng;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author sunfusheng on 2018/3/7.
 */
@SuppressWarnings("unchecked")
public class StickyHeaderDecoration extends RecyclerView.ItemDecoration {

    private GroupRecyclerViewAdapter mGroupAdapter;
    private View vStickyView;
    private View vCurrStickyView;
    private View vNextStickyView;

    private int mCurrGroupPosition;
    private Rect mStickyRect = new Rect();
    private GestureDetector mGestureDetector;

    private String mCurrStickyViewHashCode;
    private String mNextStickyViewHashCode;

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (layoutManager == null || adapter == null) {
            return;
        }

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        mGroupAdapter = (GroupRecyclerViewAdapter) adapter;

        int itemCount = state.getItemCount();
        if (itemCount <= 0) {
            return;
        }

        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
        if (firstVisibleItemPosition == RecyclerView.NO_POSITION || lastVisibleItemPosition == RecyclerView.NO_POSITION) {
            return;
        }

        mCurrGroupPosition = mGroupAdapter.getGroupPosition(firstVisibleItemPosition);
        int nextGroupPosition = mCurrGroupPosition + 1;
        int currStickyPosition = mGroupAdapter.getGroupHeaderPosition(mCurrGroupPosition);
        int nextStickyPosition = mGroupAdapter.getGroupHeaderPosition(nextGroupPosition);
        if (nextStickyPosition >= itemCount) {
            nextStickyPosition = currStickyPosition;
        }

        RecyclerView.ViewHolder currViewHolder = parent.findViewHolderForAdapterPosition(currStickyPosition);
        if (currViewHolder != null && (currViewHolder.itemView.getTag() == null || (int) currViewHolder.itemView.getTag() != mCurrGroupPosition)) {
            vCurrStickyView = currViewHolder.itemView;
            vCurrStickyView.setTag(mCurrGroupPosition);
        }

        if (vCurrStickyView == null) {
            return;
        }
        int stickyViewWidth = vCurrStickyView.getWidth();
        int stickyViewHeight = vCurrStickyView.getHeight();

        if (mCurrStickyViewHashCode == null || mCurrStickyViewHashCode.equals(mNextStickyViewHashCode)) {
            vCurrStickyView = loadStickyView(parent, currStickyPosition, stickyViewWidth, stickyViewHeight);
            vCurrStickyView.setTag(mCurrGroupPosition);
        }

        // 在 Adapter 重写的 onBindViewHolder 方法中可能会重置 Tag，此时重新生成 StickyView
        if (vCurrStickyView.getTag() == null || (int) vCurrStickyView.getTag() != mCurrGroupPosition) {
            vCurrStickyView = loadStickyView(parent, currStickyPosition, stickyViewWidth, stickyViewHeight);
            vCurrStickyView.setTag(mCurrGroupPosition);
        }

        RecyclerView.ViewHolder nextViewHolder = parent.findViewHolderForLayoutPosition(nextStickyPosition);
        if (nextViewHolder != null) {
            vNextStickyView = nextViewHolder.itemView;
            vNextStickyView.setTag(nextGroupPosition);
        }

        mCurrStickyViewHashCode = Integer.toHexString(System.identityHashCode(vCurrStickyView));
        mNextStickyViewHashCode = Integer.toHexString(System.identityHashCode(vNextStickyView));

        int nextStickyViewTop = -1;
        if (vNextStickyView != null) {
            nextStickyViewTop = vNextStickyView.getTop();
        }

        int translateY = 0;
        if (nextStickyViewTop > 0 && nextStickyViewTop < stickyViewHeight && nextGroupPosition < mGroupAdapter.getGroups().size()) {
            translateY = nextStickyViewTop - stickyViewHeight;
        }
        canvas.translate(0, translateY);
        vCurrStickyView.draw(canvas);

        mStickyRect.left = 0;
        mStickyRect.top = 0;
        mStickyRect.right = stickyViewWidth;
        mStickyRect.bottom = stickyViewHeight + translateY;
        handleGestureDetector(parent);
    }

    private View loadStickyView(@NonNull RecyclerView parent, int currStickyPosition, int width, int height) {
        if (vStickyView == null) {
            vStickyView = mGroupAdapter.inflater.inflate(mGroupAdapter.getHeaderLayoutId(GroupRecyclerViewAdapter.TYPE_HEADER), parent, false);
        }
        GroupViewHolder viewHolder = new GroupViewHolder(vStickyView, parent);
        mGroupAdapter.onBindViewHolder(viewHolder, currStickyPosition);
        View itemView = viewHolder.itemView;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        itemView.setLayoutParams(layoutParams);
        itemView.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        itemView.layout(0, -height, width, 0);
        return itemView;
    }

    private void handleGestureDetector(@NonNull RecyclerView parent) {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(parent.getContext(), simpleOnGestureListener);
            parent.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
                long startTime;

                @Override
                public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                    if (vCurrStickyView != null && vCurrStickyView.isPressed() && e.getAction() == MotionEvent.ACTION_UP) {
                        vCurrStickyView.setPressed(false);
                    }
                    return mGestureDetector.onTouchEvent(e);
                }

                @Override
                public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                    super.onTouchEvent(rv, e);
                    if (vCurrStickyView == null) {
                        return;
                    }

                    if (e.getAction() == MotionEvent.ACTION_DOWN) {
                        onSingleTapUpCalled = false;
                        onLongPressCalled = false;
                        startTime = System.currentTimeMillis();
                        if (!vCurrStickyView.isPressed()) {
                            vCurrStickyView.setPressed(true);
                        }
                    } else if (e.getAction() == MotionEvent.ACTION_UP) {
                        long timeInterval = System.currentTimeMillis() - startTime;
                        if (vCurrStickyView.isPressed()) {
                            vCurrStickyView.setPressed(false);
                        }

                        if (timeInterval < 400) {
                            onLongPressCalled = true;
                            click(e);
                        } else {
                            onSingleTapUpCalled = true;
                            longClick(e);
                        }
                    }
                }
            });
        }
    }

    private boolean onSingleTapUpCalled;
    private boolean onLongPressCalled;

    private boolean click(MotionEvent e) {
        GroupRecyclerViewAdapter adapter = mGroupAdapter;
        if (isValidTouch(e) && adapter != null && adapter.onItemClickListener != null) {
            if (onSingleTapUpCalled) {
                return false;
            }
            onSingleTapUpCalled = true;
            vCurrStickyView.setPressed(false);
            adapter.onItemClickListener.onItemClick(adapter, adapter.getItem(mCurrGroupPosition, 0), mCurrGroupPosition, 0);
            return true;
        }
        return false;
    }

    private void longClick(MotionEvent e) {
        GroupRecyclerViewAdapter adapter = mGroupAdapter;
        if (isValidTouch(e) && adapter != null && adapter.onItemLongClickListener != null) {
            if (onLongPressCalled) {
                return;
            }
            onLongPressCalled = true;
            vCurrStickyView.setPressed(false);
            adapter.onItemLongClickListener.onItemLongClick(adapter, adapter.getItem(mCurrGroupPosition, 0), mCurrGroupPosition, 0);
        }
    }

    private GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            onSingleTapUpCalled = false;
            onLongPressCalled = false;
            if (isValidTouch(e)) {
                vCurrStickyView.setPressed(true);
                return true;
            }
            return super.onDown(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (click(e)) {
                return true;
            }
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            longClick(e);
        }
    };

    private boolean isValidTouch(MotionEvent e) {
        Rect rect = mStickyRect;
        float x = e.getX();
        float y = e.getY();
        return x > rect.left && x < rect.right && y > rect.top && y < rect.bottom;
    }
}
