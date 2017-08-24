package com.re2x.g6sscreensaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.re2x.g6sscreensaver.util.Log;
import com.re2x.g6sscreensaver.util.SpHelper;

public class NavBroadcastReceiver
        extends BroadcastReceiver {
    public static final String TAG = "com.re2x.g6sscreensaver.BroadCastReciver";
    public static final String AUTO_NAV = "AUTONAVI_STANDARD_BROADCAST_SEND";
    public static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    public static final String KUGOU_MUSIC = "com.kugou.android.music.metachanged";
    public static final String MEDIA_EJECT = "android.intent.action.MEDIA_EJECT";
    public static final String MEDIA_MOUNTED = "android.intent.action.MEDIA_MOUNTED";
    public static final String QQ_DETAILS = "com.android.music.metachanged";
    public static final String SCREEN_ON = "android.intent.action.SCREEN_ON";
    public static final String TRACK_DETAILS = "cn.flyaudio.media.action.TRACK_DETAILS";
    private static Context nContext;

    public void onReceive(Context context, Intent intent) {
        nContext = context;
        if (intent.getAction().equals(MEDIA_EJECT)) {
            Log.d(TAG, "MEDIA_EJECT");
        }
        if (intent.getAction().equals(MEDIA_MOUNTED)) {
            Log.d(TAG, "MEDIA_MOUNTED");
        }
        if (intent.getAction().equals(SCREEN_ON) || intent.getAction().equals(BOOT_COMPLETED)) {
            boolean isAutoStart = SpHelper.getBoolean(SpHelper.KEY_IS_AUTOSTART, false);
            if (isAutoStart) {
                Intent mainActivityIntent = new Intent(context, MainActivity.class);
                context.startActivity(mainActivityIntent);
            }
        }
        if (intent.getAction().equals(TRACK_DETAILS)) {
            Log.d(TAG, "TRACK_DETAILS");
            Log.d(TAG, "kugou metachanged");
            SpHelper.putMusicInfo("飞歌音乐播放器",
                    intent.getStringExtra("title"),
                    intent.getStringExtra("album"),
                    intent.getStringExtra("artist"));
        }
        if (intent.getAction().endsWith(KUGOU_MUSIC)) {
            Log.d(TAG, "kugou metachanged");
            SpHelper.putMusicInfo("酷狗音乐",
                    intent.getStringExtra("title"),
                    intent.getStringExtra("album"),
                    intent.getStringExtra("artist"));
        }
        if (intent.getAction().equals(QQ_DETAILS)) {
            Log.d(TAG, "第三方音乐播放器");
            SpHelper.putMusicInfo("音乐播放器",
                    intent.getStringExtra("track"),
                    intent.getStringExtra("album"),
                    intent.getStringExtra("artist"));
        }
        if (intent.getAction().equals(AUTO_NAV)) {
            Log.d(TAG, "高德广播");
            //Toast.makeText(context, "高德广播", Toast.LENGTH_SHORT).show();
            SpHelper.putNavInfo(intent.getIntExtra("LIMITED_SPEED", 0),
                    intent.getBooleanExtra("ARRIVE_STATUS", true),
                    intent.getStringExtra("CUR_ROAD_NAME"),
                    intent.getStringExtra("NEXT_ROAD_NAME"),
                    intent.getIntExtra("ICON", 0),
                    intent.getIntExtra("ROUTE_REMAIN_DIS", 0),
                    intent.getIntExtra("SEG_REMAIN_DIS", 0));
        }
    }
}