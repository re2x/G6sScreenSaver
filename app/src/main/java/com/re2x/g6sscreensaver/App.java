package com.re2x.g6sscreensaver;

import android.app.Application;

import com.re2x.g6sscreensaver.util.SpHelper;

/**
 * Created by linzq on 2017/8/21.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SpHelper.init(this);
    }
}
