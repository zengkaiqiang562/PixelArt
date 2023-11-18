package com.project_ci01.app.base.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Pattern;

public class NumUtils {
    private static final Random RANDOM = new Random();

    /**
     * float 保留两位小数方法1
     *
     * @param value
     * @return
     */
    public static String formatFloatValue(double value) {
        DecimalFormat df = new DecimalFormat("###0.00");
        return df.format(value);
    }

    /**
     * float 保留两位小数方法1
     *
     * @param value
     * @param pattern ###0.00
     * @return
     */
    public static String formatFloatValue(double value, String pattern) {
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(value);
    }

    /**
     * float 保留两位小数方法1
     *
     * @param value
     * @return
     */
    public static float formatFloatValue(float value) {

        BigDecimal b = new BigDecimal(String.valueOf(value));
        float result = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        //   b.setScale(2,  BigDecimal.ROUND_HALF_UP) 表明四舍五入，保留两位小数
        return result;
    }

    /**
     * 步数换算w
     *
     * @return 0：数值 1：单位
     * create at 2020-04-27 19:40
     * @author wy
     */
    public static String stepNumberToW(int monthNumber) {
        String number = "";
        if (monthNumber < 9999) {
            number = monthNumber + "";
        } else if (monthNumber < 99999) {
            number = NumUtils.formatFloatValueToStr(monthNumber / 10000.0f, 2) + "w";
        } else if (monthNumber < 9999999) {
            number = NumUtils.formatFloatValueToStr(monthNumber / 10000.0f, 1) + "w";
        } else {
            number = NumUtils.formatFloatValueToStr(monthNumber / 10000.0f, 0) + "w";
        }
        return number;
    }

    /**
     * float 保留两位小数方法1
     *
     * @param value
     * @return
     */
    public static String formatFloatValueToStr(float value, int newScale) {

        BigDecimal b = new BigDecimal(String.valueOf(value));
        String temp = b.setScale(newScale, BigDecimal.ROUND_HALF_UP).toString();
        return subZeroAndDot(temp);

    }

