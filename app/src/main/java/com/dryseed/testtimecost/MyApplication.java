package com.dryseed.testtimecost;

import android.app.Application;

import com.dryseed.timecost.TimeCostCanary;
import com.dryseed.timecost.TimeCostConfig;
import com.dryseed.timecost.constants.TimeCostConstant;

/**
 * @author caiminming
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TimeCostCanary.install(this).config(
                new TimeCostConfig.Builder()
                        .setExceedMilliTime(500L)
                        //.setExceedMaxMilliTIme(2000L)
                        .setThreadExceedMilliTime(500L)
                        .setMonitorOnlyMainThread(true)
                        .setSortType(TimeCostConstant.CONFIG_SORT_TYPE_START_TIME)
                        .setShowDetailUI(true)
                        .setShowNotifaction(true)
                        .setDelayStartMilliTime(1000L)
                        .build()
        );

    }
}
