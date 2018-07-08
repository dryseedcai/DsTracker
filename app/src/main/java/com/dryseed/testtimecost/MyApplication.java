package com.dryseed.testtimecost;

import android.app.Application;

import com.dryseed.timecost.TimeCostCanary;
import com.dryseed.timecost.TimeCostConfig;

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
                        .setMonitorOnlyMainThread(true)
                        .setSortType(TimeCostConfig.CONFIG_SORT_TYPE_START_TIME)
                        .setShowDetailUI(true)
                        .build()
        );

    }
}
