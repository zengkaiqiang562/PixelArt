package com.project_ci01.app.dao;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.project_ci01.app.base.manage.LifecyclerManager;


@Database(entities = {ImageEntity.class}, version = 1, exportSchema = false)
public abstract class ImageDB extends RoomDatabase {
    private static final String DATABASE_NAME = "image_db";

    private volatile static ImageDB instance;

    public static ImageDB getInstance() {
        if(instance == null) {
            synchronized(ImageDB.class) {
                if(instance == null) {
                    instance = Room.databaseBuilder(LifecyclerManager.INSTANCE.getApplication(), ImageDB.class, DATABASE_NAME).build();
                }
            }
        }
        return instance;
    }

    public abstract ImageDao imageDao();
}
