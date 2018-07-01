package com.dryseed.dstracker;

import android.app.Application;

/**
 * @author caiminming
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TimeCostCanary.install(this);
    }
}
