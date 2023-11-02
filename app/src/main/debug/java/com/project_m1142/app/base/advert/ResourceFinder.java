package com.project_m1142.app.base.advert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//import com.applovin.mediation.ads.MaxAdView;
import com.project_m1142.app.base.applovin.MaxResource;
import com.project_m1142.app.base.bean.gson.PlaceBean;
import com.project_m1142.app.base.bean.gson.UnitBean;
import com.project_m1142.app.base.utils.LogUtils;
//import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class ResourceFinder {

    private static final String TAG = "ResourceFinder";

    /**
     * 参数集合 按权重从大到小排序
     */
    private void sortResources(@NonNull List<Resource<?>> resources) {
        if (resources.isEmpty()) {
            return;
        }

        Collections.sort(resources, new Comparator<Resource<?>>() {
            @Override
            public int compare(Resource<?> res1, Resource<?> res2) {
                try {
                    int w1 = Integer.parseInt(res1.getUnitBean().getWeight());
                    int w2 = Integer.parseInt(res2.getUnitBean().getWeight());
                    return w2 - w1;
                } catch (Exception e) {
                    return 0;
                }
            }
        });
    }

    /**
     * @return 比较并返回所有广告单元权重较高的 Resource
     */
    @Nullable
    private Resource<?> compareAndGetMostWeight(@NonNull Resource<?> ... resArray) {
        LogUtils.e(TAG, "-->  compareAndGetMostWeight  resArray=" + Arrays.toString(resArray));
        List<Resource<?>> resources = new ArrayList<>();

        for (Resource<?> resource : resArray) { // 过滤掉 null 数据
            if (resource != null) {
                resources.add(resource);
            }
        }

        if (resources.isEmpty()) {
            return null;
        }

        sortResources(resources);

        return resources.get(0); // 按权重从大到小排序
    }

    @Nullable
    public synchronized Resource<?> findMostWeightResource(@NonNull PlaceType placeType, boolean extra, ResourceListener listener) {
        if (!extra && !AdConfig.isPlaceOpen(placeType)) { // 若 admobPos 广告位关闭，则不再展示（额外广告不考虑广告位关系的情况，有可用的缓存广告单元就展示）
            return null;
        }
        List<Resource<?>> compatResources = findCompatResources(placeType, listener); // 返回所有可兼容广告位上的 可展示的 广告集合

        sortResources(compatResources); // 注意：非瀑布流时，不需要对各个广告位中的广告进行排序，优先用集合中的第一个广告，即参数广告位 adPlaceHolder 自己的广告（若存在）

        List<UnitType> uncompatTypes = AdConfig.getUncompatUnitTypes(placeType);

        for (int i = compatResources.size() - 1; i >= 0; i--) {
            Resource<?> resource = compatResources.get(i);
            UnitType type = UnitType.convert(resource.getUnitBean().getType());
            if (uncompatTypes.contains(type)) {
                compatResources.remove(i); // 移除掉与 admobPos 广告位 不兼容的广告
            }
        }

        Resource<?> mostWeightResource = compatResources.isEmpty() ? null : compatResources.get(0);
        LogUtils.e(TAG, "--> findMostWeightResource()  placeType=" + placeType + "  mostWeightResource=" + mostWeightResource);
        return mostWeightResource;
    }

    /**
     * @return 返回所有可兼容广告位上的 可展示的 广告集合。且集合按权重从大到小排序
     */
    @NonNull
    private List<Resource<?>> findCompatResources(@NonNull PlaceType placeType, ResourceListener listener) {
        List<Resource<?>> compatResources = new ArrayList<>();
//        List<PlaceType> compatPlaceTypes = AdConfig.getCompatPlaceTypes(placeType);
//        for (PlaceType compatPlaceType : compatPlaceTypes) {
//            PlaceBean compatPlaceBean = AdConfig.getPlaceBean(compatPlaceType);
//            if (compatPlaceBean == null) { // 兼容广告位可能已关闭，不考虑（包括：当兼容广告位就是参数广告位本身时，也不考虑）
//                continue;
//            }
//            /*
//                1. 先从缓存中取广告位中已缓存的广告单元
//                2. 再从广告位中取权重最高的 banner 广告单元（当前广告位中可以没有banner广告单元，也可以有多个）
//                3. 比较 已缓存广告单元 和 权重最高的 banner 广告单元 二者的权重，取最高者
//             */
//            Resource<?> resPullSuccess = Buffer.get(compatPlaceType, Status.PULL_SUCCESS);
//            Resource<?> mostWeightBannerRes = null;
//            UnitBean mostWeightBannerUnit = AdConfig.getMostWeightUnitBean(compatPlaceType, UnitType.BAN);
//            if (mostWeightBannerUnit != null) {
//                // ban 不放入缓存池，但走回调监听
//                if (UnitType.BAN.provider == Provider.ADMOB) {
//                    mostWeightBannerRes = new Resource<AdView>(compatPlaceBean, mostWeightBannerUnit, listener);
//                } else { // Provider.MAX
//                    mostWeightBannerRes = new MaxResource<MaxAdView>(compatPlaceBean, mostWeightBannerUnit, listener);
//                }
//
//            }
//            Resource<?> compatResource = compareAndGetMostWeight(resPullSuccess, mostWeightBannerRes);
//            if (compatResource != null) {
//                compatResources.add(compatResource); // 在某个兼容广告位中，取权重最高的广告单元所在的 Resource
//            }
//
//            // compatPlaceType 广告位上无缓存广告时，去拉取一个
//            if (resPullSuccess == null) {
//                AdResourceManager.INSTANCE.pullAdInPlace(compatPlaceType);
//            }
//        }
//        LogUtils.e(TAG, "--> findCompatResources()  placeType=" + placeType + "  compatResources=" + compatResources);
        return compatResources;
    }

