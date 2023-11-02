package com.project_m1142.app.dao;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.project_m1142.app.base.manage.LifecyclerManager;

@Database(entities = {TestHistoryEntity.class}, version = 1, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "my_db";

    private volatile static MyDatabase instance;

    public static MyDatabase getInstance() {
        if(instance == null) {
            synchronized(MyDatabase.class) {
                if(instance == null) {
                    instance = Room.databaseBuilder(LifecyclerManager.INSTANCE.getApplication(), MyDatabase.class, DATABASE_NAME).build();
                }
            }
        }
        return instance;
    }

    public abstract TestHistoryDao testHistoryDao();
}
