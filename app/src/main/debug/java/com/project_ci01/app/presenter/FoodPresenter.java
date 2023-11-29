package com.project_ci01.app.presenter;

import com.project_ci01.app.base.utils.LogUtils;
import com.project_ci01.app.base.view.FragmentPresenter;
import com.project_ci01.app.dao.Category;
import com.project_ci01.app.dao.ImageDbManager;
import com.project_ci01.app.dao.ImageEntityNew;
import com.project_ci01.app.fragment.category.FoodFragment;

import java.util.List;

public class FoodPresenter extends FragmentPresenter<FoodFragment, ImageEntityNew> {


    public FoodPresenter(FoodFragment fragment) {
        super(fragment);
        TAG = "FoodPresenter";
    }

    @Override
    protected void requestData(int pageNum, int itemCount) {
        ImageDbManager.getInstance().queryByCategory(Category.FOOD.catName, pageNum, itemCount, (totalSize, result) -> {
            LogUtils.e(TAG, "-->  requestData()   onCompleted()  pageNum=" + pageNum + ", itemCount=" + itemCount + ", totalSize=" + totalSize + ", result.size=" + result.size() + ", result=" + result);
            handleData(totalSize, result);
        });
    }

    private void handleData(int totalSize, List<ImageEntityNew> result) {
        this.totalSize = totalSize;

        if (data.isEmpty()) {
            data.addAll(result);
        } else {
            for (ImageEntityNew entity : result) {
                if (!data.contains(entity)) {
                    data.add(entity);
                }/* else {
                    // entity 中的填色进度可能发生了改变，需要将 data 中的替换掉
                    int index = data.indexOf(entity);
                    data.remove(index);
                    data.add(index, entity);
                }*/
            }
        }

        LogUtils.e(TAG, "-->  handleData()   data.size=" + data.size());
        for (ImageEntityNew entity : data) {
            LogUtils.e(TAG, "---->  handleData()  data item ##   entity=" + entity);
        }
        noMore = data.size() != 0 && data.size() >= totalSize;
        isUpdating = false;
        notifyDataChanged();
    }

}
