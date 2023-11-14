package com.project_ci01.app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.project_ci01.app.pixel.PixelManager;

import java.io.File;
import java.util.regex.Pattern;

@Entity(tableName = "table_image")
public class ImageEntity implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long createTime;

    public long colorTime; // 最近一次的填色时间（进入过填色页填过色或者重置就有值，没进过填色页或删除就置为0）

    public String fileName; // 原图文件名

    public String netUrl; // 当 fromType 是网络时，图片的网络url地址

    public String fromType; // 来源（本地，网络，自己创建）

    public String category; // 类别（food, cartoon, love, mystery 盲盒）

    public String storeDir; // 图片，像素对象的存储目录
    public String originImagePath; // 原图的本地存储路径
    public String colorImagePath; // 填色图的本地储存路径（未填色就是灰色图）
    public String pixelsObjPath; // 像素集对象（PixelList）的持久化储存路径

    public boolean completed; // 是否已完成填色

    public ImageEntity(long id, long createTime, long colorTime, String fileName, String netUrl, String fromType, String category,
                       String storeDir, String originImagePath, String colorImagePath, String pixelsObjPath, boolean completed) {
        this.id = id;
        this.createTime = createTime;
        this.colorTime = colorTime;
        this.fileName = fileName;
        this.netUrl = netUrl;
        this.fromType = fromType;
        this.category = category;
        this.storeDir = storeDir;
        this.originImagePath = originImagePath;
        this.colorImagePath = colorImagePath;
        this.pixelsObjPath = pixelsObjPath;
        this.completed = completed;
    }

    @Ignore
    private ImageEntity(long createTime, long colorTime, String fileName, String netUrl, String fromType, String category, boolean completed) {
        this.createTime = createTime;
        this.colorTime = colorTime;
        this.fileName = fileName;
        this.netUrl = netUrl;
        this.fromType = fromType;
        this.category = category;
        this.storeDir = PixelManager.getInstance().storeDir(fromType, category, fileName);
        this.originImagePath = storeDir + File.separator + fileName;
        this.colorImagePath = storeDir + File.separator + "color_image";
        this.pixelsObjPath = storeDir + File.separator + "pixel_list";
        this.completed = completed;
    }

    @Ignore
    public ImageEntity(long createTime, String fileName, String fromType, String category) {
        this(createTime, 0, fileName, null, fromType, category, false);
    }

    @Ignore
    public ImageEntity() {}

    @Ignore
    protected ImageEntity(Parcel in) {
        id = in.readLong();
        createTime = in.readLong();
        colorTime = in.readLong();
        fileName = in.readString();
        netUrl = in.readString();
        fromType = in.readString();
        category = in.readString();
        storeDir = in.readString();
        originImagePath = in.readString();
        colorImagePath = in.readString();
        pixelsObjPath = in.readString();
        completed = in.readByte() != 0;
    }

    public static final Creator<ImageEntity> CREATOR = new Creator<ImageEntity>() {
        @Override
        public ImageEntity createFromParcel(Parcel in) {
            return new ImageEntity(in);
        }

        @Override
        public ImageEntity[] newArray(int size) {
            return new ImageEntity[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageEntity that = (ImageEntity) o;

        return storeDir.equals(that.storeDir);
    }

    @Override
    public int hashCode() {
        return storeDir.hashCode();
    }

    @Override
    public String toString() {
        return "ImageEntity{" +
                "colorTime=" + colorTime +
                ", fileName='" + fileName + '\'' +
                ", fromType='" + fromType + '\'' +
                ", category='" + category + '\'' +
                ", storeDir='" + storeDir + '\'' +
                ", completed=" + completed +
                '}';
    }

    public boolean inProgress() {
        return colorTime > 0 && !completed; // 填色时间大于0 且未完成，就在进行中
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(createTime);
        dest.writeLong(colorTime);
        dest.writeString(fileName);
        dest.writeString(netUrl);
        dest.writeString(fromType);
        dest.writeString(category);
        dest.writeString(storeDir);
        dest.writeString(originImagePath);
        dest.writeString(colorImagePath);
        dest.writeString(pixelsObjPath);
        dest.writeByte((byte) (completed ? 1 : 0));
    }
}
