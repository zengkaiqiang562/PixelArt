package com.project_ci01.app.base.advert;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.project_ci01.app.base.bean.event.AdResourceEvent;
import com.project_ci01.app.base.bean.gson.UnitBean;
import com.project_ci01.app.base.event.EventType;
import com.project_ci01.app.base.event.EventBusHelper;
import com.project_ci01.app.base.utils.LogUtils;

public enum AdResourceManager implements ResourceListener {

    INSTANCE;

    private static final String TAG = "AdResourceManager";

//    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final Handler handler;

    private final ResourcePuller resPuller;
    private final ResourceFinder resFinder;

    AdResourceManager() {
        handler = new Handler(Looper.getMainLooper());
        resPuller = new ResourcePuller();
        resFinder = new ResourceFinder();
    }

//    public static void finishAdmob() {
//        ContextService.getService().finishActivity(com.google.android.gms.ads.AdActivity.class);
//    }
//
//    public static void addAdmob(@NonNull Activity activity) {
//        if (activity.getClass() == com.google.android.gms.ads.AdActivity.class) {
//            ContextService.getService().addActivity(activity);
//        }
//    }
//
//    public static void removeAdmob(@NonNull Activity activity) {
//        if (activity.getClass() == com.google.android.gms.ads.AdActivity.class) {
//            ContextService.getService().removeActivity(activity);
//        }
//    }

    // 判断广告位上指定状态的广告缓存是否存在
    public synchronized boolean contains(PlaceType place, Status status) {
        return Buffer.contains(place, status);
    }

    public void pullAds() {
        resPuller.pullAds(this);
    }

    /**
     * @param placeType 默认拉取 placeType 广告位中权重最高的广告单元
     */
    public void pullAdInPlace(@Nullable PlaceType placeType) {
        if (placeType != null) {
            resPuller.pullAdInPlace(placeType, null, this);
        }
    }

    boolean pullAdInPlace(@Nullable PlaceType placeType, @Nullable UnitBean moreWeightUnitBean) {
        if (placeType != null) {
            return resPuller.pullAdInPlace(placeType, moreWeightUnitBean, this);
        }
        return false;
    }

    @Nullable
    public synchronized Resource<?> findMostWeightResource(@NonNull PlaceType placeType) {
        return resFinder.findMostWeightResource(placeType, false, this);
    }

    @Nullable
    public synchronized Resource<?> findMostWeightResource(@NonNull PlaceType placeType, boolean extra) {
        return resFinder.findMostWeightResource(placeType, extra, this);
    }

//    /**
//     * 查找指定广告位上的有效广告（返回的广告可复用其他广告位上的同类型广告，特别地，对 start 位上的 start 类型广告，还可复用其他位上的 int 广告）
//     * 一般在要显示广告时调用该方法查找可显示的广告，若没有则显示失败，此时，应该去加载一个新的广告
//     *
//     * @param placeType 广告位
//     * @return 返回广告位上的有效广告，or 其他广告位上的可共用广告
//     */
//    public synchronized Resource<?> findResource(@NonNull PlaceType placeType) {
//        return resFinder.findResource(placeType);
//    }

//    Resource<?> newResourceForBan(@NonNull PlaceType placeType) {
//        PlaceBean placeBean = AdConfig.getPlaceBean(placeType);
//        if (placeBean == null || placeBean.getUnitBean() == null) {
//            return null;
//        }
//        UnitBean unitBean = placeBean.getUnitBean();
//        if (unitBean == null) {
//            return null;
//        }
//        LogUtils.d("开始拉取<" + placeBean.getPlace() + ">广告 ## " + unitBean);
//        // ban 不放入缓存池，但走回调监听
//        return new Resource<AdView>(placeBean, unitBean, this);
//    }

    @Override
    public void onPrePull(Resource<?> res) {
        // 准备加载前，先移除缓存中之前 加载失败的广告 和 已销毁的广告
        handler.post(() -> {
            PlaceType placeType = PlaceType.convert(res.getPlaceBean().getPlace());
            Buffer.remove(placeType, Status.PULL_FAILED);
            Buffer.remove(placeType, Status.RELEASE);
        });
    }

