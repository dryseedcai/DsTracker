package com.dryseed.timecost;

import com.dryseed.timecost.entity.TimeCostInfo;
import com.dryseed.timecost.utils.CanaryLogUtils;
import com.dryseed.timecost.utils.DebugLog;
import com.dryseed.timecost.utils.HandlerThreadUtils;
import com.dryseed.timecost.utils.ThreadUtils;

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
     * @param exceededTime
     */
    public void setStartTime(String methodName, long curTime, long curThreadTime, long exceededTime, boolean monitorOnlyMainThread) {
        if (!checkThread(monitorOnlyMainThread)) {
            DebugLog.d(TAG, "thread is not valid !!!");
            return;
        }
        mTimeCostInfoHashMap.put(methodName, TimeCostInfo.parse(methodName, curTime, curThreadTime, exceededTime));
    }

    /**
     * Set EndTime
     *
     * @param methodName
     * @param time
     */
    public void setEndTime(String methodName, long time, long threadTime) {
        TimeCostInfo timeCostInfo = mTimeCostInfoHashMap.get(methodName);
        if (null == timeCostInfo) {
            return;
        }
        timeCostInfo.setEndMilliTime(time);
        timeCostInfo.setEndThreadMilliTime(threadTime);
        handleCost(timeCostInfo);
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
                    "%s has exceed time. TimeCost : %d | ExceededTime : %d",
                    timeCostInfo.getName(),
                    timeCostInfo.getTimeCost(),
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
