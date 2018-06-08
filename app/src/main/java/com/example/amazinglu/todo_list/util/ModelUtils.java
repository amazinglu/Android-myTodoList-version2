package com.example.amazinglu.todo_list.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ModelUtils {
    private static Gson gsonForSerialization = new Gson();
    private static Gson gsonForDeserialization = new Gson();

    private static String PREF_NAME = "models";

    public static void save(Context context, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String josnString = gsonForSerialization.toJson(object);
        sp.edit().putString(key, josnString).apply();
    }

    public static <T> T read(Context context, String key, TypeToken<T> typeToken) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return gsonForDeserialization.fromJson(sp.getString(key, ""), typeToken.getType());
    }
}
