package com.project_ci01.app.base.advert;

import androidx.annotation.Nullable;

import com.project_ci01.app.base.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class Buffer {

    private static final String TAG = "AdBuffer";

    private static final Map<PlaceType, List<Resource<?>>> bufferMap = new ConcurrentHashMap<>();

    static synchronized void put(PlaceType place, Resource<?> res) {
        if (place == null) {
            return;
        }
        List<Resource<?>> resources = bufferMap.get(place);
        if (resources == null) {
            resources = new ArrayList<>();
            bufferMap.put(place, resources);
        }
        resources.add(res);
    }

    // 获取广告位上指定状态的广告缓存
    static synchronized Resource<?> get(PlaceType place, Status status) {
        if (place == null) {
            return null;
        }
        List<Resource<?>> resources = bufferMap.get(place);
        if (resources == null || resources.size() <= 0) {
            return null;
        }
        for (int i = resources.size() - 1; i >= 0; i--) {
            Resource<?> res = resources.get(i);
            if (res.getStatus() == status) { //
                LogUtils.e(TAG, "--> get()  place=" + place + "  status=" + status);
                return res;
            }
        }

        return null;
    }

    // 移除广告位上指定状态的广告缓存
    static synchronized void remove(@Nullable PlaceType place, Status status) {
        if (place == null) {
            return;
        }
        List<Resource<?>> resources = bufferMap.get(place);
        if (resources == null || resources.size() <= 0) {
            return;
        }

        for (int i = resources.size() - 1; i >= 0; i--) {
            Resource<?> res = resources.get(i);
            if (res.getStatus() == status) {
                res.destroy();
                resources.remove(res);
            }
        }
    }

    // 判断广告位上指定状态的广告缓存是否存在
    static synchronized boolean contains(PlaceType place, Status status) {
        if (place == null) {
            return false;
        }
        List<Resource<?>> resources = bufferMap.get(place);
        if (resources == null || resources.size() <= 0) {
            return false;
        }
        for (int i = resources.size() - 1; i >= 0; i--) {
            Resource<?> res = resources.get(i);
            if (res.getStatus() == status) {
                LogUtils.e(TAG, "--> contains()  place=" + place + "  status=" + status);
                return true;
            }
        }

        return false;
    }

//    // 获取广告位上缓存的广告类型
//    static synchronized AdCategory getAdCategory(AdPlaceHolder adPlaceHolder) {
//        List<AdPacket<?>> adPackets = map.get(adPlaceHolder);
//        if (adPackets == null || adPackets.size() <= 0) {
//            return null;
//        }
//        AdCategory adCategory = AdCategory.convert(adPackets.get(0).getTypeEntity().getCategory());
//        Logger.e(TAG, "--> getAdCategory()  adPlaceHolder=" + adPlaceHolder + "  adCategory=" + adCategory);
//        return type;
//    }

    static synchronized Set<Map.Entry<PlaceType, List<Resource<?>>>> entrySet() {
        return bufferMap.entrySet();
    }
}
