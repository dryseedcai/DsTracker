package com.dryseed.dstracker;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.dryseed.dstracker.annotations.TimeCost;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimeCostCanary {
    public static final String TAG = "TimeCostCanary";

    private static volatile TimeCostCanary sInstance;
    private static Context sApplicationContext;

    private TimeCostCore mTimeCostCore;
    private boolean mIsRunning = true;

    public static void install(Context applicationContext) {
        sApplicationContext = applicationContext;
    }

    public TimeCostCanary() {
        TimeCostContext.init(sApplicationContext);
        mTimeCostCore = TimeCostCore.getInstance();
        mTimeCostCore.addInterceptor(new NotificationService());
    }

    /**
     * Get {@link TimeCostCanary} singleton.
     *
     * @return {@link TimeCostCanary} instance
     */
    public static TimeCostCanary get() {
        if (sInstance == null) {
            synchronized (TimeCostCanary.class) {
                if (sInstance == null) {
                    sInstance = new TimeCostCanary();
                }
            }
        }
        return sInstance;
    }

    /**
     * Start monitoring.
     */
    public void start() {
        if (!mIsRunning) {
            mIsRunning = true;
        }
    }

    /**
     * Stop monitoring.
     */
    public void stop() {
        if (mIsRunning) {
            mIsRunning = false;
        }
    }

    /**
     * Set StartTime
     *
     * @param methodName
     * @param curTime
     */
    public void setStartTime(String methodName, long curTime) {
        Log.d(TAG, String.format("setStartTime : %s", methodName));
        if (!mIsRunning) {
            return;
        }
        mTimeCostCore.setStartTime(methodName, curTime);
    }

    /**
     * Set StartTime
     *
     * @param methodName
     * @param curTime
     * @param exceededTime
     */
    public void setStartTime(String methodName, long curTime, long exceededTime) {
        Log.d(TAG, String.format("setStartTime2 : %s", methodName));
        if (!mIsRunning) {
            return;
        }
        mTimeCostCore.setStartTime(methodName, curTime, exceededTime);
    }

    /**
     * Set EndTime
     *
     * @param methodName
     * @param time
     */
    public void setEndTime(String methodName, long time) {
        Log.d(TAG, String.format("setEndTime : %s", methodName));
        if (!mIsRunning) {
            return;
        }
        mTimeCostCore.setEndTime(methodName, time);
    }
}
