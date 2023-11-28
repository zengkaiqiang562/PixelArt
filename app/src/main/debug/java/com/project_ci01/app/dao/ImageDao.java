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
    long addImage(ImageEntityNew image); // 返回记录id

//    @Delete
//    int deleteImage(ImageEntityNew image); // 返回删除行数
//
//    @Delete
//    int deleteImage(List<ImageEntityNew> entities); // 返回删除行数

//    @Query("delete from table_image_new")
//    int deleteAll();

    @Update
    int updateImage(ImageEntityNew image); //

    @Query("select * from table_image_new order by createTime desc")
    List<ImageEntityNew> queryAll(); // 包含 daily

    @Query("select * from table_image_new where category in ('love', 'cartoon', 'food') order by createTime desc")
    List<ImageEntityNew> queryAllInHome(); // 排除 daily

    @Query("select * from table_image_new where createTime= :createTime")
    List<ImageEntityNew> queryByCreateTime(long createTime);

    @Query("select * from table_image_new where storeDir= :storeDir")
    List<ImageEntityNew> queryByStoreDir(String storeDir);

    @Query("select * from table_image_new where fromType= :fromType order by createTime desc")
    List<ImageEntityNew> queryByFromType(String fromType);

    @Query("select * from table_image_new where category= :category order by createTime desc")
    List<ImageEntityNew> queryByCategory(String category);

    @Query("select * from table_image_new where completed= 1 order by colorTime desc")
    List<ImageEntityNew> queryCompleted();

    @Query("select * from table_image_new where completed= 0 and colorTime > 0  order by colorTime desc")
    List<ImageEntityNew> queryInProgress();

    @Query("select count(*) from table_image_new")
    int countImage();

    @Query("select count(*) from table_image_new where storeDir= :storeDir")
    int countByStoreDir(String storeDir);

    @Query("select count(*) from table_image_new where completed= 1")
    int countCompleted();

    @Query("select count(*) from table_image_new where completed= 0 and colorTime > 0")
    int countInProgress();

    /*==============================================*/
    @Query("update table_image_new set fileName= :fileName, description= :description, permission= :permission, display= :display  where imageId= :imageId")
    int updateImage(int imageId, String fileName, String description, List<String> permission, List<String> display);
    @Query("update table_image_new set colorTime= :colorTime, completed= :completed where imageId= :imageId")
    int updateImage(int imageId, long colorTime, boolean completed);

    @Query("update table_image_new set saveImagePath= :saveImagePath where imageId= :imageId")
    int updateImage(int imageId, String saveImagePath);

    @Query("select count(*) from table_image_new where imageId= :imageId")
    int countByImageId(long imageId);

    @Query("select * from table_image_new where imageId= :imageId")
    List<ImageEntityNew> queryByImageId(int imageId);

    @Query("select distinct category from table_image_new")
    List<String> queryAllCategories();

    @Query("select distinct category from table_image_new where category not in ('Daily')")
    List<String> queryHomeCategories();

    @Query("select * from table_image_new where category= :category and createTime <= :endTime order by createTime desc")
    List<ImageEntityNew> queryByCategory(String category, long endTime);

    @Query("select count(*) from table_image_new where category= :category and createTime <= :endTime order by createTime desc")
    int countByCategory(String category, long endTime);

    @Query("select * from table_image_new where category= :category and createTime >= :startTime and createTime <= :endTime order by createTime desc")
    List<ImageEntityNew> queryCategoryByRange(String category, long startTime, long endTime);

    @Query("select * from table_image_new where category not in ('Daily') and createTime <= :endTime order by createTime desc")
    List<ImageEntityNew> queryAllInHome(long endTime); // 排除 daily

    @Query("select count(*) from table_image_new where category not in ('Daily') and createTime <= :endTime order by createTime desc")
    int countAllInHome(long endTime); // 排除 daily

    @Query("select * from table_image_new where category not in ('Daily') and createTime >= :startTime and createTime <= :endTime order by createTime desc")
    List<ImageEntityNew> queryAllInHomeByRange(long startTime, long endTime); // 排除 daily

    @Query("select * from table_image_new where category not in ('Daily') and display like '%All%' and createTime <= :endTime order by createTime desc")
    List<ImageEntityNew> queryAllRecommendInHome(long endTime); // 查询 All 中的推荐记录

    @Query("select * from table_image_new where category not in ('Daily') and display not like '%All%' and createTime <= :endTime order by createTime desc")
    List<ImageEntityNew> queryAllUnrecommendInHome(long endTime); // 查询 All 中的非推荐记录

    @Query("select * from table_image_new where category= :category and display like '%' || :category || '%' and createTime <= :endTime order by createTime desc")
    List<ImageEntityNew> queryRecommendCategory(String category, long endTime); // 查询 category 中的推荐记录

    @Query("select * from table_image_new where category= :category and display not like '%' || :category || '%' and createTime <= :endTime order by createTime desc")
    List<ImageEntityNew> queryUnrecommendCategory(String category, long endTime); // 查询 category 中的非推荐记录

    @Query("select * from table_image_new where  display like '%' || :display || '%' ")
    List<ImageEntityNew> queryByDisplay(String display); // 查询某个具体推荐位置的记录，如 All_1 , Cartoon_1 等

    @Query("select * from table_image_new where category='Daily' and createTime >= :startTime and createTime <= :endTime order by createTime desc")
    List<ImageEntityNew> queryDailyInRange(long startTime, long endTime); // 查询某天更新的图片

    @Query("select * from table_image_new where colorTime=0 and createTime <= :endTime order by createTime desc")
    List<ImageEntityNew> queryWithoutColor(long endTime); // 查询未填色的图片
}
