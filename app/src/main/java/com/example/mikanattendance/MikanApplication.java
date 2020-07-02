package com.example.mikanattendance;

import android.app.Application;

import org.xutils.x;

public class MikanApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化XUtils
        x.Ext.init(this);
        x.Ext.setDebug(true);
    }
}
