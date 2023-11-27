package com.project_ci01.app.pixel;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.google.gson.reflect.TypeToken;
import com.project_ci01.app.base.utils.BitmapUtils;
import com.project_ci01.app.base.utils.FileUtils;
import com.project_ci01.app.dao.ImageEntityNew;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PixelHelper {
    private static final String TAG = "PixelHelper";

    public static int getStdPixelUnitSize(@NonNull PixelList pixelList) {
        int bitmapWidth = pixelList.originWidth;
        int screenWidth = ScreenUtils.getScreenWidth();
        float unitSize = screenWidth * 1f / bitmapWidth;
//        LogUtils.e(TAG, "--> getPixelUnitSize()  unitSize=" + unitSize);
        return  unitSize < 1 ? 1 : Math.round(unitSize);
    }

    public static int getHintPixelUnitSize() {
        int screenWidth = ScreenUtils.getScreenWidth();
        float unitSize = screenWidth / 14f; // 单个像素点尺寸占屏幕宽度的 1/14
//        LogUtils.e(TAG, "--> getHintPixelUnitSize()  unitSize=" + unitSize);
        return  unitSize < 1 ? 1 : Math.round(unitSize);
    }
    public static int getMaxPixelUnitSize() {
        int screenWidth = ScreenUtils.getScreenWidth();
        float unitSize = screenWidth / 4f; // 单个像素点尺寸占屏幕宽度的 1/3 为最大
//        LogUtils.e(TAG, "--> getHintPixelUnitSize()  unitSize=" + unitSize);
        return  unitSize < 1 ? 1 : Math.round(unitSize);
    }

    public static int getMinPixelUnitSize(@NonNull PixelList pixelList) {
        int bitmapWidth = pixelList.originWidth;
        int screenWidth = ScreenUtils.getScreenWidth();
        float unitSize = screenWidth * 1f / bitmapWidth / 2; // 单个像素尺寸最小缩小到原图的一半
//        LogUtils.e(TAG, "--> getPixelUnitSize()  unitSize=" + unitSize);
        return  unitSize < 1 ? 1 : Math.round(unitSize);
    }


    public static int getListPixelUnitSize(@NonNull PixelList pixelList) {
        int bitmapWidth = pixelList.originWidth;
        int dp162 = ConvertUtils.dp2px(162);
        float unitSize = dp162 * 1f / bitmapWidth;
//        LogUtils.e(TAG, "--> getListPixelUnitSize()  unitSize=" + unitSize);
        return  unitSize < 1 ? 1 : Math.round(unitSize);
    }

    /**
     * @param mapResult
     * Map<Integer, int[]> 某种颜色 Integer 的总像素点个数int[0] 和 已绘制个数 int[1]
     *
     * @return
     * result[0] 总像素点个数（去除白色和透明色）
     * result[1] 已绘制像素点个数（去除白色和透明色）
     */
    public static int[] countDrawnPixels(@NonNull PixelList pixelList, @NonNull Map<Integer, int[]> mapResult) {
        int[] result = new int[2];

        mapResult.clear();

        List<PixelUnit> pixels = pixelList.pixels;
        for (PixelUnit pixel : pixels) {
            int color = pixel.color;
            if (PixelHelper.ignoreColor(color)) {
                continue;
            }

            int[] colorResult = mapResult.get(color);
            if (colorResult == null) {
                colorResult = new int[2];
                mapResult.put(color, colorResult);
            }

            ++result[0];
            ++colorResult[0];
            if (pixel.enableDraw) {
                ++result[1];
                ++colorResult[1];
            }
        }

        return result;
    }

    /**
     * @return
     * result[0] 总像素点个数（去除白色和透明色）
     * result[1] 已绘制像素点个数（去除白色和透明色）
     * Map<Integer, int[]> 某种颜色 Integer 的总像素点个数int[0] 和 已绘制个数 int[1]
     */
    public static int[] countDrawnPixels(@NonNull PixelList pixelList) {
        int[] result = new int[2];

        List<PixelUnit> pixels = pixelList.pixels;
        for (PixelUnit pixel : pixels) {
            if (PixelHelper.ignorePixel(pixel)) {
                continue;
            }
            ++result[0];
            if (pixel.enableDraw) {
                ++result[1];
            }
        }

        return result;
    }


    /**
     *
     * @return 检查是否要处理 pixel
     * 目前 不处理白色和透明
     */
    public static boolean ignorePixel(@NonNull PixelUnit pixel) {
        // 不处理白色和透明
        return ignoreColor(pixel.color);
    }

    public static boolean ignoreColor(int color) {
        // 不处理白色和透明
        boolean likeWhite = (Color.red(color) >= 250 && Color.green(color) >= 250 && Color.blue(color) >= 250) || Color.alpha(color) < 20; // 类似白色也不处理
        return color == Color.WHITE || color == Color.TRANSPARENT || likeWhite;
    }

    /*========================*/

    public static void writeColorImage(String colorImagePath, @NonNull PixelList pixelList, boolean delIfExist) {
        File file = new File(colorImagePath);
        if (file.exists() && !delIfExist) { // 文件存在时不处理
            return;
        }
        Canvas canvas = new Canvas();
        Rect rect = new Rect();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        int pixelUnit = PixelHelper.getListPixelUnitSize(pixelList);
        int width = pixelList.originWidth * pixelUnit;
        int height = pixelList.originHeight * pixelUnit;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);

        List<PixelUnit> pixels = pixelList.pixels;

        for (PixelUnit pixel : pixels) {
            if (PixelHelper.ignorePixel(pixel)) {
                continue;
            }
            int left = pixel.x * pixelUnit;
            int right = pixel.x * pixelUnit + pixelUnit;
            int top = pixel.y * pixelUnit;
            int bottom = pixel.y * pixelUnit + pixelUnit;
            rect.set(left, top, right, bottom);
            if (pixel.enableDraw) {
                paint.setColor(pixel.color);
            } else {
                paint.setColor(BitmapUtils.convertGrey(pixel.color));
            }
            canvas.drawRect(rect, paint);
        }

        FileUtils.saveBitmap(bitmap, colorImagePath);
        bitmap.recycle();
        canvas.setBitmap(null);
    }

    /*==================================================*/

    public static PixelList getAllPixels(@NonNull Bitmap bitmap/*, int unit*/) { // bitmap 像素点太多时，会内存溢出，所以 bitmap 一定是很小很小的原图
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        List<PixelUnit> pixels = new ArrayList<>();
        PixelUnit pixel;
        for (int x = 0; x < bitmapWidth; x++) { // 按列遍历（先垂直遍历[内]，再水平遍历[外]）
            for (int y = 0; y < bitmapHeight; y++) {

                int color = bitmap.getPixel(x, y);
                pixel = new PixelUnit(x, y, color, false);

                // 添加到总集合
                pixels.add(pixel);
            }
        }

        return new PixelList(pixels/*, unit, unit*/, bitmapWidth, bitmapHeight);
    }

    public static Map<Integer, String> getNumberMap(@NonNull PixelList pixelList, @NonNull Map<Integer, List<PixelUnit>> colorMap) {
        Map<Integer, String> numberMap = new HashMap<>(); // 颜色-Number 键值对
        List<Map.Entry<Integer, List<PixelUnit>>> colorEntries = new ArrayList<>(colorMap.entrySet());
        Collections.sort(colorEntries, (o1, o2) -> { // 同颜色像素点多的排在前面
            int size1 = o1.getValue().size();
            int size2 = o2.getValue().size();
            return size2 - size1;
        });

        int number = 1;
        for (int index = 0; index < colorEntries.size(); index++) {
            Integer color = colorEntries.get(index).getKey();
            if (PixelHelper.ignoreColor(color)) { // 不处理白色和透明
                continue;
            }
            numberMap.put(color, String.valueOf(number));
            number++;
        }
        return numberMap;
    }

    public static Map<Integer, List<PixelUnit>> getColorMap(@NonNull PixelList pixelList) {
        Map<Integer, List<PixelUnit>> colorMap = new HashMap<>();

        int color;
        List<PixelUnit> pixels = pixelList.pixels;
        for (PixelUnit pixel : pixels) {
            color = pixel.color;
            // 添加到按颜色分类的集合
            if (!ignoreColor(color)) { // 不处理白色和透明
                List<PixelUnit> colorPixels = colorMap.get(color);
                if (colorPixels == null) {
                    colorPixels = new ArrayList<>();
                    colorMap.put(color, colorPixels);
                }
                colorPixels.add(pixel);
            }
        }

        return colorMap;
    }

    public static Map<Integer, List<List<PixelUnit>>> getAdjoinMap(@NonNull PixelList pixelList) {
        int bitmapWidth = pixelList.originWidth;
        int bitmapHeight = pixelList.originHeight;
        /*
        int column = (int) (left / lastPixelUnit); // 第几列
        int row = (int) (top / lastPixelUnit); // 第几行
        int pixelIndex = row + column * pixelList.originHeight; // pixelList 集合是按列遍历的， 一列有 pixelList.originHeight 个像素
        当前索引 index，与之相邻的索引有：
            index = x * bitmapHeight + y
            x = index / bitmapHeight 取整
            y = index % bitmapHeight 取余

            x-1, y // 左    -> indexL = (x-1) * bitmapHeight + y
            x+1, y // 右    -> indexR = (x+1) * bitmapHeight + y
            x, y-1 // 上    -> indexT = x * bitmapHeight + (y-1)
            x, y+1 // 下    -> indexB = x * bitmapHeight + (y+1)
            x-1,y-1 // 左上 -> indexLT = (x-1) * bitmapHeight + (y-1)
            x+1,y-1 // 右上 -> indexRT = (x+1) * bitmapHeight + (y-1)
            x-1,y+1 // 左下 -> indexLB = (x-1) * bitmapHeight + (y+1)
            x+1,y+1 // 右下 -> indexRB = (x+1) * bitmapHeight + (y+1)

            向下垂直，再向右水平遍历，只需考虑已遍历过的点，即：左，上，左上，左下
         */

        Map<Integer, List<List<PixelUnit>>> adjoinMap = new HashMap<>(); // key 为 color，value 为一组组的相邻同色集

        int color;
        int x;
        int y;
        List<PixelUnit> pixels = pixelList.pixels;
        for (PixelUnit pixel : pixels) {
            x = pixel.x;
            y = pixel.y;
            color = pixel.color;
            // 相邻同色像素点放一起
            if (!ignoreColor(color)) { // 不处理白色和透明
                boolean adjoinL = false; // 是否左邻同色
                if ((x - 1) >= 0) { // 存在 左 邻像素点
                    int indexL = (x-1) * bitmapHeight + y;
                    PixelUnit pixelL = pixels.get(indexL);
                    if (color == pixelL.color) { // 左邻同色
                        adjoinL = true;
                        List<List<PixelUnit>> adjoinOuters = adjoinMap.get(color);
                        if (adjoinOuters != null) {
                            for (List<PixelUnit> adjoinInners : adjoinOuters) {
                                if (adjoinInners.contains(pixelL) && !adjoinInners.contains(pixel)) {
                                    adjoinInners.add(pixel); // 存入相邻集
                                    break;
                                }
                            }
                        }
                    }
                }

                boolean adjoinLT = false; // 是否 左上 邻同色
                if ((x - 1) >= 0 && (y - 1) >= 0) { // 存在 左上 邻像素点
                    int indexLT = (x-1) * bitmapHeight + (y-1);
                    PixelUnit pixelLT = pixels.get(indexLT);
                    if (color == pixelLT.color) { // 左上 邻同色
                        adjoinLT = true;
                        List<List<PixelUnit>> adjoinOuters = adjoinMap.get(color);
                        if (adjoinOuters != null) {
                            for (List<PixelUnit> adjoinInners : adjoinOuters) {
                                if (adjoinInners.contains(pixelLT) && !adjoinInners.contains(pixel)) {
                                    adjoinInners.add(pixel); // 存入相邻集
                                    break;
                                }
                            }
                        }
                    }
                }

                boolean adjoinT = false; // 是否 上 邻同色
                if ((y - 1) >= 0) { // 存在 上 邻像素点
                    int indexT = x * bitmapHeight + (y-1);
                    PixelUnit pixelT = pixels.get(indexT);
                    if (color == pixelT.color) { // 上 邻同色
                        adjoinT = true;
                        List<List<PixelUnit>> adjoinOuters = adjoinMap.get(color);
                        if (adjoinOuters != null) {
                            for (List<PixelUnit> adjoinInners : adjoinOuters) {
                                if (adjoinInners.contains(pixelT) && !adjoinInners.contains(pixel)) {
                                    adjoinInners.add(pixel); // 存入相邻集
                                    break;
                                }
                            }
                        }
                    }
                }

                boolean adjoinLB = false; // 是否 左下 邻同色
                if ((x - 1) >= 0 && (y + 1) < bitmapHeight) { // 存在 左下 邻像素点
                    int indexLB = (x-1) * bitmapHeight + (y+1);
                    PixelUnit pixelLB = pixels.get(indexLB);
                    if (color == pixelLB.color) { // 上 邻同色
                        adjoinLB = true;
                        List<List<PixelUnit>> adjoinOuters = adjoinMap.get(color);
                        if (adjoinOuters != null) {
                            for (List<PixelUnit> adjoinInners : adjoinOuters) {
                                if (adjoinInners.contains(pixelLB) && !adjoinInners.contains(pixel)) {
                                    adjoinInners.add(pixel); // 存入相邻集
                                    break;
                                }
                            }
                        }
                    }
                }

                // 1. 如果左下邻和左上邻都同色，可能存在 左下 和 左上 不在同一个相邻组的情况，此时需要合并相邻组
                // 2. 如果左下邻和上邻都同色，可能存在 左下 和 上 不在同一个相邻组的情况，此时需要合并相邻组
                if (adjoinLB && (adjoinLT || adjoinT)) {
                    List<List<PixelUnit>> adjoinOuters = adjoinMap.get(color);
                    if (adjoinOuters != null) {
                        List<PixelUnit> newAdjoinInners = new ArrayList<>();
                        for (int i = adjoinOuters.size() - 1; i >= 0; i--) {
                            List<PixelUnit> adjoinInners = adjoinOuters.get(i);
                            if (adjoinInners.contains(pixel)) {
                                newAdjoinInners.addAll(adjoinInners);
                                newAdjoinInners.remove(pixel); // 先移除，最后添加，不然可能 newAdjoinInners 有多个 pixel
                                adjoinOuters.remove(i);
                            }
                        }
                        newAdjoinInners.add(pixel);
                        adjoinOuters.add(newAdjoinInners);
                    }
                }

                if (!adjoinL && !adjoinLT && !adjoinT && !adjoinLB) { // 不存在相邻，则创建一个新的相邻组
                    List<List<PixelUnit>> adjoinOuters = adjoinMap.get(color); // 同颜色的所有相邻组的集合
                    if (adjoinOuters == null) {
                        adjoinOuters = new ArrayList<>();
                        adjoinMap.put(color, adjoinOuters);
                    }
                    List<PixelUnit> adjoinInners = new ArrayList<>(); // 新的相邻组
                    adjoinInners.add(pixel);
                    adjoinOuters.add(adjoinInners);
                }
            }
        }

        return adjoinMap;
    }


    public static void resetDraw(@NonNull PixelList pixelList) {
        List<PixelUnit> pixels = pixelList.pixels;
        int color;
        for (PixelUnit pixel : pixels) {
            color = pixel.color;
            if (!ignoreColor(color)) { // 不处理白色和透明
                pixel.enableDraw = false;
            }
        }
    }

    @Nullable
    public static PixelList getPixelList(@NonNull ImageEntityNew entity) {
        return FileUtils.readObjectByZipJson(PixelList.class, entity.pixelsObjPath);
    }

    @Nullable
    public static Map<Integer, List<List<PixelUnit>>> fetchAdjoinMapFromLocal(@NonNull ImageEntityNew entity) {
        String storePath = entity.storeDir + File.separator + "adjoin_map";
        return FileUtils.readObjectByZipJson(new TypeToken<>() {}, storePath);
    }

    public static void storeAdjoinMap2Local(@Nullable Map<Integer, List<List<PixelUnit>>> adjoinMap, @NonNull ImageEntityNew entity) {
        if (adjoinMap == null || adjoinMap.isEmpty()) {
            return;
        }
        String storePath = entity.storeDir + File.separator + "adjoin_map";
        FileUtils.writeObjectByZipJson(adjoinMap, storePath);
    }
}
