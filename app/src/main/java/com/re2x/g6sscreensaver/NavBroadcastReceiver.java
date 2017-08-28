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
            SpHelper.putNavInfo(intent.getIntExtra(GuideInfoExtraKey.LIMITED_SPEED, 0),
                    intent.getBooleanExtra(GuideInfoExtraKey.ARRIVE_STATUS, false),
                    intent.getStringExtra(GuideInfoExtraKey.CUR_ROAD_NAME),
                    intent.getStringExtra(GuideInfoExtraKey.NEXT_ROAD_NAME),
                    intent.getIntExtra(GuideInfoExtraKey.ICON, 0),
                    intent.getIntExtra(GuideInfoExtraKey.ROUTE_REMAIN_DIS, 0),
                    intent.getIntExtra(GuideInfoExtraKey.SEG_REMAIN_DIS, 0));
        }
    }

    public class GuideInfoExtraKey {
        //导航类型，对应的值为int类型
        //：GPS导航
        //1：模拟导航
        public static final String TYPE = "TYPE";

        //当前道路名称，对应的值为String类型
        public static final String CUR_ROAD_NAME = "CUR_ROAD_NAME";

        //下一道路名，对应的值为String类型
        public static final String NEXT_ROAD_NAME = "NEXT_ROAD_NAME";


        //距离最近服务区的距离，对应的值为int类型，单位：米
        public static final String SAPA_DIST = "SAPA_DIST";

        //服务区类型，对应的值为int类型
        //0：高速服务区
        //1：其他服务器

        public static final String SAPA_TYPE = "SAPA_TYPE";

        //距离最近的电子眼距离，对应的值为int类型，单位：米
        public static final String CAMERA_DIST = "CAMERA_DIST";

        //电子眼类型，对应的值为int类型
        //0 测速摄像头
        //1为监控摄像头
        //2为闯红灯拍照
        //3为违章拍照
        //4为公交专用道摄像头
        //5为应急车道摄像头
        public static final String CAMERA_TYPE = "CAMERA_TYPE";

        //电子眼限速度，对应的值为int类型，无限速则为0，单位：公里/小时
        public static final String CAMERA_SPEED = "CAMERA_SPEED";


        //下一个将要路过的电子眼编号，若为-1则对应的道路上没有电子眼，对应的值为int类型
        public static final String CAMERA_INDEX = "CAMERA_INDEX";

        //导航转向图标，对应的值为int类型
        public static final String ICON = "ICON";

        //路径剩余距离，对应的值为int类型，单位：米
        public static final String ROUTE_REMAIN_DIS = "ROUTE_REMAIN_DIS";

        //路径剩余时间，对应的值为int类型，单位：秒
        public static final String ROUTE_REMAIN_TIME = "ROUTE_REMAIN_TIME";

        //当前导航段剩余距离，对应的值为int类型，单位：米
        public static final String SEG_REMAIN_DIS = "SEG_REMAIN_DIS";

        //当前导航段剩余时间，对应的值为int类型，单位：秒
        public static final String SEG_REMAIN_TIME = "SEG_REMAIN_TIME";

        //自车方向，对应的值为int类型，单位：度，以正北为基准，顺时针增加
        public static final String CAR_DIRECTION = "CAR_DIRECTION";

        //当前道路速度限制，对应的值为int类型，单位：公里/小时
        public static final String LIMITED_SPEED = "LIMITED_SPEED";

        //当前自车所在Link，对应的值为int类型，从0开始
        public static final String CUR_SEG_NUM = "CUR_SEG_NUM";

        //当前位置的前一个形状点号，对应的值为int类型，从0开始
        public static final String CUR_POINT_NUM = "CUR_POINT_NUM";

        //环岛出口序号，对应的值为int类型，从0开始.
        //1.x版本：只有在icon为11和12时有效，其余为无效值0
        //2.x版本：只有在icon为11、12、17、18时有效，其余为无效值0
        public static final String ROUND_ABOUT_NUM = "ROUNG_ABOUT_NUM";

        //路径总距离，对应的值为int类型，单位：米
        public static final String ROUTE_ALL_DIS = "ROUTE_ALL_DIS";

        //路径总时间，对应的值为int类型，单位：秒
        public static final String ROUTE_ALL_TIME = "ROUTE_ALL_TIME";

        //当前车速，对应的值为int类型，单位：公里/小时
        public static final String CUR_SPEED = "CUR_SPEED";

        //红绿灯个数，对应的值为int类型
        public static final String TRAFFIC_LIGHT_NUM = "TRAFFIC_LIGHT_NUM";

        //服务区个数，对应的值为int类型
        public static final String SAPA_NUM = "SAPA_NUM";

        //下一个服务区名称，对应的值为String类型
        public static final String SAPA_NAME = "SAPA_NAME";

        //当前道路类型，对应的值为int类型
        //0：高速公路
        //1：国道
        //2：省道
        //3：县道
        //4：乡公路
        //5：县乡村内部道路
        //6：主要大街、城市快速道
        //7：主要道路
        //8：次要道路
        //9：普通道路
        //10：非导航道路
        public static final String ROAD_TYPE = "ROAD_TYPE";

        //是否到达目的地,对应的值为boolean类型
        public static final String ARRIVE_STATUS = "ARRIVE_STATUS";
    }
}