//    /**
//     * 查找指定广告位上的有效广告（返回的广告可复用其他广告位上的同类型广告，特别地，对 start 位上的 start 类型广告，还可复用其他位上的 int 广告）
//     * 一般在要显示广告时调用该方法查找可显示的广告，若没有则显示失败，此时，应该去加载一个新的广告
//     *
//     * @param placeType 广告位
//     * @return 返回广告位上的有效广告，or 其他广告位上的可共用广告
//     */
//    Resource<?> findResource(@NonNull PlaceType placeType) {
//        LogUtils.e(TAG, "--> findResource()  pos=" + placeType);
//
//        UnitType unitType = AdConfig.getUnitType(placeType);
//        boolean enablePos = AdConfig.isPlaceOpen(placeType);
//
//        if (enablePos && unitType == UnitType.BAN) {
//            return AdResourceManager.INSTANCE.newResourceForBan(placeType);
//        }
//
//        if (!AdConfig.isPlaceOpen(placeType)) { // 若 slot 广告位关闭，则不再去展示广告
//            return null;
//        }
//
//        Resource<?> res = Buffer.get(placeType, Status.PULL_SUCCESS);
//        if (res != null) {
//            LogUtils.e(TAG, "--> findResource() success!!!  placeType=" + placeType + "  resource=" + res);
//            return res;
//        } else {
//            AdResourceManager.INSTANCE.pullAdInPlace(placeType); // slot 广告位上无有效广告时，去加载新的广告
//        }
//
////        if (!AdConfig.isEnablePos(pos)) { // 若 slot 广告位关闭，则不再去共用其他广告位的同类型广告
////            return null;
////        }
//
//        // 执行到这里说明在 slot 上没找到有效广告，此时去其他广告位上找可共用的同类型的有效广告
//        Resource<?> availableResource = findAvailableResource(unitType, placeType);
//
//        if (availableResource != null) {
//            return availableResource;
//        }
//
//        // 当 start 位上的广告是 start 类型且无有效的 start 类型时，可以去其他广告位（如 finish 位）上找 int 类型广告
//        if (unitType == UnitType.START && placeType == PlaceType.START) {
//            return findAvailableResource(UnitType.INT, placeType);
//        }
//
//        return null;
//    }
//
//    /**
//     * 在其他广告位上查找可共用的有效广告
//     * @param dstUnitType 广告类型
//     * @param exclPlaceType 当前广告位
//     * @return 其他广告位上可共用的广告
//     */
//    private Resource<?> findAvailableResource(UnitType dstUnitType, PlaceType exclPlaceType) {
//        Set<Map.Entry<PlaceType, List<Resource<?>>>> entrySet = Buffer.entrySet();
//        for (Map.Entry<PlaceType, List<Resource<?>>> entry : entrySet) {
//            PlaceType placeType = entry.getKey();
//            if (placeType == exclPlaceType) { // 排除当前广告位
//                continue;
//            }
//            List<Resource<?>> resourcesInPlace = entry.getValue();
//            if (resourcesInPlace == null || resourcesInPlace.size() <= 0) {
//                continue;
//            }
//            for (int i = resourcesInPlace.size() - 1; i >= 0; i--) {
//                Resource<?> res = resourcesInPlace.get(i);
//                UnitType unitType = UnitType.convert(res.getUnitBean().getType());
//                Status status = res.getStatus();
//                if (unitType != dstUnitType) {
//                    continue; // 只有同类型的广告才可共用
//                }
//                if (status == Status.PULL_SUCCESS) {
//                    LogUtils.e(TAG, "--> findAvailableResource success!!!  placeType=" + placeType + "  resource=" + res);
//                    LogUtils.d("<" + exclPlaceType.type + ">上可共用<" + res.getPlaceBean().getPlace() + ">上的广告 ## " + res.getUnitBean());
//                    return res;
//                } else { // slot 广告位上没有可显示的广告，加载一个新的
//                    AdResourceManager.INSTANCE.pullAdInPlace(placeType);
//                }
//            }
//        }
//        return null;
//    }
}
