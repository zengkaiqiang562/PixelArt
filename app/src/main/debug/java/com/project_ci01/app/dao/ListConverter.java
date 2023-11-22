package com.project_ci01.app.dao;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ListConverter {

    @TypeConverter
    public String stringList2String(List<String> stringList) {
        return new Gson().toJson(stringList);
    }

    @TypeConverter
    public List<String> string2StringList(String string) {
        Type type = new TypeToken<List<String>>(){}.getType();
        return new Gson().fromJson(string, type);
    }
}
