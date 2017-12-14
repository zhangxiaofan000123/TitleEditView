package com.zhang.editview.spf;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zhang on 2016/10/10.
 * SharedPreferences的帮助类
 */

public class SpfHelp {

    private static final String TAG = "SpfHelp";




    /**
     * 获取字符串类型的SharedPreference
     *
     * @param fileName 文件名
     * @param key      存储的建
     * @return
     */
    public static String getStringFormSpf(Context context,String fileName, String key) {
        SharedPreferences spf = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return spf.getString(key, "");
    }

    public static int getIntFormSpf(Context context,String fileName, String key) {
        SharedPreferences spf = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return spf.getInt(key, 0);
    }

    public static boolean getBooleanFormSpf(Context context,String fileName, String key) {
        SharedPreferences spf = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return spf.getBoolean(key, false);
    }

    /**
     * 将字符串存到SharedPreference中
     *
     * @param fileName 文件名
     * @param key      键
     * @param value    值
     */
    public static void putString2Spf(Context context,String fileName, String key, String value) {
        SharedPreferences spf = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        spf.edit().putString(key, value).apply();
    }

    public static void putInt2Spf(Context context,String fileName, String key, int value) {
        SharedPreferences spf = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        spf.edit().putInt(key, value).apply();
    }

    public static void putBoolean2Spf(Context context,String fileName, String key, boolean value) {
        SharedPreferences spf = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        spf.edit().putBoolean(key, value).apply();
    }


}
