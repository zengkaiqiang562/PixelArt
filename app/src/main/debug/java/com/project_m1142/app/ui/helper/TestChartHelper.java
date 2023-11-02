package com.project_m1142.app.ui.helper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.ConvertUtils;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.project_m1142.app.R;
import com.project_m1142.app.network.TrafficChartManager;
import com.project_m1142.app.network.TrafficRateBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TestChartHelper {

    private static final String TAG = "TestChartHelper";
    private static final String LABEL_RX = "RX";
    private static final String LABEL_TX = "TX";

    public static void updateChartView(Context context, LineChart chart, View emptyView) {

        List<TrafficRateBean> rateBeans = TrafficChartManager.getInstance().getTrafficRateBeans();

        if (rateBeans.isEmpty()) {
            emptyView.setVisibility(VISIBLE);
            chart.setVisibility(GONE);
            return;
        }

        emptyView.setVisibility(GONE);
        chart.setVisibility(VISIBLE);

        initChart(context, chart, rateBeans);


        LineData lineData = chart.getLineData();

        LineDataSet rxDataSet = initRxLineData(context, rateBeans);
        LineDataSet txDataSet = initTxLineData(context, rateBeans);

        if (lineData != null && lineData.getDataSetCount() > 0/*lineData.getDataSetByLabel(LABEL_RX, false) != null && lineData.getDataSetByLabel(LABEL_TX, false) != null*/) {
            LineDataSet rxLineDataSet = (LineDataSet) lineData.getDataSetByLabel(LABEL_RX, false);
            LineDataSet txLineDataSet = (LineDataSet) lineData.getDataSetByLabel(LABEL_TX, false);
            rxLineDataSet.setEntries(rxDataSet.getEntries());
            txLineDataSet.setEntries(txDataSet.getEntries());
            lineData.notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            ArrayList<ILineDataSet> dataSets = new ArrayList<>(2); // 一条收缩压数据链 + 一条舒张压数据链
            dataSets.add(rxDataSet);
            dataSets.add(txDataSet);
            lineData = new LineData(dataSets);
            chart.setData(lineData);
//        chart.invalidate();
        }


    }

    private static LineDataSet initRxLineData(Context context, List<TrafficRateBean> rxRateBeans) {

//        //无数据时，显示带坐标轴，无数据线的图表
//        if (rxRateBeans.isEmpty()) {
//            ArrayList<Entry> emptyEntries = new ArrayList<>();
//            emptyEntries.add(new Entry(0, 0));
//
//            LineDataSet emptyDataSet = new LineDataSet(emptyEntries, null);
//            emptyDataSet.setColor(Color.TRANSPARENT);//不显示数据线
//            emptyDataSet.setDrawCircles(false);
//            emptyDataSet.setDrawCircleHole(false);
//            emptyDataSet.setDrawValues(false); //不绘制数据链上的数值
//
////            ArrayList<ILineDataSet> emptyDataSets = new ArrayList<>();
////            emptyDataSets.add(emptyDataSet);
////            LineData emptyLineData = new LineData(emptyDataSets);
//            return emptyDataSet;
//        }

        ArrayList<Entry> rxEntries = new ArrayList<>(rxRateBeans.size());//主数据链
        int[] rxPointColors = new int[rxRateBeans.size()]; // 指定每个数据点的颜色

        for (int index = 0; index < rxRateBeans.size(); index++) {
            TrafficRateBean rateBean = rxRateBeans.get(index);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(rateBean.time);
            int ms = calendar.get(Calendar.MILLISECOND);
            int sec = calendar.get(Calendar.SECOND);
            int minute = calendar.get(Calendar.MINUTE);
            int msTime = ms + sec * 1000 + minute * 60 * 1000;
            rxEntries.add(new Entry(msTime, rateBean.rxRate));

            rxPointColors[index] = ContextCompat.getColor(context, R.color.FFFFBB3E); // 一条数据链上的所有数据点颜色相同
        }

        LineDataSet rxDataSet = new LineDataSet(rxEntries, LABEL_RX);
        rxDataSet.setColor(ContextCompat.getColor(context, R.color.FFFFBB3E)); // 设置数据链的颜色，数据链和数据点颜色也相同
//                rxDataSet.setColor(Color.TRANSPARENT);//不显示数据线
        rxDataSet.setLineWidth(2f);//设置线宽 2dp
        rxDataSet.setDrawCircles(false); // 绘制数据点所在的圆
        rxDataSet.setCircleColors(rxPointColors);
        rxDataSet.setCircleRadius(5f); // 表示数据点的圆的半径 = 5.5 dp
        rxDataSet.setDrawCircleHole(false); // true 表示数据点为空心圆环，false 为实心圆
        rxDataSet.setCircleHoleRadius(3f); // 设置空心圆环的内半径为 3dp
        rxDataSet.setDrawValues(false); //绘制数据链上的数值
        rxDataSet.setValueTextSize(12f);//设置显示值的文字大小
        rxDataSet.setValueTextColor(ContextCompat.getColor(context, R.color.FFFFBB3E)); // 显示值的文字的颜色（所有点统一颜色）
//                rxDataSet.setValueTextColors();  // 显示值的文字的颜色（为每个点设置不同的颜色）
        rxDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // 设置数据链展示为圆滑曲线（LineDataSet.Mode.LINEAR 则为折线）
        rxDataSet.setHighlightEnabled(false); // 禁用点击高亮线
//                rxDataSet.setHighlightLineWidth(2f); // 设置点击交点后显示高亮线宽
//                rxDataSet.setHighLightColor(colorInt);//设置点击交点后显示交高亮线的颜色
        rxDataSet.setDrawFilled(true);// 禁用范围背景填充
        Drawable drawable = new ColorDrawable(ContextCompat.getColor(context, R.color._26FFBB3E));
        rxDataSet.setFillDrawable(drawable);
        rxDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.valueOf((int)value);
            }
        });

        return rxDataSet;
    }

    private static LineDataSet initTxLineData(Context context, List<TrafficRateBean> rateBeans) {

//        //无数据时，显示带坐标轴，无数据线的图表
//        if (rateBeans.isEmpty()) {
//            ArrayList<Entry> emptyEntries = new ArrayList<>();
//            emptyEntries.add(new Entry(0, 0));
//
//            LineDataSet emptyDataSet = new LineDataSet(emptyEntries, null);
//            emptyDataSet.setColor(Color.TRANSPARENT);//不显示数据线
//            emptyDataSet.setDrawCircles(false);
//            emptyDataSet.setDrawCircleHole(false);
//            emptyDataSet.setDrawValues(false); //不绘制数据链上的数值
//
////            ArrayList<ILineDataSet> emptyDataSets = new ArrayList<>();
////            emptyDataSets.add(emptyDataSet);
////            LineData emptyLineData = new LineData(emptyDataSets);
//            return emptyDataSet;
//        }

        ArrayList<Entry> txEntries = new ArrayList<>(rateBeans.size());//主数据链
        int[] txPointColors = new int[rateBeans.size()]; // 指定每个数据点的颜色

        for (int index = 0; index < rateBeans.size(); index++) {
            TrafficRateBean rateBean = rateBeans.get(index);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(rateBean.time);
            int ms = calendar.get(Calendar.MILLISECOND);
            int sec = calendar.get(Calendar.SECOND);
            int minute = calendar.get(Calendar.MINUTE);
            int msTime = ms + sec * 1000 + minute * 60 * 1000;
            txEntries.add(new Entry(msTime, rateBean.txRate));

            txPointColors[index] = ContextCompat.getColor(context, R.color.FF8A49FF); // 一条数据链上的所有数据点颜色相同
        }

        LineDataSet txDataSet = new LineDataSet(txEntries, LABEL_TX);
        txDataSet.setColor(ContextCompat.getColor(context, R.color.FF8A49FF)); // 设置数据链的颜色，数据链和数据点颜色也相同
//                rxDataSet.setColor(Color.TRANSPARENT);//不显示数据线
        txDataSet.setLineWidth(2f);//设置线宽 2dp
        txDataSet.setDrawCircles(false); // 绘制数据点所在的圆
        txDataSet.setCircleColors(txPointColors);
        txDataSet.setCircleRadius(5f); // 表示数据点的圆的半径 = 5.5 dp
        txDataSet.setDrawCircleHole(false); // true 表示数据点为空心圆环，false 为实心圆
        txDataSet.setCircleHoleRadius(3f); // 设置空心圆环的内半径为 3dp
        txDataSet.setDrawValues(false); //绘制数据链上的数值
        txDataSet.setValueTextSize(12f);//设置显示值的文字大小
        txDataSet.setValueTextColor(ContextCompat.getColor(context, R.color.FF8A49FF)); // 显示值的文字的颜色（所有点统一颜色）
//                rxDataSet.setValueTextColors();  // 显示值的文字的颜色（为每个点设置不同的颜色）
        txDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // 设置数据链展示为圆滑曲线（LineDataSet.Mode.LINEAR 则为折线）
        txDataSet.setHighlightEnabled(false); // 禁用点击高亮线
//                rxDataSet.setHighlightLineWidth(2f); // 设置点击交点后显示高亮线宽
//                rxDataSet.setHighLightColor(colorInt);//设置点击交点后显示交高亮线的颜色
        txDataSet.setDrawFilled(true);// 禁用范围背景填充
        Drawable drawable = new ColorDrawable(ContextCompat.getColor(context, R.color._178A49FF));
        txDataSet.setFillDrawable(drawable);
        txDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.valueOf((int)value);
            }
        });

        return txDataSet;
    }


    private static void initChart(Context context, LineChart chart, @NonNull List<TrafficRateBean> rateBeans) {
        //禁止与图表的所有可能的触摸交互
        chart.setTouchEnabled(false);


        //去掉描述信息
        Description description = new Description();
        description.setText("");
        description.setTextColor(Color.RED);
        description.setTextSize(20);
        chart.setDescription(description); // 设置图表描述信息

        //设置图列
        Legend le = chart.getLegend();
        le.setOrientation(Legend.LegendOrientation.HORIZONTAL); // 图列水平排列
        le.setTextColor(Color.parseColor("#FF9D9D9D"));
        le.setTextSize(12);
        le.setForm(Legend.LegendForm.CIRCLE);
        le.setFormSize(14);
        le.setFormLineWidth(5);
        le.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        le.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        le.setYOffset(16);
        le.setEnabled(false);

        chart.setNoDataText(""); // 没有数据时显示的文字
        chart.setNoDataTextColor(Color.parseColor("#FF9D9D9D")); // 没有数据时显示文字的颜色
        chart.setDrawGridBackground(false); //chart 绘图区后面的背景矩形将绘制
        chart.setDrawBorders(false); // 禁止绘制图表边框的线
        chart.setScaleEnabled(false);

        chart.animateXY(0, 0); // 去掉绘制图表时的动画（从左到右，从下到上）
        chart.setMinOffset(0); // 去掉图表的边距（默认15dp）

        // x轴线
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawLabels(false); // 显示 或 隐藏
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false); // 隐藏x轴线
        xAxis.setAxisLineColor(Color.parseColor("#FFC6C6C6"));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 设置X轴位置
        xAxis.setTextColor(Color.parseColor("#FF2E2E2E"));
        // 设置竖线的显示样式为虚线 lineLength控制虚线段的长度 spaceLength控制线之间的空间
        //mXAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setTextSize(10);
        //启动坐标间隔控制，避免坐标值绘制重复。还可以通过setGranularity指定间隔
