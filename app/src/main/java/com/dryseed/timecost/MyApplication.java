package com.dryseed.timecost;

import android.app.Application;

/**
 * @author caiminming
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        TimeCostCanary.install(this).config(
                new TimeCostConfig.Builder()
                        .setMilliExceedTime(200L)
                        //.setMonitorOnlyMainThread(true)
                        .build()
        );

    }
}