    @Override
    public void onPullFailed(Resource<?> res, int code, String msg) {
        LogUtils.e(TAG, "--> onPullFailed()  resource=" + res);
        LogUtils.d("拉取<" + res.getPlaceBean().getPlace() + ">广告失败 ## " + res.getUnitBean());
        handler.post(() -> {
            PlaceType placeType = PlaceType.convert(res.getPlaceBean().getPlace());
            boolean result = pullAdInPlace(placeType, res.getUnitBean()); // 拉取失败后，尝试再拉取同一个广告位中的权重更低的广告单元
            if (!result) {
                notifyAdUnPull(res);
            }
            checkReady();
        });
    }

    @Override
    public void onPullSuccess(Resource<?> res) {
        LogUtils.e(TAG, "--> onPullSuccess()  resource=" + res);
        LogUtils.d("拉取<" + res.getPlaceBean().getPlace() + ">广告成功 ## " + res.getUnitBean());
        handler.post(() -> {
            checkReady();
            notifyAdPulled(res);
        });
    }

    @Override
    public void onShow(Resource<?> res) {
        // 一个广告位上的广告被展示后，再为该广告位加载一个新的广告进行缓存（按需求：一个广告位上只有一个广告），以便下次展示
        LogUtils.d("展示<" + res.getPlaceBean().getPlace() + ">广告成功 ## " + res.getUnitBean());
        handler.post(() -> {
            PlaceType placeType = PlaceType.convert(res.getPlaceBean().getPlace());
//            loadBill(slot); // 显示成功再另外缓存个新的
            pullAds(); // 2022.10.21 fix bug #25581 ：在广告展示完之后，需要检查所有的广告位
            notifyAdShow(res);
        });
    }

    @Override
    public void onDismiss(Resource<?> res) {
        // 广告显示完，从缓存中移除
        handler.post(() -> {
            PlaceType placeType = PlaceType.convert(res.getPlaceBean().getPlace());
            Buffer.remove(placeType, Status.DISMISS);
            notifyAdDismiss(res);
        });
    }

    @Override
    public void onUnshow(Resource<?> res, int code, String msg) {
        // 广告显示失败，从缓存中移除
        LogUtils.d("展示<" + res.getPlaceBean().getPlace() + ">广告失败 ## " + res.getUnitBean());
        handler.post(() -> {
            PlaceType placeType = PlaceType.convert(res.getPlaceBean().getPlace());
            Buffer.remove(placeType, Status.UNSHOW);
            pullAdInPlace(placeType); // 显示失败再另外缓存个新的
            notifyAdUnshow(res, code, msg);
        });
    }

    @Override
    public void onExpired(Resource<?> res) {
        // 广告超时，从缓存中移除
        LogUtils.d("<" + res.getPlaceBean().getPlace() + ">广告有效期超过 1 小时 ## " + res.getUnitBean());
        handler.post(() -> {
            PlaceType placeType = PlaceType.convert(res.getPlaceBean().getPlace());
            Buffer.remove(placeType, Status.EXPIRED);
            pullAdInPlace(placeType); // 缓存超时再另外缓存个新的
        });
    }

    @Override
    public void onClick(Resource<?> res) {
        LogUtils.d("<" + res.getPlaceBean().getPlace() + ">广告被点击 ## " + res.getUnitBean());
    }

    @Override
    public void onEarnedReward(Resource<?> res) {
        LogUtils.e(TAG, "--> onEarnedReward()  resource=" + res);
        LogUtils.d("从<" + res.getPlaceBean().getPlace() + ">广告中获取到了奖励 ## " + res.getUnitBean());
        handler.post(() -> {
            notifyAdReward(res);
        });
    }

    /*---------------------------------------------------------------------------------*/

    /**
     * 是否可以加速启动
     */
    public boolean isReady() {
        boolean startEnable = AdConfig.isPlaceOpen(PlaceType.START);
        boolean connEnable = AdConfig.isPlaceOpen(PlaceType.Connect);
        boolean infoEnable = AdConfig.isPlaceOpen(PlaceType.INFO);

        Resource<?> startSource = Buffer.get(PlaceType.START, Status.PULL_SUCCESS);
        Resource<?> connectSource = Buffer.get(PlaceType.Connect, Status.PULL_SUCCESS);
        Resource<?> infoSource = Buffer.get(PlaceType.INFO, Status.PULL_SUCCESS);
        boolean isStartLoad = !startEnable || startSource != null;
        boolean isConnLoad = !connEnable || connectSource != null;
        boolean isInfoLoad = !infoEnable || infoSource != null;

        return isStartLoad && isConnLoad && isInfoLoad;
    }

