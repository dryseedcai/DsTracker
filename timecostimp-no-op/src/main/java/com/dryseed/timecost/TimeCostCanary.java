package com.dryseed.timecost;

import android.content.Context;

/**
 * @author caiminming
 */
public class TimeCostCanary {
    public static final String TAG = "TimeCostCanary";

    /**
     * TimeCostCanary Instance
     */
    private static volatile TimeCostCanary sInstance;

    /**
     * TimeCostConfig Instance
     */
    private TimeCostConfig mTimeCostConfig;

    /**
     * Application Context
     */
    private static Context sApplicationContext;

    private static boolean sHasInstalled = false;

    /**
     * Flag whether TimeCostCanary is running
     */
    private boolean mIsRunning = true;


    /**
     * Init TimeCostCanary
     *
     * @param applicationContext
     * @return
     */
    public static TimeCostCanary install(Context applicationContext) {
        sHasInstalled = true;
        sApplicationContext = applicationContext;
        if (sInstance != null) {
            sInstance = new TimeCostCanary();
        }
        return get();
    }

    /**
     * Private TimeCostCanary Constructor
     */
    private TimeCostCanary() {
    }

    /**
     * Get {@link TimeCostCanary} singleton.
     * Do not modify the method name unless you are compatible with asm code
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
     * Get TimeCostConfig
     *
     * @return
     */
    public TimeCostConfig getConfig() {
        return mTimeCostConfig;
    }

    /**
     * Set TimeCostConfig
     *
     * @param timeCostConfig
     * @return
     */
    public TimeCostCanary config(TimeCostConfig timeCostConfig) {
        mTimeCostConfig = timeCostConfig;
        return this;
    }

    /**
     * start monitoring.
     */
    public void start() {
    }

    /**
     * Stop monitoring.
     */
    public void stop() {
    }

    /**
     * Set StartTime , Called by Asm Code
     *
     * @param methodName
     * @param curTime
     * @param curThreadTime
     */
    public void setStartTime(String methodName, long curTime, long curThreadTime) {
    }

    /**
     * Set EndTime , Called by Asm Code
     *
     * @param methodName
     * @param curTime
     * @param curThreadTime
     * @param exceedTime
     * @param monitorOnlyMainThread
     */
    public void setEndTime(String methodName, long curTime, long curThreadTime, long exceedTime, boolean monitorOnlyMainThread) {
    }
}
