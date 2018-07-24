package com.dryseed.timecost;

import android.text.TextUtils;

import com.dryseed.timecost.annotations.TimeCost;
import com.dryseed.timecost.entity.TimeCostInfo;
import com.dryseed.timecost.utils.CanaryLogUtils;
import com.dryseed.timecost.utils.DebugLog;
import com.dryseed.timecost.utils.HandlerThreadUtils;
import com.dryseed.timecost.utils.ThreadUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author caiminming
 */
public class TimeCostCore {
    public static final String TAG = "TimeCostCore";

    /**
     * TimeCostCore Instance
     */
    private static TimeCostCore sInstance;

    /**
     * Interceptor Chain for processing exceeded logic
     */
    private List<TimeCostInterceptor> mInterceptorChain = new LinkedList<>();

    /**
     * Save TimeCostInfo during the method invocation
     */
    private ConcurrentHashMap<String, TimeCostInfo> mTimeCostInfoHashMap1 = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, TimeCostInfo> mTimeCostInfoHashMap2 = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, TimeCostInfo> mTimeCostInfoHashMap3 = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, TimeCostInfo> mTimeCostInfoHashMap4 = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, TimeCostInfo> mTimeCostInfoHashMap5 = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, TimeCostInfo> mTimeCostInfoHashMap6 = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, TimeCostInfo> mTimeCostInfoHashMap7 = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, TimeCostInfo> mTimeCostInfoHashMap8 = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, TimeCostInfo>> mHashMaps = new ConcurrentHashMap<>();
    private int mHashMapListSize;

    public TimeCostCore() {
        mHashMaps.put(0, mTimeCostInfoHashMap1);
        mHashMaps.put(1, mTimeCostInfoHashMap2);
        mHashMaps.put(2, mTimeCostInfoHashMap3);
        mHashMaps.put(3, mTimeCostInfoHashMap4);
        mHashMaps.put(4, mTimeCostInfoHashMap5);
        mHashMaps.put(5, mTimeCostInfoHashMap6);
        mHashMaps.put(6, mTimeCostInfoHashMap7);
        mHashMaps.put(7, mTimeCostInfoHashMap8);
        mHashMapListSize = 8;
    }

    /**
     * Get TimeCostCore singleton
     *
     * @return TimeCostCore instance
     */
    static TimeCostCore getInstance() {
        if (sInstance == null) {
            synchronized (TimeCostCore.class) {
                if (sInstance == null) {
                    sInstance = new TimeCostCore();
                }
            }
        }
        return sInstance;
    }

    /**
     * chech the thread whether is valid
     *
     * @param monitorOnlyMainThread determined by TimeCost Annotation
     * @return true if the thread is valid
     */
    private boolean checkThread(boolean monitorOnlyMainThread) {
        // monitorOnlyMainThread is true
        if (monitorOnlyMainThread && ThreadUtils.isMainThread()) {
            return true;
        } else if (monitorOnlyMainThread) {
            return false;
        }

        // monitorOnlyMainThread is false
        if (TimeCostCanary.get().getConfig().isMonitorOnlyMainThread()
                && ThreadUtils.isMainThread()) {
            return true;
        } else if (TimeCostCanary.get().getConfig().isMonitorAllThread()) {
            return true;
        }
        return false;

    }

    /**
     * Set StartTime
     *
     * @param methodName
     * @param curTime
     * @param exceedTime
     * @param threadExceedTime
     * @param monitorOnlyMainThread
     */
    public void setStartTime(final String methodName, final long curTime, final long curThreadTime,
                             final long exceedTime, final long threadExceedTime, final boolean monitorOnlyMainThread) {
        if (TextUtils.isEmpty(methodName)) {
            DebugLog.d(TAG, "methodName is not valid !!!");
            return;
        }

        if (!checkThread(monitorOnlyMainThread)) {
            DebugLog.d(TAG, "thread is not valid !!!");
            return;
        }

        int index = methodName.length() % mHashMapListSize;
        TimeCostInfo timeCostInfo = TimeCostInfo.parse(methodName, curTime, curThreadTime, exceedTime, threadExceedTime);
        DebugLog.d(String.format("=============> 111 [methodName:%s][timeCostInfo:%s][index:%d]", methodName, timeCostInfo.toString(), index));
        mHashMaps.get(index).put(methodName, timeCostInfo);
    }

    /**
     * Set EndTime
     *
     * @param methodName
     * @param time
     */
    public void setEndTime(final String methodName, final long time, final long threadTime) {
        if (TextUtils.isEmpty(methodName)) {
            DebugLog.d(TAG, "methodName is not valid !!!");
            return;
        }

        int index = methodName.length() % mHashMapListSize;
        TimeCostInfo timeCostInfo = mHashMaps.get(index).get(methodName);

        if (null == timeCostInfo) {
            return;
        }
        DebugLog.d(String.format("=============> 222 [methodName:%s][timeCostInfo:%s][index:%d]", methodName, timeCostInfo.toString(), index));
        timeCostInfo.setEndMilliTime(time);
        timeCostInfo.setEndThreadMilliTime(threadTime);
        handleCost(timeCostInfo);
        mHashMaps.get(index).remove(methodName);
    }

    /**
     * Process TimeCost
     *
     * @param timeCostInfo
     */
    private void handleCost(final TimeCostInfo timeCostInfo) {
        if (null == timeCostInfo) {
            return;
        }

        if (timeCostInfo.isExceed()) {
            DebugLog.w(TAG, String.format(
                    "%s has exceed time. [TimeCost:%d][ThreadTimeCost:%d][ExceededTime:%d]",
                    timeCostInfo.getName(),
                    timeCostInfo.getTimeCost(),
                    timeCostInfo.getThreadTimeCost(),
                    timeCostInfo.getExceedMilliTime())
            );

            if (!mInterceptorChain.isEmpty()) {
                for (final TimeCostInterceptor interceptor : mInterceptorChain) {
                    if (null == TimeCostContext.getContext()) {
                        return;
                    }
                    HandlerThreadUtils.getWriteLogThreadHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            CanaryLogUtils.save(timeCostInfo.formatInfo());
                            interceptor.onExceed(TimeCostContext.getContext(), timeCostInfo);
                        }
                    });

                }
            }

        } else {
            DebugLog.d(TAG, String.format(
                    "[TimeCost:%d][ThreadTimeCost:%d][ExceededTime:%d] . %s has not exceed time. ",
                    timeCostInfo.getTimeCost(),
                    timeCostInfo.getThreadTimeCost(),
                    timeCostInfo.getExceedMilliTime(),
                    timeCostInfo.getName())
            );
        }
    }

    /**
     * Add Interceptor
     *
     * @param timeCostInterceptor
     */
    public void addInterceptor(TimeCostInterceptor timeCostInterceptor) {
        if (null != mInterceptorChain) {
            mInterceptorChain.add(timeCostInterceptor);
        }
    }

    /**
     * Clear Interceptor
     */
    public void clearInterceptor() {
        if (null != mInterceptorChain) {
            mInterceptorChain.clear();
        }
    }
}
