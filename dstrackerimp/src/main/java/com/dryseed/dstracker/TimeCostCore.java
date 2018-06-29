package com.dryseed.dstracker;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TimeCostCore {
    public static final String TAG = "TimeCostCore";

    private static TimeCostCore sInstance;

    private List<TimeCostInterceptor> mInterceptorChain = new LinkedList<>();
    public ConcurrentHashMap<String, TimeCostInfo> mTimeCostInfoHashMap = new ConcurrentHashMap<>();


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
     * Set StartTime
     *
     * @param methodName
     * @param curTime
     */
    public void setStartTime(String methodName, long curTime) {
        mTimeCostInfoHashMap.put(methodName, TimeCostInfo.parse(methodName, curTime));
    }

    /**
     * Set StartTime
     *
     * @param methodName
     * @param curTime
     * @param exceededTime
     */
    public void setStartTime(String methodName, long curTime, long exceededTime) {
        mTimeCostInfoHashMap.put(methodName, TimeCostInfo.parse(methodName, curTime, exceededTime));
    }

    /**
     * Set EndTime
     *
     * @param methodName
     * @param time
     */
    public void setEndTime(String methodName, long time) {
        TimeCostInfo timeCostInfo = mTimeCostInfoHashMap.get(methodName);
        if (null == timeCostInfo) {
            return;
        }
        timeCostInfo.setEndMilliTime(time);
        handleCost(timeCostInfo);
    }

    /**
     * Process TimeCost
     *
     * @param timeCostInfo
     */
    private void handleCost(TimeCostInfo timeCostInfo) {
        if (null == timeCostInfo) {
            return;
        }

        Log.d(TAG, "handleCost " + timeCostInfo.toString());

        long cost = timeCostInfo.getEndMilliTime() - timeCostInfo.getStartMilliTime();

        if (cost > timeCostInfo.getExceedMilliTime()) {
            Log.w(TAG, String.format("%s has exceed time. TimeCost : %d | ExceededTime : %d", timeCostInfo.getName(), cost, timeCostInfo.getExceedMilliTime()));
        } else {
            Log.d(TAG, String.format("%s has not exceed time. TimeCost : %d | ExceededTime : %d", timeCostInfo.getName(), cost, timeCostInfo.getExceedMilliTime()));
        }

        if (!mInterceptorChain.isEmpty()) {
            for (TimeCostInterceptor interceptor : mInterceptorChain) {
                interceptor.onExceed(TimeCostContext.getContext(), timeCostInfo);
            }
        }
    }

    /**
     * Add Interceptor
     *
     * @param timeCostInterceptor
     */
    public void addInterceptor(TimeCostInterceptor timeCostInterceptor) {
        mInterceptorChain.add(timeCostInterceptor);
    }
}