//            xAxis.setGranularityEnabled(true);

        long minX;
        long maxX;
        int size = rateBeans.size();

        if (size == 0) {
            minX = System.currentTimeMillis();
            maxX = System.currentTimeMillis() + ConvertUtils.timeSpan2Millis(1, TimeConstants.MIN);
        } else {
            TrafficRateBean oldestItem = rateBeans.get(0);
            TrafficRateBean newestItem = rateBeans.get(size - 1);
            minX = oldestItem.time;
//            maxX = newestItem.getTime() + ConvertUtils.timeSpan2Millis(1, TimeConstants.MIN);
            maxX = newestItem.time;
        }
//        LogUtils.e(TAG, "--> minX=" + TimeUtils.millis2String(minX, "HH:mm:ss") + "  maxX=" +  TimeUtils.millis2String(maxX, "HH:mm:ss"));
        Calendar minCalendar = Calendar.getInstance();
        minCalendar.setTimeInMillis(minX);
        int minMs = minCalendar.get(Calendar.MILLISECOND);
        int minSec = minCalendar.get(Calendar.SECOND);
        int minMinute = minCalendar.get(Calendar.MINUTE);
//        LogUtils.e(TAG, "--> minHour=" + minHour + "  minMinute=" + minMinute + "  minSec=" + minSec);
        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.setTimeInMillis(maxX);
        int maxMs = maxCalendar.get(Calendar.MILLISECOND);
        int maxSec = maxCalendar.get(Calendar.SECOND);
        int maxMinute = maxCalendar.get(Calendar.MINUTE);
