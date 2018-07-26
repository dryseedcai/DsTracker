package com.dryseed.timecost.entity;

import android.text.TextUtils;

import com.dryseed.timecost.TimeCostCanary;
import com.dryseed.timecost.utils.DebugLog;

/**
 * @author caiminming
 */
public class TimeCostInfo {
    public static final String SEPARATOR = "\r\n";
    protected static final String EQUALS = "=";
    protected static final String KEY_NAME = "MethodName";
    protected static final String KEY_START_TIME = "StartMilliTime";
    protected static final String KEY_END_TIME = "EndMilliTime";
    protected static final String KEY_EXCEED_TIME = "ExceedMilliTime";
    protected static final String KEY_THREAD_EXCEED_TIME = "ThreadExceedMilliTime";
    protected static final String KEY_TIME_COST = "TimeCost";
    protected static final String KEY_THREAD_TIME_COST = "ThreadTimeCost";

    /**
     * Method Name
     */
    protected String mMethodName;

    /**
     * Start Time
     */
    protected long mStartMilliTime;

    /**
     * Start Thread Time
     */
    protected long mStartThreadMilliTime;

    /**
     * End Time
     */
    protected long mEndMilliTime;

    /**
     * End Thread Time
     */
    protected long mEndThreadMilliTime;

    /**
     * Exceed Time
     */
    protected long mExceedMilliTime = 0;

    /**
     * Thread Exceed Time
     */
    protected long mExceedThreadMilliTime = 0;

    /**
     * Cost Time
     */
    protected long mTimeCost;

    /**
     * Thread Cost Time
     */
    protected long mThreadTimeCost;

    public TimeCostInfo() {
    }

    public TimeCostInfo(String name, long startMilliTime, long startThreadMilliTime, long endMilliTime,
                        long endThreadMilliTime, long exceedMilliTime, long exceedThreadMilliTime) {
        this.mMethodName = name;
        this.mStartMilliTime = startMilliTime;
        this.mStartThreadMilliTime = startThreadMilliTime;
        this.mEndMilliTime = endMilliTime;
        this.mEndThreadMilliTime = endThreadMilliTime;
        this.mExceedMilliTime = exceedMilliTime <= 0 ? TimeCostCanary.get().getConfig().getExceedMilliTime() : exceedMilliTime;
        this.mExceedThreadMilliTime = exceedThreadMilliTime <= 0 ? TimeCostCanary.get().getConfig().getExceedMilliTime() : exceedThreadMilliTime;
        this.mTimeCost = mEndMilliTime - mStartMilliTime;
        this.mThreadTimeCost = mEndThreadMilliTime - mStartThreadMilliTime;
    }

    public String getName() {
        return mMethodName == null ? "" : mMethodName;
    }

    public void setName(String name) {
        this.mMethodName = name;
    }

    public static TimeCostInfo parse(String name, long startMilliTime, long startThreadMilliTime,
                                     long endMilliTime, long endThreadMilliTime, long exceedMilliTime, long exceedThreadMilliTime) {
        return new TimeCostInfo(name, startMilliTime, startThreadMilliTime, endMilliTime, endThreadMilliTime, exceedMilliTime, exceedThreadMilliTime);
    }

    public long getStartMilliTime() {
        return mStartMilliTime;
    }

    public long getEndThreadMilliTime() {
        return mEndThreadMilliTime;
    }

    public long getEndMilliTime() {
        return mEndMilliTime;
    }

    public long getExceedMilliTime() {
        return mExceedMilliTime;
    }

    public void setExceedMilliTime(long exceedMilliTime) {
        this.mExceedMilliTime = exceedMilliTime;
    }

    public long getStartThreadMilliTime() {
        return mStartThreadMilliTime;
    }

    public void setStartThreadMilliTime(long startThreadMilliTime) {
        mStartThreadMilliTime = startThreadMilliTime;
    }

    public long getTimeCost() {
        return mTimeCost;
    }

    public void setTimeCost(long timeCost) {
        mTimeCost = timeCost;
    }

    public long getThreadTimeCost() {
        return mThreadTimeCost;
    }

    public void setThreadTimeCost(long threadTimeCost) {
        mThreadTimeCost = threadTimeCost;
    }

    public String formatInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append(KEY_NAME).append(EQUALS).append(mMethodName).append(SEPARATOR);
        sb.append(KEY_START_TIME).append(EQUALS).append(mStartMilliTime).append(SEPARATOR);
        sb.append(KEY_END_TIME).append(EQUALS).append(mEndMilliTime).append(SEPARATOR);
        sb.append(KEY_EXCEED_TIME).append(EQUALS).append(mExceedMilliTime).append(SEPARATOR);
        sb.append(KEY_THREAD_EXCEED_TIME).append(EQUALS).append(mExceedThreadMilliTime).append(SEPARATOR);
        sb.append(KEY_TIME_COST).append(EQUALS).append(mTimeCost).append(SEPARATOR);
        sb.append(KEY_THREAD_TIME_COST).append(EQUALS).append(mThreadTimeCost).append(SEPARATOR);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "TimeCostInfo{" +
                "name='" + mMethodName + '\'' +
                ", mStartMilliTime=" + mStartMilliTime +
                ", mStartThreadMilliTime=" + mStartThreadMilliTime +
                ", mEndMilliTime=" + mEndMilliTime +
                ", mEndThreadMilliTime=" + mEndThreadMilliTime +
                ", mExceedMilliTime=" + mExceedMilliTime +
                ", mExceedThreadMilliTime=" + mExceedThreadMilliTime +
                ", mTimeCost=" + mTimeCost +
                ", mThreadTimeCost=" + mThreadTimeCost +
                '}';
    }

    public boolean isExceed() {
        return mTimeCost > mExceedMilliTime && mThreadTimeCost > mExceedThreadMilliTime;
    }

    @Override
    public boolean equals(Object o) {
        if (null != o && this.getClass() == o.getClass()) {
            return TextUtils.equals(mMethodName, ((TimeCostInfo) o).mMethodName);
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        if (!TextUtils.isEmpty(mMethodName)) {
            return mMethodName.hashCode();
        }
        return super.hashCode();
    }
}
