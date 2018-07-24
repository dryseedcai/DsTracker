package com.dryseed.timecost.entity;

import android.text.TextUtils;

import com.dryseed.timecost.TimeCostCanary;
import com.dryseed.timecost.utils.DebugLog;

import java.util.Objects;

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
    protected long mThreadExceedMilliTime = 0;

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

    public TimeCostInfo(String name, long curMilliTime, long curThreadMilliTime, long exceedMilliTime, long threadExceedMilliTime) {
        this.mMethodName = name;
        this.mStartMilliTime = curMilliTime;
        this.mStartThreadMilliTime = curThreadMilliTime;
        this.mExceedMilliTime = exceedMilliTime <= 0 ? TimeCostCanary.get().getConfig().getExceedMilliTime() : exceedMilliTime;
        this.mThreadExceedMilliTime = threadExceedMilliTime <= 0 ? TimeCostCanary.get().getConfig().getExceedMilliTime() : threadExceedMilliTime;
    }

    public String getName() {
        return mMethodName == null ? "" : mMethodName;
    }

    public void setName(String name) {
        this.mMethodName = name;
    }

    public static TimeCostInfo parse(String name, long curMilliTime, long curThreadMilliTime) {
        return parse(name, curMilliTime, curThreadMilliTime, 0, 0);
    }

    public static TimeCostInfo parse(String name, long curMilliTime, long curThreadMilliTime, long exccedMilliTime, long threadExceedMilliTIme) {
        return new TimeCostInfo(name, curMilliTime, curThreadMilliTime, exccedMilliTime, threadExceedMilliTIme);
    }

    public long getStartMilliTime() {
        return mStartMilliTime;
    }

    public void setStartMilliTime(long startMilliTime) {
        mStartMilliTime = startMilliTime;
    }

    public long getEndThreadMilliTime() {
        return mEndThreadMilliTime;
    }

    public void setEndThreadMilliTime(long endThreadMilliTime) {
        mEndThreadMilliTime = endThreadMilliTime;
        mThreadTimeCost = mEndThreadMilliTime - mStartThreadMilliTime;
        DebugLog.d(String.format(
                "setEndThreadMilliTime [mThreadTimeCost:%d][mEndThreadMilliTime:%d][mStartThreadMilliTime:%d]",
                mThreadTimeCost,
                mEndThreadMilliTime,
                mStartThreadMilliTime
        ));
    }

    public long getEndMilliTime() {
        return mEndMilliTime;
    }

    public void setEndMilliTime(long endMilliTime) {
        mEndMilliTime = endMilliTime;
        mTimeCost = mEndMilliTime - mStartMilliTime;
        DebugLog.d(String.format(
                "setEndMilliTime [mTimeCost:%d][mEndMilliTime:%d][mStartMilliTime:%d]",
                mTimeCost,
                mEndMilliTime,
                mStartMilliTime
        ));
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
        sb.append(KEY_THREAD_EXCEED_TIME).append(EQUALS).append(mThreadExceedMilliTime).append(SEPARATOR);
        sb.append(KEY_TIME_COST).append(EQUALS).append(mTimeCost).append(SEPARATOR);
        sb.append(KEY_THREAD_TIME_COST).append(EQUALS).append(mThreadTimeCost).append(SEPARATOR);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "TimeCostInfo{" +
                "name='" + mMethodName + '\'' +
                ", mStartMilliTime=" + mStartMilliTime +
                ", mEndMilliTime=" + mEndMilliTime +
                ", mExceedMilliTime=" + mExceedMilliTime +
                ", mThreadExceedMilliTime=" + mThreadExceedMilliTime +
                '}';
    }

    public boolean isExceed() {
        return mTimeCost > mExceedMilliTime && mThreadTimeCost > mThreadExceedMilliTime;
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
