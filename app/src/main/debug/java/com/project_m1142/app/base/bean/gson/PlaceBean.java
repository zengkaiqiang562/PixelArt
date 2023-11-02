package com.project_m1142.app.base.bean.gson;

import androidx.annotation.Nullable;

import com.project_m1142.app.base.utils.LogUtils;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlaceBean {
    private static final String TAG = "PlaceBean";

    @SerializedName("enable")
    private boolean enable;

    @SerializedName("place")
    private String place;

    @SerializedName("units")
    private List<UnitBean> unitBeans;


    public boolean isEnable() {
        return enable;
    }

    public String getPlace() {
        return place;
    }

    public boolean isUnitBeansEmpty() {
        return unitBeans == null || unitBeans.isEmpty();
    }


    /**
     * @return 返回权重最高的广告单元
     */
    @Nullable
    public UnitBean getMostWeightUnitBean() {
        return getLessWeightUnitBean(null);
    }

    /**
     * @param moreWeightUnitBean 为 null 时返回权重最高的广告单元
     * @return 返回比参数广告单元权重更低的广告单元
     */
    @Nullable
    public UnitBean getLessWeightUnitBean(@Nullable UnitBean moreWeightUnitBean) {
        if (unitBeans == null || unitBeans.isEmpty()) {
            return null;
        }
        List<UnitBean> sorts = getSortUnitBeans();
        if (sorts == null || sorts.isEmpty()) {
            return null;
        }
        if (moreWeightUnitBean == null) { // 没有权重更高的，则返回权重最高
            return sorts.get(0);
        }

        int index = sorts.indexOf(moreWeightUnitBean);

        if (index == -1) { // 没有权重更高的，则返回权重最高
            return sorts.get(0);
        }

        if (index == sorts.size() - 1) {
            return null; // 没有权重更低的，则返回null
        }

        return sorts.get(index + 1); // 返回权重更低的
    }

    /**
     * @return 按权重从大到小排序后的广告单元集合
     */
    @Nullable
    public List<UnitBean> getSortUnitBeans() {
        sortUnitBeans();
        LogUtils.e(TAG, "--> place=" + place + "  unitBeans=" + unitBeans);
        return unitBeans;
    }

    private void sortUnitBeans() {
        if (unitBeans == null || unitBeans.isEmpty()) {
            return;
        }
        Collections.sort(unitBeans, new Comparator<UnitBean>() {
            @Override
            public int compare(UnitBean o1, UnitBean o2) {
                try {
                    int w1 = Integer.parseInt(o1.getWeight());
                    int w2 = Integer.parseInt(o2.getWeight());
                    return w2 - w1;
                } catch (Exception e) {
                    return 0;
                }
            }
        });
    }

//    @Override
//    public String toString() {
//        return "PlaceBean{" +
//                "enable=" + enable +
//                ", place='" + place + '\'' +
//                '}';
//    }


    @Override
    public String toString() {
        return "PlaceBean{" +
                "enable=" + enable +
                ", place='" + place + '\'' +
                ", unitBeans=" + unitBeans +
                '}';
    }
}