    public static String formatDoubleValueToStr(double value, int newScale) {

        BigDecimal b = new BigDecimal(String.valueOf(value));
        String temp = b.setScale(newScale, BigDecimal.ROUND_HALF_UP).toString();
        return subZeroAndDot(temp);

    }

    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0 行结尾出现0~1次0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }


    /**
     * float 保留两位小数方法1
     *
     * @param value
     * @return
     */
    public static String formatFloatValueToStr(float value) {

        BigDecimal b = new BigDecimal(String.valueOf(value));
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * float 保留小数
     *
     * @param value
     * @return
     */
    public static float formatFloatValue(float value, int newScale) {

        BigDecimal b = new BigDecimal(String.valueOf(value));
        float result = b.setScale(newScale, BigDecimal.ROUND_HALF_UP).floatValue();
        //   b.setScale(2,  BigDecimal.ROUND_HALF_UP) 表明四舍五入，保留两位小数
        return result;
    }

    /**
     * 获取随机数
     *
     * @author 肖亮
     * create at 2020-03-10 17:56
     */
    public static float getRandomFloat(int max, int min) {
        float floatBounded = min + ((RANDOM.nextFloat() * (max - min)));
        return formatFloatValue(floatBounded, 1);
    }

    /**
     * 获取随机数
     *
     * @author 肖亮
     * create at 2020-03-10 17:56
     */
    public static int getRandomInt(int max, int min) {
        int intBounded = min + ((int) (RANDOM.nextFloat() * (max - min)));
        return intBounded;
    }

    /**
     * 分转元
     *
     * @param value
     * @return
     */
    public static float amountFloatValue(float value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        BigDecimal tempBd = new BigDecimal(100);
        BigDecimal temp3 = bigDecimal.divide(tempBd);
        return temp3.setScale(2, BigDecimal.ROUND_UNNECESSARY).floatValue();
        //   b.setScale(2,  BigDecimal.ROUND_HALF_UP) 表明四舍五入，保留两位小数
//        return result;
    }

    /**
     * 分转元，整元 不带小数点
     * @author zengkaiqiang
     * create at 2020/8/7 9:33
     */
    public static String convertYuan(int fen) {
        float floatYuan = amountFloatValue(fen);
        return subZeroAndDot(String.valueOf(floatYuan));
//        int intYuan = (int) floatYuan;
//        float remainder = floatYuan % intYuan;
//        //当小数位是0时，转int
//        String dstYuan = (remainder < 10e-6) ? String.valueOf(intYuan) : String.valueOf(floatYuan);
//        return dstYuan;
    }

    public static String convertYuan(float fen) {
        float floatYuan = amountFloatValue(fen);
        return subZeroAndDot(String.valueOf(floatYuan));

    }

    public static String amoutToStringValue(long value) {

        BigDecimal bigDecimal = new BigDecimal(value);
        BigDecimal tempBd = new BigDecimal(100);
        BigDecimal temp3 = bigDecimal.divide(tempBd);

        //如果是整数，则不保留小数

        String str = temp3.setScale(2, BigDecimal.ROUND_UNNECESSARY).toString();
        String[] strs = str.split("\\.");
        if(strs[1].equals("00")){
            return strs[0];
        }else{
            return str;
        }
//        BigDecimal b = new BigDecimal(String.valueOf(value / 100f));
//        return b.setScale(2, BigDecimal.ROUND_UNNECESSARY).toString();
    }

    /**
     * 获取积分
     *
     * @author 肖亮
     * create at 2020-03-27 19:03
     */
    public static String integralToStringValue(long value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        BigDecimal tempBd = new BigDecimal(100);
        BigDecimal temp3 = bigDecimal.divide(tempBd);

        String str = temp3.setScale(2, BigDecimal.ROUND_UNNECESSARY).toString();
        String[] strs = str.split("\\.");
        if(strs[1].equals("00")){
            return strs[0];
        }else{
            return str;
        }
//        LogUtil.e(str);
//        float temp = value / 100;
//        BigDecimal b = new BigDecimal(temp);
//        return b.setScale(2, BigDecimal.ROUND_UNNECESSARY).toString();
    }

    /**
     * 积分 当天新增
     *
     * @author 肖亮
     * create at 2020-03-27 19:36
     */
    public static String integralIncomeNewlyAdded(long value) {
        if (value <= 0) {
            return "";
        }
        String temp = integralToStringValue(value);
        return "+" + temp;
    }

    /**
     * 钱 当天新增
     *
     * @author 肖亮
     * create at 2020-03-27 19:36
     */
    public static String amoutIncomeNewlyAdded(long value) {
        if (value <= 0) {
            return "";
        }
        String temp = amoutToStringValue(value);
        return "+" + temp;
    }


    /**
     * float 保留两位小数方法2
     *
     * @param value
     * @return
     */
    public static String formatFloatValue2(float value) {
        DecimalFormat fnum = new DecimalFormat("##0.00");
        String result = fnum.format(value);

        return result;
    }

    /**
     * float 保留两位小数方法3
     *
     * @param value
     * @return
     */
    public static float formatFloatValue3(float value) {

        float result = (float) (Math.round(value * 100)) / 100;
        return result;
    }

    /**
     * @Description: 格式化数字显示
     * @Author: chenshouyin@mxtio.com
     * @CreateDate: 2020/7/22
     * @UpdateUser:
     * @UpdateRemark:
     */
    public static String formatNum2W(int num) {
        String result =""+ num;
        if(num>9999){
            result = num/10000+"."+((num%10000)/1000)+"w";
        }else if(num>999){
            result = num/1000+"."+((num%1000)/100)+"k";
        }
        return result;
    }

    /*
     * 是否为浮点数？double或float类型。
     * @param str 传入的字符串。
     * @return 是浮点数返回true,否则返回false。
     */
    public static boolean isDoubleOrFloat(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

}
