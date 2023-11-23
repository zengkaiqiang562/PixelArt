package com.project_ci01.app.dao;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.project_ci01.app.base.utils.StringUtils;

import java.io.File;
import java.util.List;

@TypeConverters({ ListConverter.class})
@Entity(tableName = "table_image_new")
public class ImageEntityNew implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long createTime;
    public int imageId;
    public String fileName; // 原图文件名
    public String description;
    public List<String> permission;
    public List<String> display;

    public String category; // 类别（food, cartoon, love, mystery 盲盒）
    public String fromType; // 来源（本地，网络，自己创建）
    public String filePath; // 当 fromType 是网络时，图片的网络url地址

    /*===========================================*/
    public String storeDir; // 图片，像素对象的存储目录
//    public String originImagePath; // 原图的本地存储路径
    public String colorImagePath; // 填色图的本地储存路径（未填色就是灰色图）
    public String pixelsObjPath; // 像素集对象（PixelList）的持久化储存路径

    public long colorTime; // 最近一次的填色时间（进入过填色页填过色或者重置就有值，没进过填色页或删除就置为0）
    public boolean completed; // 是否已完成填色


    public ImageEntityNew(long id, long createTime, int imageId, String fileName, String description, List<String> permission, List<String> display, String category, String fromType, String filePath, String storeDir/*, String originImagePath*/, String colorImagePath, String pixelsObjPath, long colorTime, boolean completed) {
        this.id = id;
        this.createTime = createTime;
        this.imageId = imageId;
        this.fileName = fileName;
        this.description = description;
        this.permission = permission;
        this.display = display;
        this.category = category;
        this.fromType = fromType;
        this.filePath = filePath;
        this.storeDir = storeDir;
//        this.originImagePath = originImagePath;
        this.colorImagePath = colorImagePath;
        this.pixelsObjPath = pixelsObjPath;
        this.colorTime = colorTime;
        this.completed = completed;
    }

    // for Home
    @Ignore
    public ImageEntityNew(Context context, long createTime, String fileName, String fromType, String category) {
        this.createTime = createTime;
        this.colorTime = 0;
        this.fileName = fileName;
        this.filePath = null;
        this.fromType = fromType;
        this.category = category;
        this.storeDir = context.getCacheDir() + File.separator
                + fromType + File.separator + category + File.separator
                + StringUtils.trimSuffix(fileName);
//        this.originImagePath = storeDir + File.separator + fileName;
        this.colorImagePath = storeDir + File.separator + "color_image";
        this.pixelsObjPath = storeDir + File.separator + "pixel_list";
        this.completed = false;
    }

    // for daily
    @Ignore
    public ImageEntityNew(Context context, long createTime, String fileName, String fromType, String category, String dateOfMonth) {
        this.createTime = createTime;
        this.colorTime = 0;
        this.fileName = fileName;
        this.filePath = null;
        this.fromType = fromType;
        this.category = category;
        this.storeDir = context.getCacheDir() + File.separator
                + fromType + File.separator + category + File.separator + dateOfMonth + File.separator
                + StringUtils.trimSuffix(fileName);
//        this.originImagePath = storeDir + File.separator + fileName;
        this.colorImagePath = storeDir + File.separator + "color_image";
        this.pixelsObjPath = storeDir + File.separator + "pixel_list";
        this.completed = false;
    }

    @Ignore
    public ImageEntityNew() {}


    protected ImageEntityNew(Parcel in) {
        id = in.readLong();
        createTime = in.readLong();
        imageId = in.readInt();
        fileName = in.readString();
        description = in.readString();
        permission = in.createStringArrayList();
        display = in.createStringArrayList();
        category = in.readString();
        fromType = in.readString();
        filePath = in.readString();
        storeDir = in.readString();
//        originImagePath = in.readString();
        colorImagePath = in.readString();
        pixelsObjPath = in.readString();
        colorTime = in.readLong();
        completed = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(createTime);
        dest.writeInt(imageId);
        dest.writeString(fileName);
        dest.writeString(description);
        dest.writeStringList(permission);
        dest.writeStringList(display);
        dest.writeString(category);
        dest.writeString(fromType);
        dest.writeString(filePath);
        dest.writeString(storeDir);
//        dest.writeString(originImagePath);
        dest.writeString(colorImagePath);
        dest.writeString(pixelsObjPath);
        dest.writeLong(colorTime);
        dest.writeByte((byte) (completed ? 1 : 0));
    }

    public static final Creator<ImageEntityNew> CREATOR = new Creator<ImageEntityNew>() {
        @Override
        public ImageEntityNew createFromParcel(Parcel in) {
            return new ImageEntityNew(in);
        }

        @Override
        public ImageEntityNew[] newArray(int size) {
            return new ImageEntityNew[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageEntityNew that = (ImageEntityNew) o;

        return storeDir.equals(that.storeDir);
    }

    @Override
    public int hashCode() {
        return storeDir.hashCode();
    }

    @Override
    public String toString() {
        return "ImageEntity{" +
                "imageId=" + imageId +
//                ", fileName='" + fileName + '\'' +
//                ", fromType='" + fromType + '\'' +
//                ", category='" + category + '\'' +
//                ", storeDir='" + storeDir + '\'' +
                ", completed=" + completed +
                ", display=" + display +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
