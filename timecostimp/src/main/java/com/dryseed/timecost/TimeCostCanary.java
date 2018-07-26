package com.dryseed.timecost;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.util.Log;

import com.dryseed.timecost.ui.TimeCostInfoListActivity;
import com.dryseed.timecost.utils.DebugLog;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import static android.content.pm.PackageManager.DONT_KILL_APP;

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

    /**
     * Prevent uninitialized calls
     */
    private static boolean sHasInstalled = false;

    /**
     * TimeCostCore process time-cost logic
     */
    private TimeCostCore mTimeCostCore;

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
        setShowDetailUIEnable(applicationContext, TimeCostInfoListActivity.class, true);
        if (sInstance != null) {
            sInstance = new TimeCostCanary();
        }
        return get();
    }

    /**
     * Private TimeCostCanary Constructor
     */
    private TimeCostCanary() {
        TimeCostContext.init(sApplicationContext);
        mTimeCostConfig = new TimeCostConfig.Builder().build();
        mTimeCostCore = TimeCostCore.getInstance();
        mTimeCostCore.clearInterceptor();
        mTimeCostCore.addInterceptor(new NotificationService());
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
        if (timeCostConfig.isShowDetailUI() != mTimeCostConfig.isShowDetailUI()) {
            setShowDetailUIEnable(sApplicationContext, TimeCostInfoListActivity.class, timeCostConfig.isShowDetailUI());
        }
        mTimeCostConfig = timeCostConfig;
        return this;
    }

    /**
     * start monitoring.
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
     * Set StartTime , Called by Asm Code
     *
     * @param methodName
     * @param curTime
     * @param curThreadTime
     */
    public void setStartTime(String methodName, long curTime, long curThreadTime) {
        if (!sHasInstalled || !mIsRunning) {
            DebugLog.d(TAG, String.format("setStartTime return -- sHasInstalled : %b | mIsRunning : %b", sHasInstalled, mIsRunning));
            return;
        }
        mTimeCostCore.setStartTime(methodName, curTime, curThreadTime);
    }

    /**
     * Set EndTime , Called by Asm Code
     *
     * @param methodName
     */
    public void setEndTime(String methodName, long curTime, long curThreadTime, long exceedTime, boolean monitorOnlyMainThread) {
        if (!sHasInstalled || !mIsRunning) {
            DebugLog.d(TAG, String.format("setEndTime return -- sHasInstalled : %b | mIsRunning : %b", sHasInstalled, mIsRunning));
            return;
        }
        mTimeCostCore.setEndTime(methodName, curTime, curThreadTime, exceedTime, monitorOnlyMainThread);
    }

    // these lines are originally copied from LeakCanary: Copyright (C) 2015 Square, Inc.
    private static final Executor fileIoExecutor = newSingleThreadExecutor("File-IO");

    private static void setShowDetailUIEnableInner(Context appContext,
                                                   Class<?> componentClass,
                                                   boolean enabled) {
        ComponentName component = new ComponentName(appContext, componentClass);
        PackageManager packageManager = appContext.getPackageManager();
        int newState = enabled ? COMPONENT_ENABLED_STATE_ENABLED : COMPONENT_ENABLED_STATE_DISABLED;
        // Blocks on IPC.
        packageManager.setComponentEnabledSetting(component, newState, DONT_KILL_APP);
    }

    private static void executeOnFileIoThread(Runnable runnable) {
        fileIoExecutor.execute(runnable);
    }

    private static Executor newSingleThreadExecutor(String threadName) {
        return Executors.newSingleThreadExecutor(new SingleThreadFactory(threadName));
    }

    private static void setShowDetailUIEnable(Context context,
                                              final Class<?> componentClass,
                                              final boolean enabled) {
        final Context appContext = context.getApplicationContext();
        executeOnFileIoThread(new Runnable() {
            @Override
            public void run() {
                setShowDetailUIEnableInner(appContext, componentClass, enabled);
            }
        });
    }
    // end of lines copied from LeakCanary
}
