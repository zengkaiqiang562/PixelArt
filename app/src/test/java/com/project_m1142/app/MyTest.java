package com.project_m1142.app;

import android.graphics.Color;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.google.android.gms.common.util.Hex;
import com.project_m1142.app.base.utils.MyTimeUtils;

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
        String hexColor = "#" + Hex.bytesToStringUppercase(new byte[]{alpha, red, green, blue});
        System.out.println("hexColor="+hexColor);
    }

    @Test
    public void test4() {// 去掉文件后缀
        String str = "image1.xxx.png";
        System.out.println("str="+str);
        String result = Pattern.compile("\\.[a-zA-Z0-9_]+$").matcher(str).replaceAll("");
        System.out.println("result="+result);
    }
}