    private void checkReady() {
        boolean ready = isReady();

        LogUtils.e(TAG, "--> checkReady()  ready=" + ready);
        if (ready) {
            EventBusHelper.post(new AdResourceEvent(null, EventType.TYPE_AD_READY), false);
        }
    }

    private void notifyAdUnPull(Resource<?> res) {
        EventBusHelper.post(new AdResourceEvent(res, EventType.TYPE_AD_UNPULL), false);
    }

    private void notifyAdPulled(Resource<?> res) {
        EventBusHelper.post(new AdResourceEvent(res, EventType.TYPE_AD_PULLED), false);
    }

    private void notifyAdShow(Resource<?> res) {
        EventBusHelper.post(new AdResourceEvent(res, EventType.TYPE_AD_SHOW), false);
    }

    private void notifyAdDismiss(Resource<?> res) {
        EventBusHelper.post(new AdResourceEvent(res, EventType.TYPE_AD_DISMISS), false);
    }

    private void notifyAdUnshow(Resource<?> res, int code, String msg) {
        EventBusHelper.post(new AdResourceEvent(res, EventType.TYPE_AD_UNSHOW), false);
    }

    private void notifyAdReward(Resource<?> res) {
        EventBusHelper.post(new AdResourceEvent(res, EventType.TYPE_AD_REWARD), false);
    }

//    /**
//     * nav 广告当天访问（点击）计数
//     */
//    public static void calcNavClickTimes() {
//        // 1. 获取当天时间
//        // 2. 去 sp 中找是否保存有当天的记录
//        // 2.1 有则 +1 ，无则以 yyyyMMdd#count 从 1 开始保存
//        String curDate = TimeUtils.date2String(Calendar.getInstance().getTime(), "yyyyMMdd"); // 当天的日期
//        String formatCount = SPUtils.getInstance().getString(IProfile.SP_NAV_CLICK_TIMES, "");
//
//        Slog.e(TAG, "--> calcNavClickTimes() curDate=" + curDate + "  SP formatCount=" + formatCount);
//
//        String saveDate = null;
//        int saveCount = -1;
//        if (!TextUtils.isEmpty(formatCount)) {
//            String[] split = formatCount.split("#");
//            if (split.length == 2 && !TextUtils.isEmpty(split[0]) && !TextUtils.isEmpty(split[1])) {
//                saveDate = split[0]; // 保存时的日期
//                saveCount = Integer.parseInt(split[1]);
//            }
//        }
//
//        if (curDate.equals(saveDate) && saveCount != -1) { // 当天有访问（点击）过，累加
//            int curCount = saveCount + 1;
//            formatCount = curDate + "#" + curCount;
//        } else {
//            formatCount = curDate + "#" + 1; // 没有保存过，或保存的日期不是当天，则当天从 1 开始计
//        }
//
//        Slog.e(TAG, "--> calcNavClickTimes() save formatCount=" + formatCount);
//        SPUtils.getInstance().put(IProfile.SP_NAV_CLICK_TIMES, formatCount);
//    }

//    /**
//     * @return 是否因达到当日最大点击数而限制 nav 广告的加载（默认 false 未限制，即：可加载）
//     */
//    public static boolean isArrivedNavMaxClickTimes() {
//
//        // 当日内，原生广告的访问（点击）次数上限（默认无上限）
//        int maxNavClick = BillProfile.getNavMaxClickTimes();
//
//        // 从 SP 中获取当天 nav 广告的访问（点击）次数，默认 0
//        int accessCount = 0;
//        String curDate = TimeUtils.date2String(Calendar.getInstance().getTime(), "yyyyMMdd"); // 当天的日期
//        String formatCount = SPUtils.getInstance().getString(IProfile.SP_NAV_CLICK_TIMES, "");
//        Slog.e(TAG, "--> isArrivedNavMaxClickTimes() curDate=" + curDate + "  SP formatCount=" + formatCount);
//
//        if (TextUtils.isEmpty(formatCount)) {
//            return false;
//        }
//
//        String[] split = formatCount.split("#");
//        if (split.length == 2 && !TextUtils.isEmpty(split[0]) && !TextUtils.isEmpty(split[1])) {
//            String saveDate = split[0]; // 保存时的日期
//            if (curDate.equals(saveDate)) {
//                accessCount = Integer.parseInt(split[1]); // 当天有点击过，则返回
//            }
//        }
//
//        return accessCount >= maxNavClick;
//    }
}
