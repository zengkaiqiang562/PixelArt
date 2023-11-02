package com.project_m1142.app.wifi.ext.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WifiDao {
    @Insert
    long addWifi(WifiEntity wifi); // 返回记录id

    @Delete
    int deleteWifi(WifiEntity wifi); // 返回删除行数

    @Delete
    int deleteWifi(List<WifiEntity> entities); // 返回删除行数

    @Query("delete from table_wifi")
    int deleteAll();

    @Query("delete from table_wifi where ssid=:ssid")
    int deleteBySsid(String ssid);

    @Update
    int updateWifi(WifiEntity wifi);

    @Query("select * from table_wifi where ssid= :ssid")
    List<WifiEntity> queryBySsid(String ssid);

    @Query("select * from table_wifi order by updateTime asc")
    List<WifiEntity> queryAll();


    @Query("select count(*) from table_wifi")
    int countWifi();
}
