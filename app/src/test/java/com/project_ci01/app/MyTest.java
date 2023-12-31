package com.project_ci01.app;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.project_ci01.app.base.utils.MyTimeUtils;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class MyTest {
    @Test
    public void test1() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy,h:mm a", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.add(Calendar.HOUR_OF_DAY, 4);
//        calendar.add(Calendar.DATE, -1);
        calendar.add(Calendar.MINUTE, -80);
        String fitTime = TimeUtils.millis2String(calendar.getTimeInMillis(), dateFormat);
        System.out.println("fitTime="+fitTime);

        Calendar yesterdayCalendar = Calendar.getInstance();
        yesterdayCalendar.add(Calendar.DATE, -1);
        boolean inToday = MyTimeUtils.isInToday(calendar.getTimeInMillis());
        boolean inYesterday = calendar.getTimeInMillis() >= MyTimeUtils.getStartOfDay(yesterdayCalendar) && calendar.getTimeInMillis() <= MyTimeUtils.getEndOfDay(yesterdayCalendar);
        boolean inOneHour = System.currentTimeMillis() - calendar.getTimeInMillis() <= ConvertUtils.timeSpan2Millis(1, TimeConstants.HOUR);
        System.out.println("inToday="+inToday);
        System.out.println("inYesterday="+inYesterday);
        System.out.println("inOneHour="+inOneHour);
    }

    @Test
    public void test2() {// 去掉头尾的 "
        String str = "ASUS_public_5G";
        System.out.println("str="+str);
        String result = Pattern.compile("^\"|\"$").matcher(str).replaceAll("");
        System.out.println("result="+result);
    }

    @Test
    public void test3() { // 颜色值转16进制
        int color = -515;
        byte alpha = (byte) (color >>> 24);
        byte red = (byte) ((color >> 16) & 0xFF);
        byte green = (byte) ((color >> 8) & 0xFF);
        byte blue = (byte) (color & 0xFF);
        String hexColor = "#" + Hex.encodeHexString(new byte[]{alpha, red, green, blue}, false);
        System.out.println("hexColor="+hexColor);
    }

    @Test
    public void test4() {// 去掉文件后缀
        String str = "image1.xxx.png";
        System.out.println("str="+str);
        String result = Pattern.compile("\\.[a-zA-Z0-9_]+$").matcher(str).replaceAll("");
        System.out.println("result="+result);
    }

    @Test
    public void test5() {
        String str = "images/daily/202311/01.png";
        String result = str.substring(str.lastIndexOf("/") + 1);
//        String result = Pattern.compile("\\.[a-zA-Z0-9_]+$").matcher(str).replaceAll("");
        System.out.println("result="+result);
    }

    @Test
    public void test6() {
        long tsDate = TimeUtils.string2Millis("20231101", "yyyyMMdd");
        String fitDate = TimeUtils.millis2String(tsDate, "yyyyMMdd HH:mm:ss");
        System.out.println("fitDate="+fitDate);
    }

    @Test
    public void test7() {
        for (int i=1211; i < 1211+79; i++ ) {
            System.out.println(i);
        }
    }

    @Test
    public void test8() {
        String[] arr = new String[]{
                "01",
                "02",
                "03",
                "04",
                "05",
                "06",
                "07",
                "08",
                "09",
                "10",
                "11",
                "12",
                "13",
                "14",
                "15",
                "16",
                "17",
                "18",
                "19",
                "20",
                "21",
                "22",
                "23",
                "24",
                "25",
                "26",
                "27",
                "28",
                "29",
        };

        int count = 0;
//        for (int i = arr.length - 1; i>=0; i--) {
        for (int i = 0; i < arr.length; i++) {
            if (count > 53) {
                return;
            }
            System.out.println(arr[i]);
            System.out.println(arr[i]);
            count +=2;
        }
    }
}