//        LogUtils.e(TAG, "--> maxHour=" + maxHour + "  maxMinute=" + maxMinute + "  maxSec=" + maxSec);
        int msMinX = minMs + minSec * 1000 + minMinute * 60 * 1000;
        int msMaxX = maxMs + maxSec * 1000 + maxMinute * 60 * 1000;


        xAxis.setAxisMinimum(msMinX);
        xAxis.setAxisMaximum(msMaxX);
        xAxis.setLabelCount(5, true);// 一共显示5个数据点，每个数据点对应一个 x轴坐标
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                if (value >= secMinX && value <= secMaxX) {
//                    SimpleDateFormat dateFormat = TimeUtils.getSafeDateFormat("HH:mm:ss");
//                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//                    return TimeUtils.millis2String((long)(value * 1000L), dateFormat);
//                } else {
//                    return "";
//                }
//            }
//        });

        // 左轴
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawLabels(false); // 显示 或 隐藏左轴坐标值
        yAxis.setDrawAxisLine(false); // 隐藏左轴线
        yAxis.setAxisLineColor(Color.parseColor("#FFC6C6C6"));
        yAxis.setTextColor(Color.parseColor("#FF8C857E"));
        yAxis.setTextSize(12);
        yAxis.setDrawGridLines(false); // true 绘制横线虚线
        yAxis.setGridColor(Color.parseColor("#FFC6C6C6"));
        yAxis.enableGridDashedLine(ConvertUtils.dp2px(5), ConvertUtils.dp2px(5), 0f);

        float minRate = Math.min(TrafficChartManager.getInstance().minRxRate(), TrafficChartManager.getInstance().minTxRate());
        float maxRate = Math.max(TrafficChartManager.getInstance().maxRxRate(), TrafficChartManager.getInstance().maxTxRate());
        float minY = minRate * 0.8f;
        float maxY = maxRate * 1.2f;
        yAxis.setAxisMinimum(minY);
        yAxis.setAxisMaximum(maxY);
//            yAxis.setLabelXOffset(-15f);
        yAxis.setDrawTopYLabelEntry(true); // true 允许绘制 Y轴上 最上面的坐标值，false 不允许
        yAxis.setLabelCount(8, true); // Y轴上一共绘制 5个坐标值
//        yAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                if (value < 0) {
//                    return String.valueOf(value);
//                }
//                String formatValue = NetworkTestManager.byte2FitMemorySize((long) value);
//                String unit = formatValue.substring(formatValue.length() - 1);
//                String strValue = NumUtils.subZeroAndDot(formatValue.substring(0, formatValue.length() - 1));
//                return strValue + unit;
//            }
//        });

        // 右轴
        YAxis rightYAxis = chart.getAxisRight();
        rightYAxis.setDrawLabels(false); // 显示 或 隐藏
        rightYAxis.setDrawAxisLine(false); // 右轴线
        rightYAxis.setDrawGridLines(false);
        rightYAxis.setDrawTopYLabelEntry(false);
        rightYAxis.setTextColor(Color.parseColor("#FF8C857E"));
    }
}
