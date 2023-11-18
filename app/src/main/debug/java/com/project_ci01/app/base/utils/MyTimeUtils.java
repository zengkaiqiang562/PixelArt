package com.project_ci01.app.base.utils;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class MyTimeUtils {

    public static String millis2StringGMT(long ts, String pattern) {
        SimpleDateFormat dateFormat = TimeUtils.getSafeDateFormat(pattern);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return TimeUtils.millis2String(ts, dateFormat);
    }

    public static String millis2StringGMT(long ts, String pattern, Locale locale) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return TimeUtils.millis2String(ts, dateFormat);
    }

    public static String millis2String(long ts, String pattern, Locale locale) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
        return TimeUtils.millis2String(ts, dateFormat);
    }

    public static Calendar getCalendarGMT() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    }

    public static long time2Millis(int hour, int minute) {
        return ConvertUtils.timeSpan2Millis(hour, TimeConstants.HOUR) + ConvertUtils.timeSpan2Millis(minute, TimeConstants.MIN);
    }

    public static long time2Millis(int hour, int minute, int sec) {
        return ConvertUtils.timeSpan2Millis(hour, TimeConstants.HOUR) + ConvertUtils.timeSpan2Millis(minute, TimeConstants.MIN)
                + ConvertUtils.timeSpan2Millis(sec, TimeConstants.SEC);
    }

    /**
     *
     * @return 返回 len=2 的 int[] 数组，
     * int[0] 表示小时，取值 [0, 23]
     * int[1] 表示分钟，取值 [0, 59]
     */
    public static int[] millis2HourMin(long millis) {
        int[] result = new int[2];
        int[] unitLen = {86400000, 3600000, 60000}; // 天，小时，分钟
        for (int i = 0; i < unitLen.length; i++) {
            if (millis >= unitLen[i]) {
                long mode = millis / unitLen[i];
                if (i != 0) { // 去除天
                    result[i-1] = (int) mode;
                }
                millis -= mode * unitLen[i];
            }
        }
        return result;
    }

    /**
     *
     * @return 返回 len=3 的 int[] 数组，
     * int[0] 表示小时，取值 [0, 23]
     * int[1] 表示分钟，取值 [0, 59]
     * int[2] 表示秒，取值 [0, 59]
     */
    public static int[] millis2HourMinSec(long millis) {
        int[] result = new int[3];
        int[] unitLen = {86400000, 3600000, 60000, 1000}; // 天，小时，分钟，秒
        for (int i = 0; i < unitLen.length; i++) {
            if (millis >= unitLen[i]) {
                long mode = millis / unitLen[i];
                if (i != 0) { // 去除天
                    result[i-1] = (int) mode;
                }
                millis -= mode * unitLen[i];
            }
        }
        return result;
    }

    public static long getStartOfMonth(int year, int month) { // month = 1-12
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        int minDayOfMonth = calendar.getActualMinimum(Calendar.DATE);
        calendar.set(Calendar.DATE, minDayOfMonth);
        return getStartOfDay(calendar);
    }

    public static long getEndOfMonth(int year, int month) { // month = 1-12
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        int maxDayOfMonth = calendar.getActualMaximum(Calendar.DATE);
        calendar.set(Calendar.DATE, maxDayOfMonth);
        return getEndOfDay(calendar);
    }

    /**
     * 获取某天的起始时间，即 0点0分0秒
     */
    public static long getStartOfDay(Calendar day) {
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);
        return day.getTimeInMillis();
    }

    public static long getStartOfDay(int year, int month, int day) { // month = 1-12
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取某天的结束时间，即 23点59分59秒
     */
    public static long getEndOfDay(Calendar day) {
        day.set(Calendar.HOUR_OF_DAY, 23);
        day.set(Calendar.MINUTE, 59);
        day.set(Calendar.SECOND, 59);
        day.set(Calendar.MILLISECOND, 0);
        return day.getTimeInMillis();
    }

    public static long getEndOfDay(int year, int month, int day) { // month = 1-12
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getStartOfToday() {
        Calendar today = Calendar.getInstance();
        return getStartOfDay(today);
    }

    public static long getEndOfToday() {
        Calendar today = Calendar.getInstance();
        return getEndOfDay(today);
    }

    /**
     * @return 判断 参数时间 time 是否在昨天之内
     */
    public static boolean isInYesterday(long time) {
        Calendar yesterdayCalendar = Calendar.getInstance();
        yesterdayCalendar.add(Calendar.DATE, -1);
        return time >= getStartOfDay(yesterdayCalendar) && time <= getEndOfDay(yesterdayCalendar);
    }

    /**
     * @return 判断 参数时间 time 是否在今天之内
     */
    public static boolean isInToday(long time) {
        long startOfToday = getStartOfToday();
        long endOfToday = getEndOfToday();
        return time >= startOfToday && time <= endOfToday;
    }

    /**
     * 是否是当月
     */
    public static boolean isCurrentMonth(long time) {
        Calendar nowCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.MONTH) == nowCalendar.get(Calendar.MONTH);
    }

    /**
     * @return 判断 参数时间 time 是否在 1 小时内
     */
    public static boolean isInOneHour(long time) {
        return System.currentTimeMillis() - time < ConvertUtils.timeSpan2Millis(1, TimeConstants.HOUR);
    }

    public static String fitDate(int year, int month, String pattern) { // month = 1-12
        return fitDate(year, month, pattern, Locale.getDefault());
    }

    public static String fitDate(int year, int month, String pattern, Locale locale) { // month = 1-12
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1 );
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
        return TimeUtils.millis2String(calendar.getTimeInMillis(), dateFormat);
    }
}
