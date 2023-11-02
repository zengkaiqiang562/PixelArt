package com.project_m1142.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TestHistoryDao {
    @Insert
    long addHistory(TestHistoryEntity history); // 返回记录id

    @Delete
    int deleteHistory(TestHistoryEntity history); // 返回删除行数

    @Delete
    int deleteHistory(List<TestHistoryEntity> entities); // 返回删除行数

    @Query("delete from table_test_history")
    int deleteAll();

    @Update
    int updateHistory(TestHistoryEntity history); // 是否收藏用到这个，返回更新条数

    @Query("select * from table_test_history where createTime= :createTime")
    List<TestHistoryEntity> queryByCreateTime(long createTime);

    @Query("select * from table_test_history order by createTime desc")
    List<TestHistoryEntity> queryAll();

    @Query("select * from table_test_history order by createTime desc limit 7")
    List<TestHistoryEntity> queryLast7(); // 查询最近7条记录

    @Query("select * from table_test_history order by createTime desc limit 2")
    List<TestHistoryEntity> queryLast2(); // 查询最近2条记录

    @Query("select * from table_test_history order by createTime desc limit 1")
    List<TestHistoryEntity> queryLast1(); // 查询最近1条记录

    @Query("select count(*) from table_test_history")
    int countHistory();

    @Query("select count(*) from table_test_history where createTime >= :startCreateTime and createTime <= :endCreateTime")
    int countByCreateTimeRange(long startCreateTime, long endCreateTime);

    @Query("select * from table_test_history where createTime >= :startCreateTime and createTime <= :endCreateTime order by createTime desc")
    List<TestHistoryEntity> queryByCreateTimeRange(long startCreateTime, long endCreateTime);
}
