package com.re2x.g6sscreensaver;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.re2x.g6sscreensaver.util.Log;
import com.re2x.g6sscreensaver.util.SpHelper;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MainActivity extends Activity {

    public static final String TAG = "com.re2x.g6sscreensaver.MainActivity";
    NavBroadcastReceiver navBroadcastReceiver;
    long begin = -20L;
    int speedid = 0;
    RelativeLayout rlWallpaper;
    RelativeLayout rlBackground;
    TextView tvSpeed;
    TextView tvSpeedLimit;
    TextView tvTitleDate;
    TextView tvTitleRoadName;
    TextView tvNextRoadName;
    TextView tvSegRemainDis;
    TextView tvRouteRemainDis;
    ImageView ivSpeed;
    ImageView ivNavIco;
    ImageView ivAutoNav;
    TextView tvSongName;
    TextView tvSongArtist;
    TextView tvSongAlbum;
    ImageView ivMediaPlay;
    ImageView ivMediaPrev;
    ImageView ivMediaNext;
    TextView tvLargeTime;
    TextView tvLargeDate;


    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            super.handleMessage(message);
            updateDate();

            if (Build.BRAND.startsWith("generic")) {
                // 测试
                Random rnd = new Random();
                boolean isArrival = (int) (0 + rnd.nextDouble() * 4) > 2 ? true : false;
                MainActivity.this.speedid = (int) (0 + rnd.nextDouble() * 200);
                SpHelper.putNavInfo((int) (40 + rnd.nextDouble() * 120), isArrival, "当前路名", "下一条路名", (int) (1 + rnd.nextDouble() * 20), (int) (5 + rnd.nextDouble() * 15000), (int) (5 + rnd.nextDouble() * 5000));
                SpHelper.putMusicInfo("飞歌音乐", "歌曲名称" + (int) (1 + rnd.nextDouble() * 100), "专辑" + (int) (1 + rnd.nextDouble() * 10), "歌手" + (int) (1 + rnd.nextDouble() * 10));
            }

            // 速度
            if (MainActivity.this.speedid > 0) {
                MainActivity.this.startSpeedAnimation(MainActivity.this.speedid);
                MainActivity.this.tvSpeed.setText(String.valueOf(speedid));
            } else {
                MainActivity.this.startSpeedAnimation(0);
                MainActivity.this.tvSpeed.setText("");
            }

            //媒体
            String songName = SpHelper.getString(SpHelper.KEY_MUSIC_TITLE, "");
            if (!TextUtils.isEmpty(songName)) {
                MainActivity.this.tvSongName.setText(songName);
            }
            String songArtist = SpHelper.getString(SpHelper.KEY_MUSIC_ARTIST, "");
            if (!TextUtils.isEmpty(songArtist)) {
                MainActivity.this.tvSongArtist.setText(songArtist);
            }
            String songAlbum = SpHelper.getString(SpHelper.KEY_MUSIC_ALBUM, "");
            if (!TextUtils.isEmpty(songAlbum)) {
                MainActivity.this.tvSongAlbum.setText(songAlbum);
            }

            // 导航信息
            boolean isArrival = SpHelper.getBoolean(SpHelper.KEY_NAV_ARRIVE_STATUS, false);
            if (isArrival) {
                MainActivity.this.tvTitleDate.setVisibility(View.GONE);
                MainActivity.this.tvLargeDate.setVisibility(View.VISIBLE);
                MainActivity.this.tvLargeTime.setVisibility(View.VISIBLE);
                MainActivity.this.setIconView(0);
                MainActivity.this.tvTitleRoadName.setText("");
                MainActivity.this.tvSegRemainDis.setText("");
                MainActivity.this.tvNextRoadName.setText("");
                MainActivity.this.tvRouteRemainDis.setText("");
                MainActivity.this.tvSpeedLimit.setText("");
            } else {
                MainActivity.this.tvTitleDate.setVisibility(View.VISIBLE);
                MainActivity.this.tvLargeDate.setVisibility(View.GONE);
                MainActivity.this.tvLargeTime.setVisibility(View.GONE);
                String curRoadName = SpHelper.getString(SpHelper.KEY_NAV_CUR_ROAD_NAME, "");
                if (!TextUtils.isEmpty(curRoadName)) {
                    MainActivity.this.tvTitleRoadName.setText("当前：" + curRoadName);
                }
                DecimalFormat decimalFormat = new DecimalFormat("0.0");
                int segRemainDis = SpHelper.getInt(SpHelper.KEY_NAV_SEG_REMAIN_DIS, 0);
                if (segRemainDis > 0) {
                    String segRemainDisValue = segRemainDis + " 米";
                    if (segRemainDis > 1000) {
                        segRemainDisValue = decimalFormat.format(Double.valueOf((double) segRemainDis / 1000.0D)) + " 公里后";
                    }
                    MainActivity.this.tvSegRemainDis.setText(segRemainDisValue);
                    MainActivity.this.tvNextRoadName.setText("转入 " + SpHelper.getString(SpHelper.KEY_NAV_NEXT_ROAD_NAME, ""));
                }
                int routeRemainDis = SpHelper.getInt(SpHelper.KEY_NAV_ROUTE_REMAIN_DIS, 0);
                if (routeRemainDis > 0) {
                    String routeRemainDisValue = routeRemainDis + " 米";
                    if (routeRemainDis > 1000) {
                        routeRemainDisValue = decimalFormat.format(Double.valueOf((double) routeRemainDis / 1000.0D)) + " 公里";
                    }
                    MainActivity.this.tvRouteRemainDis.setText(routeRemainDisValue + " 后到达");
                }
                int limitSpeed = SpHelper.getInt(SpHelper.KEY_NAV_LIMITED_SPEED, 0);
                if (limitSpeed > 0) {
                    tvSpeedLimit.setText(String.valueOf(limitSpeed));
                } else {
                    tvSpeedLimit.setText("");
                }
                MainActivity.this.setIconView(SpHelper.getInt(SpHelper.KEY_NAV_ICONID, 0));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initData();
        (new TimeThread()).start();

        // 广播相关
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NavBroadcastReceiver.QQ_DETAILS);
        intentFilter.addAction(NavBroadcastReceiver.KUGOU_MUSIC);
        intentFilter.addAction(NavBroadcastReceiver.AUTO_NAV);
        intentFilter.addAction(NavBroadcastReceiver.SCREEN_ON);
        navBroadcastReceiver = new NavBroadcastReceiver();
        registerReceiver(navBroadcastReceiver, intentFilter);
        Intent intent = new Intent();
        intent.setAction("AUTONAVI_STANDARD_BROADCAST_SEND");
        intent.putExtra("KEY_TYPE", 10001);
        sendBroadcast(intent);

        // 开启GPS
        openGPSSettings();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.d(TAG, "GPS_PROVIDER:" + "gps");
        updateLocation(locationManager.getLastKnownLocation("gps"));
        locationManager.requestLocationUpdates("gps", 1000L, 1.0F, new LocationListener() {
            public void onLocationChanged(Location location) {
                MainActivity.this.updateLocation(location);
                Log.d(MainActivity.TAG, "GPS位置变化:" + location);
            }

            public void onProviderDisabled(String paramAnonymousString) {
            }

            public void onProviderEnabled(String paramAnonymousString) {
            }

            public void onStatusChanged(String paramAnonymousString, int paramAnonymousInt, Bundle paramAnonymousBundle) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (navBroadcastReceiver != null) {
            unregisterReceiver(navBroadcastReceiver);
        }
        initData();
    }

    private void initViews() {
        rlWallpaper = (RelativeLayout) findViewById(R.id.rlWallpaper);
        rlBackground = (RelativeLayout) findViewById(R.id.rlBackground);
        updateWallpaper();
        tvSpeed = (TextView) findViewById(R.id.tvSpeed);
        tvSpeed.setText("");
        tvSpeedLimit = (TextView) findViewById(R.id.tvSpeedLimit);
        tvSpeedLimit.setText("");
        tvTitleRoadName = (TextView) findViewById(R.id.tvTitleRoadName);
        tvTitleRoadName.setText("");
        tvNextRoadName = (TextView) findViewById(R.id.tvNextRoadName);
        tvNextRoadName.setText("");
        tvSegRemainDis = (TextView) findViewById(R.id.tvSegRemainDis);
        tvSegRemainDis.setText("");
        tvRouteRemainDis = (TextView) findViewById(R.id.tvRouteRemainDis);
        tvRouteRemainDis.setText("");
        tvTitleDate = (TextView) findViewById(R.id.tvTitleDate);
        tvLargeTime = (TextView) findViewById(R.id.tvLargeTime);
        tvLargeDate = (TextView) findViewById(R.id.tvLargeDate);
        updateDate();
        ivSpeed = (ImageView) findViewById(R.id.ivSpeed);
        ivNavIco = (ImageView) findViewById(R.id.ivNavIco);
        ivAutoNav = (ImageView) findViewById(R.id.ivAutoNav);
        ivAutoNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClassName("com.autonavi.amapauto", "com.autonavi.auto.remote.fill.UsbFillActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    MainActivity.this.startActivity(intent);
                } catch (Exception ex) {

                }
            }
        });
        ivAutoNav.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                boolean isAutoStart = SpHelper.getBoolean(SpHelper.KEY_IS_AUTOSTART, false);
                isAutoStart = !isAutoStart;
                SpHelper.putBoolean(SpHelper.KEY_IS_AUTOSTART, isAutoStart);
                Toast.makeText(MainActivity.this, "设置自动启动：" + isAutoStart, Toast.LENGTH_LONG).show();
                return true;
            }
        });

        // 媒体
        tvSongName = (TextView) findViewById(R.id.tvSongName);
        tvSongArtist = (TextView) findViewById(R.id.tvSongArtist);
        tvSongAlbum = (TextView) findViewById(R.id.tvSongAlbum);
        View.OnClickListener mediaOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int id = view.getId();
                new Thread() {
                    public void run() {
                        try {
                            Instrumentation localInstrumentation = new Instrumentation();
                            switch (id) {
                                case R.id.ivMediaPlay:
                                    localInstrumentation.sendKeyDownUpSync(85);
                                    break;
                                case R.id.ivMediaPrev:
                                    localInstrumentation.sendKeyDownUpSync(88);
                                    break;
                                case R.id.ivMediaNext:
                                    localInstrumentation.sendKeyDownUpSync(87);
                                    break;
                            }

                        } catch (Exception ex) {

                        }
                    }
                }.start();
            }
        };
        ivMediaPlay = (ImageView) findViewById(R.id.ivMediaPlay);
        ivMediaPlay.setOnClickListener(mediaOnClickListener);
        ivMediaPrev = (ImageView) findViewById(R.id.ivMediaPrev);
        ivMediaPrev.setOnClickListener(mediaOnClickListener);
        ivMediaNext = (ImageView) findViewById(R.id.ivMediaNext);
        ivMediaNext.setOnClickListener(mediaOnClickListener);
    }

    private void initData() {
        SpHelper.putNavInfo(0, true, "", "", 0, 0, 0);
        SpHelper.putMusicInfo("", "", "", "");
    }

    private String updateLocation(Location location) {
        if (location != null) {
            //if (location.getSpeed() * 3.6D < 10.0D) {
            this.speedid = (int) (location.getSpeed() * 3.6D);
            //}
        }
        return "";
    }

    private void updateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 " + getWeek() + " HH:mm");
        tvTitleDate.setText(sdf.format(new Date()));
        sdf = new SimpleDateFormat("yyyy年MM月dd日 " + getWeek());
        tvLargeDate.setText(sdf.format(new Date()));
        sdf = new SimpleDateFormat("HH:mm");
        tvLargeTime.setText(sdf.format(new Date()));
    }

    private void openGPSSettings() {
        if (((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled("gps")) {
            Toast.makeText(this, "GPS模块正常", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, "请打开GPS", Toast.LENGTH_LONG).show();
        startActivityForResult(new Intent("android.settings.SECURITY_SETTINGS"), 0);
    }

    private void startSpeedAnimation(int paramInt) {
        AnimationSet localAnimationSet = new AnimationSet(true);
        paramInt -= 20;
        RotateAnimation localRotateAnimation = new RotateAnimation(this.begin, paramInt, Animation.RELATIVE_TO_SELF, 1F, Animation.RELATIVE_TO_SELF, 0F);
        localRotateAnimation.setDuration(1000L);
        localAnimationSet.addAnimation(localRotateAnimation);
        this.ivSpeed.startAnimation(localAnimationSet);
        this.begin = paramInt;
    }

    private void updateWallpaper() {
        WallpaperManager wallpaperManager = WallpaperManager
                .getInstance(this);
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        if (wallpaperDrawable != null) {
            rlWallpaper.setBackground(wallpaperDrawable);
            rlBackground.getBackground().setAlpha(235);
        }
    }

    public static String getWeek() {
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            default:
                return "";
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
        }
    }

    public void setIconView(int navIcoIndex) {
        switch (navIcoIndex) {
            default:
                this.ivNavIco.setImageResource(R.drawable.guidepost);
                return;
            case 1:
                this.ivNavIco.setImageResource(R.drawable.sou1);
                return;
            case 2:
                this.ivNavIco.setImageResource(R.drawable.sou2);
                return;
            case 3:
                this.ivNavIco.setImageResource(R.drawable.sou3);
                return;
            case 4:
                this.ivNavIco.setImageResource(R.drawable.sou4);
                return;
            case 5:
                this.ivNavIco.setImageResource(R.drawable.sou5);
                return;
            case 6:
                this.ivNavIco.setImageResource(R.drawable.sou6);
                return;
            case 7:
                this.ivNavIco.setImageResource(R.drawable.sou7);
                return;
            case 8:
                this.ivNavIco.setImageResource(R.drawable.sou8);
                return;
            case 9:
                this.ivNavIco.setImageResource(R.drawable.sou9);
                return;
            case 10:
                this.ivNavIco.setImageResource(R.drawable.sou10);
                return;
            case 11:
                this.ivNavIco.setImageResource(R.drawable.sou11);
                return;
            case 12:
                this.ivNavIco.setImageResource(R.drawable.sou12);
                return;
            case 13:
                this.ivNavIco.setImageResource(R.drawable.sou13);
                return;
            case 14:
                this.ivNavIco.setImageResource(R.drawable.sou14);
                return;
            case 15:
                this.ivNavIco.setImageResource(R.drawable.sou15);
                return;
            case 16:
                this.ivNavIco.setImageResource(R.drawable.sou16);
                return;
            case 17:
                this.ivNavIco.setImageResource(R.drawable.sou17);
                return;
            case 18:
                this.ivNavIco.setImageResource(R.drawable.sou18);
                return;
            case 19:
                this.ivNavIco.setImageResource(R.drawable.sou19);
                return;
            case 20:
                this.ivNavIco.setImageResource(R.drawable.sou20);
                return;
        }
    }

    class TimeThread
            extends Thread {
        TimeThread() {
        }

        public void run() {
            try {
                for (; ; ) {
                    Thread.sleep(1000L);
                    Message localMessage = new Message();
                    localMessage.what = 1;
                    MainActivity.this.mHandler.sendMessage(localMessage);
                }
            } catch (InterruptedException localInterruptedException) {
                localInterruptedException.printStackTrace();
            }
        }
    }
}
