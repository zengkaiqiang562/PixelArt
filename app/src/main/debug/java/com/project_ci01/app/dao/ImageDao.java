package com.project_ci01.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ImageDao {
    @Insert
    long addImage(ImageEntity image); // 返回记录id

    @Delete
    int deleteImage(ImageEntity image); // 返回删除行数

    @Delete
    int deleteImage(List<ImageEntity> entities); // 返回删除行数

    @Query("delete from table_image")
    int deleteAll();

    @Update
    int updateImage(ImageEntity image); //

    @Query("select * from table_image order by createTime desc")
    List<ImageEntity> queryAll();

    @Query("select * from table_image where createTime= :createTime")
    List<ImageEntity> queryByCreateTime(long createTime);

    @Query("select * from table_image where storeDir= :storeDir")
    List<ImageEntity> queryByStoreDir(String storeDir);

    @Query("select * from table_image where fromType= :fromType")
    List<ImageEntity> queryByFromType(String fromType);

    @Query("select * from table_image where category= :category")
    List<ImageEntity> queryByCategory(String category);

    @Query("select * from table_image where completed= 1 order by colorTime desc")
    List<ImageEntity> queryCompleted();

    @Query("select * from table_image where completed= 0 and colorTime > 0  order by colorTime desc")
    List<ImageEntity> queryInProgress();

    @Query("select count(*) from table_image")
    int countImage();

    @Query("select count(*) from table_image where storeDir= :storeDir")
    int countByStoreDir(String storeDir);

    @Query("select count(*) from table_image where completed= 1")
    int countCompleted();

    @Query("select count(*) from table_image where completed= 0 and colorTime > 0")
    int countInProgress();

    @Query("select count(*) from table_image where createTime >= :startCreateTime and createTime <= :endCreateTime")
    int countByCreateTimeRange(long startCreateTime, long endCreateTime);

    @Query("select * from table_image where createTime >= :startCreateTime and createTime <= :endCreateTime order by createTime desc")
    List<ImageEntity> queryByCreateTimeRange(long startCreateTime, long endCreateTime);
}
