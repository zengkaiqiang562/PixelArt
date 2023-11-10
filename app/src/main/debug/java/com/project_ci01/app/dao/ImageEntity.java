package com.project_ci01.app.dao;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.File;
import java.util.regex.Pattern;

@Entity(tableName = "table_image")
public class ImageEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long createTime;

    public long colorTime; // 最近一次的填色时间（进入过填色页填过色或者重置就有值，没进过填色页或删除就置为0）

    public String fileName; // 原图文件名

//    public String originImagePath; // 原图的本地存储路径
//
//    public String colorImagePath; // 填色图的本地储存路径（未填色就是灰色图）
//
//    public String pixelsObjPath; // 像素集对象（PixelList）的持久化储存路径

    public String netUrl; // 当 fromType 是网络时，图片的网络url地址

    public int fromType; // 来源（本地，网络，自己创建）

    public int category; // 类别（food, cartoon, love, mystery 盲盒）

    public boolean completed; // 是否已完成填色

    public boolean inProgress() {
        return colorTime > 0 && !completed; // 填色时间大于0 且未完成，就在进行中
    }

    public String directDir() { // 相关数据的存储目录
        String fromTypeName = FromType.convert(fromType).typeName;
        String catName = Category.convert(category).catName;
        String noSuffixFileName = Pattern.compile("\\.[a-zA-Z0-9_]+$").matcher(fileName).replaceAll("");
        return fromTypeName + File.separator + catName + File.separator + noSuffixFileName;
    }

    public String originImagePath() { // 原图的本地存储路径
        return directDir() + File.separator + fileName;
    }

    public String colorImagePath() { // 填色图的本地储存路径（未填色就是灰色图）
        return directDir() + File.separator + "color_image";
    }

    public String pixelListPath() { // 像素集对象（PixelList）的持久化储存路径
        return directDir() + File.separator + "pixel_list";
    }
}
