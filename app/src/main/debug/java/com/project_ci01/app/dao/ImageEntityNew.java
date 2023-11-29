package com.project_ci01.app.dao;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.blankj.utilcode.util.TimeUtils;
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

    public String saveImagePath; // 保存图片的路径

    public long colorTime; // 进入填色页的时间
    public boolean completed; // 是否已完成填色
    public int colorCount; // 已填色的像素点个数
    public int totalCount; // 总像素点个数（去掉白色和透明色）


    public ImageEntityNew(long id, long createTime, int imageId, String fileName, String description, List<String> permission, List<String> display, String category, String fromType,
                          String filePath, String storeDir/*, String originImagePath*/, String colorImagePath, String pixelsObjPath, String saveImagePath,
                          long colorTime, boolean completed, int colorCount, int totalCount) {
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
        this.saveImagePath = saveImagePath;
        this.colorTime = colorTime;
        this.completed = completed;
        this.colorCount = colorCount;
        this.totalCount = totalCount;
    }

//    // for Home
//    @Ignore
//    public ImageEntityNew(Context context, long createTime, String fileName, String fromType, String category) {
//        this.createTime = createTime;
//        this.colorTime = 0;
//        this.fileName = fileName;
//        this.filePath = null;
//        this.fromType = fromType;
//        this.category = category;
//        this.storeDir = context.getCacheDir() + File.separator
//                + fromType + File.separator + category + File.separator
//                + StringUtils.trimSuffix(fileName);
////        this.originImagePath = storeDir + File.separator + fileName;
//        this.colorImagePath = storeDir + File.separator + "color_image";
//        this.pixelsObjPath = storeDir + File.separator + "pixel_list";
//        this.completed = false;
//    }

//    // for daily
//    @Ignore
//    public ImageEntityNew(Context context, long createTime, String fileName, String fromType, String category, String dateOfMonth) {
//        this.createTime = createTime;
//        this.colorTime = 0;
//        this.fileName = fileName;
//        this.filePath = null;
//        this.fromType = fromType;
//        this.category = category;
//        this.storeDir = context.getCacheDir() + File.separator
//                + fromType + File.separator + category + File.separator + dateOfMonth + File.separator
//                + StringUtils.trimSuffix(fileName);
////        this.originImagePath = storeDir + File.separator + fileName;
//        this.colorImagePath = storeDir + File.separator + "color_image";
//        this.pixelsObjPath = storeDir + File.separator + "pixel_list";
//        this.completed = false;
//    }

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
        saveImagePath = in.readString();
        colorTime = in.readLong();
        completed = in.readByte() != 0;
        colorCount = in.readInt();
        totalCount = in.readInt();
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
        dest.writeString(saveImagePath);
        dest.writeLong(colorTime);
        dest.writeByte((byte) (completed ? 1 : 0));
        dest.writeInt(colorCount);
        dest.writeInt(totalCount);
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

    public void update(ImageEntityNew entity) {
        this.id = entity.id;
        this.createTime = entity.createTime;
        this.imageId = entity.imageId;
        this.fileName = entity.fileName;
        this.description = entity.description;
        this.permission = entity.permission;
        this.display = entity.display;
        this.category = entity.category;
        this.fromType = entity.fromType;
        this.filePath = entity.filePath;
        this.storeDir = entity.storeDir;
//        this.originImagePath = entity.originImagePath;
        this.colorImagePath = entity.colorImagePath;
        this.pixelsObjPath = entity.pixelsObjPath;
        this.saveImagePath = entity.saveImagePath;
        this.colorTime = entity.colorTime;
        this.completed = entity.completed;
        this.colorCount = entity.colorCount;
        this.totalCount = entity.totalCount;
    }

    @Override
    public String toString() {
        return "ImageEntity{" +
                "imageId=" + imageId +
//                ", fileName='" + fileName + '\'' +
//                ", fromType='" + fromType + '\'' +
//                ", storeDir='" + storeDir + '\'' +
                ", completed=" + completed +
                ", colorCount=" + colorCount +
                ", totalCount=" + totalCount +
                ", createTime=" + TimeUtils.millis2String(createTime, "yyyy-MM-dd HH:ss:mm") +
                ", colorTime=" + colorTime +
                ", display=" + display +
                ", category='" + category + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
