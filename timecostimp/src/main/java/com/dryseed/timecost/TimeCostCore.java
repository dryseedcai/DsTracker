package com.dryseed.timecost;

import android.os.SystemClock;
import android.text.TextUtils;

import com.dryseed.timecost.entity.TimeCostInfo;
import com.dryseed.timecost.utils.CanaryLogUtils;
import com.dryseed.timecost.utils.DebugLog;
import com.dryseed.timecost.utils.HandlerThreadUtils;
import com.dryseed.timecost.utils.ThreadUtils;

import java.util.LinkedList;
import java.util.List;

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

    //private List<TimeCostInfo> mExceedInfoList = new ArrayList<>();

    //private long mLastNotifyTime = 0;

    //private Handler mHandler = new Handler();

    public TimeCostCore() {
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
     * @param curThreadTime
     */
    public void setStartTime(final String methodName, long curTime, long curThreadTime) {
        if (TextUtils.isEmpty(methodName)) {
            //DebugLog.d(TAG, "methodName is not valid !!!");
            return;
        }

        //DebugLog.d(String.format("setStartTime [methodName:%s][startTime:%d][startThreadTime:%d]",
        //        methodName, curTime, curThreadTime));
    }

    /**
     * Set EndTime
     *
     * @param methodName
     * @param startTime
     * @param startThreadTime
     * @param exceedTime
     * @param monitorOnlyMainThread
     */
    public void setEndTime(final String methodName, long startTime, long startThreadTime, long exceedTime, boolean monitorOnlyMainThread) {
        if (TextUtils.isEmpty(methodName)) {
            //DebugLog.d(TAG, "methodName is not valid !!!");
            return;
        }

        if (!checkThread(monitorOnlyMainThread)) {
            //DebugLog.d(TAG, "thread is not valid !!!");
            return;
        }

        long endTime = System.currentTimeMillis();
        long endThreadTime = SystemClock.currentThreadTimeMillis();

        //DebugLog.d(String.format("setEndTime [methodName:%s][startTime:%d][endTime:%d][startThreadTime:%d][endThreadTime:%d]",
        //        methodName, startTime, endTime, startThreadTime, endThreadTime));

        TimeCostInfo timeCostInfo = TimeCostInfo.parse(methodName, startTime, startThreadTime, endTime, endThreadTime, exceedTime, exceedTime);
        handleCost(timeCostInfo);

        //DebugLog.d(String.format("<<<<<<<<<<<<< TimeCost spend [time:%d][threadTime:%d]",
        //        System.currentTimeMillis() - endTime, SystemClock.currentThreadTimeMillis() - endThreadTime));
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
                    "%s has exceed time. \n==========================================================> [TimeCost:%d][ThreadTimeCost:%d][ExceededTime:%d]",
                    timeCostInfo.getName(),
                    timeCostInfo.getTimeCost(),
                    timeCostInfo.getThreadTimeCost(),
                    timeCostInfo.getExceedMilliTime())
            );

            //processExceedList(timeCostInfo);

            HandlerThreadUtils.getWriteLogThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    //DebugLog.d(TAG, String.format("======> handleCost : [name:%s]", Thread.currentThread().getName()));
                    if (TimeCostCanary.get().getConfig().isShowDetailUI()) {
                        CanaryLogUtils.save(timeCostInfo.formatInfo());
                    }

                    if (!mInterceptorChain.isEmpty()) {
                        for (final TimeCostInterceptor interceptor : mInterceptorChain) {
                            if (null == TimeCostContext.getContext()) {
                                return;
                            }

                            interceptor.onExceed(TimeCostContext.getContext(), timeCostInfo);
                        }
                    }
                }
            });

        } else {
            /*DebugLog.d(TAG, String.format(
                    "[TimeCost:%d][ThreadTimeCost:%d][ExceededTime:%d] . %s has not exceed time. ",
                    timeCostInfo.getTimeCost(),
                    timeCostInfo.getThreadTimeCost(),
                    timeCostInfo.getExceedMilliTime(),
                    timeCostInfo.getName())
            );*/
        }
    }

    /*private void processExceedList(TimeCostInfo timeCostInfo) {
        long curTime = System.currentTimeMillis();
        if (curTime - mLastNotifyTime > TimeCostCanary.get().getConfig().getNotifyInterval()) {
            mLastNotifyTime = curTime;

            Iterator<TimeCostInfo> iterator = mExceedInfoList.iterator();
            while (iterator.hasNext()) {
                final TimeCostInfo info = iterator.next();
                final boolean hasNext = iterator.hasNext();

                HandlerThreadUtils.getWriteLogThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        DebugLog.d(TAG, String.format("======> handleCost : [name:%s][hasNext:%b]", Thread.currentThread().getName(), hasNext));
                        CanaryLogUtils.save(info.formatInfo());

                        if (!mInterceptorChain.isEmpty()) {
                            for (final TimeCostInterceptor interceptor : mInterceptorChain) {
                                if (null == TimeCostContext.getContext()) {
                                    return;
                                }

                                if (!hasNext) {
                                    interceptor.onExceed(TimeCostContext.getContext(), info);
                                }
                            }
                        }
                    }
                });

                iterator.remove();
            }

        } else {
            if (null != timeCostInfo) {
                mExceedInfoList.add(timeCostInfo);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        processExceedList(null);
                    }
                }, TimeCostCanary.get().getConfig().getNotifyInterval());
            }
        }
    }*/

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
