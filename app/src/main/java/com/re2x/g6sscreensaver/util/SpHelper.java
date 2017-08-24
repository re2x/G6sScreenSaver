package com.re2x.g6sscreensaver.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by linzq on 2017/8/21.
 */

public class SpHelper {
    public static SharedPreferences preferences;

    public static final String KEY_IS_AUTOSTART = "IS_AUTOSTART";
    public static final String KEY_NAV_LIMITED_SPEED = "NAV_LIMITED_SPEED";
    public static final String KEY_NAV_ARRIVE_STATUS = "NAV_ARRIVE_STATUS";
    public static final String KEY_NAV_CUR_ROAD_NAME = "NAV_CUR_ROAD_NAME";
    public static final String KEY_NAV_ROUTE_REMAIN_DIS = "NAV_ROUTE_REMAIN_DIS";
    public static final String KEY_NAV_SEG_REMAIN_DIS = "NAV_SEG_REMAIN_DIS";
    public static final String KEY_NAV_NEXT_ROAD_NAME = "NAV_NEXT_ROAD_NAME";
    public static final String KEY_NAV_ICONID = "NAV_ICONID";
    public static final String KEY_MUSIC_TYPENAME = "MUSIC_TYPENAME";
    public static final String KEY_MUSIC_TITLE = "MUSIC_TITLE";
    public static final String KEY_MUSIC_ALBUM = "MUSIC_ALBUM";
    public static final String KEY_MUSIC_ARTIST = "MUSIC_ARTIST";


    public static void init(Context context) {
        preferences = context.getSharedPreferences("com.re2x.g6sscreensaver", 0);
    }

    public static void putMusicInfo(String typeName,
                                    String title,
                                    String album,
                                    String artist) {
        SharedPreferences.Editor localEditor = preferences.edit();
        localEditor.putString(KEY_MUSIC_TYPENAME, typeName);
        localEditor.putString(KEY_MUSIC_TITLE, title);
        localEditor.putString(KEY_MUSIC_ALBUM, album);
        localEditor.putString(KEY_MUSIC_ARTIST, artist);
        localEditor.commit();
    }

    public static void putNavInfo(int limitSpeed,
                                  boolean arriveStatus,
                                  String curRoadName,
                                  String nextRoadName,
                                  int iconid,
                                  int routeRemainDis,
                                  int segRemainDis) {
        SharedPreferences.Editor localEditor = preferences.edit();
        localEditor.putInt(KEY_NAV_LIMITED_SPEED, limitSpeed);
        localEditor.putBoolean(KEY_NAV_ARRIVE_STATUS, arriveStatus);
        //if (!TextUtils.isEmpty(curRoadName)) {
            localEditor.putString(KEY_NAV_CUR_ROAD_NAME, curRoadName);
        //}
        //if (iconid != 0) {
            localEditor.putInt(KEY_NAV_ICONID, iconid);
        //}
        //if (routeRemainDis != 0) {
            localEditor.putInt(KEY_NAV_ROUTE_REMAIN_DIS, routeRemainDis);
        //}
        //if (segRemainDis != 0) {
            localEditor.putInt(KEY_NAV_SEG_REMAIN_DIS, segRemainDis);
        //}
        //if (!TextUtils.isEmpty(nextRoadName)) {
            localEditor.putString(KEY_NAV_NEXT_ROAD_NAME, nextRoadName);
        //}
        localEditor.commit();
    }

    public static void putInt(String key, int value) {
        SharedPreferences.Editor localEditor = preferences.edit();
        localEditor.putInt(key, value);
        localEditor.commit();
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor localEditor = preferences.edit();
        localEditor.putString(key, value);
        localEditor.commit();
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor localEditor = preferences.edit();
        localEditor.putBoolean(key, value);
        localEditor.commit();
    }

    public static String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

}
