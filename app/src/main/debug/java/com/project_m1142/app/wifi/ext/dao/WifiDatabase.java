package com.project_m1142.app.wifi.ext.dao;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.project_m1142.app.base.manage.LifecyclerManager;

@Database(entities = {WifiEntity.class}, version = 1, exportSchema = false)
public abstract class WifiDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "wifi_db";

    private volatile static WifiDatabase instance;

    public static WifiDatabase getInstance() {
        if(instance == null) {
            synchronized(WifiDatabase.class) {
                if(instance == null) {
                    instance = Room.databaseBuilder(LifecyclerManager.INSTANCE.getApplication(), WifiDatabase.class, DATABASE_NAME).build();
                }
            }
        }
        return instance;
    }

    public abstract WifiDao wifiDao();
}
