package com.yuanyang.xiaohu.door.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class GsonProvider {

    private Gson gson;

    private static GsonProvider instance;

    private GsonProvider() {
        gson = new Gson();
    }

    public static GsonProvider getInstance() {
        if (instance == null) {
            synchronized (GsonProvider.class) {
                if (instance == null) {
                    instance = new GsonProvider();
                }
            }
        }
        return instance;
    }

    public Gson getGson() {
        return gson;
    }

    /**
     * 把json 字符串转化成list
     * // List<bean类名> list = GsonUtil.stringToList(result, bean类名.class);
     *
     * @param json
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> List<T> stringToList(String json, Class<T> cls) {
        Gson gson = new Gson();
        List<T> list = new ArrayList<>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(gson.fromJson(elem, cls));
        }
        return list;
    }

}
