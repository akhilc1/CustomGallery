package com.example.user.customgalleryfinal.Activities;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by ${Chandran} on 9/6/15.
 */
public class ProjectApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
