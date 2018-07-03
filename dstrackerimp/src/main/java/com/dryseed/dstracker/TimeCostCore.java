package com.dryseed.dstracker;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

        if (timeCostInfo.isExceed()) {
            Log.w(TAG, String.format(
                    "%s has exceed time. TimeCost : %d | ExceededTime : %d",
                    timeCostInfo.getName(),
                    timeCostInfo.getTimeCost(),
                    timeCostInfo.getExceedMilliTime())
            );

            if (!mInterceptorChain.isEmpty()) {
                for (TimeCostInterceptor interceptor : mInterceptorChain) {
                    interceptor.onExceed(TimeCostContext.getContext(), timeCostInfo);
                }
            }

        } else {
            Log.d(TAG, String.format(
                    "%s has not exceed time. TimeCost : %d | ExceededTime : %d",
                    timeCostInfo.getName(),
                    timeCostInfo.getTimeCost(),
                    timeCostInfo.getExceedMilliTime())
            );
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
