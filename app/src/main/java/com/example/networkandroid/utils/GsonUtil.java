package com.example.networkandroid.utils;

import com.google.gson.Gson;

public class GsonUtil {
    public static <T> T fromToJson(String json, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }
}
