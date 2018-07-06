package com.dryseed.timecost.entity;

import com.dryseed.timecost.TimeCostCanary;

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
    protected static final String KEY_TIME_COST = "TimeCost";


    /**
     * Method Name
     */
    protected String mMethodName;

    /**
     * Start Time
     */
    protected long mStartMilliTime;

    /**
     * End Time
     */
    protected long mEndMilliTime;

    /**
     * Exceed Time
     */
    protected long mExceedMilliTime = 0;

    /**
     * Cost Time
     */
    protected long mTimeCost;

    public TimeCostInfo() {
    }

    public TimeCostInfo(String name, long curMilliTime) {
        this(name, curMilliTime, TimeCostCanary.get().getConfig().getMilliExceedTime());
    }

    public TimeCostInfo(String name, long curMilliTime, long excceedMilliTime) {
        this.mMethodName = name;
        this.mStartMilliTime = curMilliTime;
        this.mExceedMilliTime = excceedMilliTime <= 0 ? TimeCostCanary.get().getConfig().getMilliExceedTime() : excceedMilliTime;
    }

    public String getName() {
        return mMethodName == null ? "" : mMethodName;
    }

    public void setName(String name) {
        this.mMethodName = name;
    }

    public static TimeCostInfo parse(String name, long curMilliTime) {
        return parse(name, curMilliTime, 0);
    }

    public static TimeCostInfo parse(String name, long curMilliTime, long excceedMilliTime) {
        return new TimeCostInfo(name, curMilliTime, excceedMilliTime);
    }

    public long getStartMilliTime() {
        return mStartMilliTime;
    }

    public void setStartMilliTime(long startMilliTime) {
        mStartMilliTime = startMilliTime;
    }

    public long getEndMilliTime() {
        return mEndMilliTime;
    }

    public void setEndMilliTime(long endMilliTime) {
        mEndMilliTime = endMilliTime;
        mTimeCost = mEndMilliTime - mStartMilliTime;
    }

    public long getExceedMilliTime() {
        return mExceedMilliTime;
    }

    public void setExceedMilliTime(long exceedMilliTime) {
        this.mExceedMilliTime = exceedMilliTime;
    }

    public long getTimeCost() {
        return mTimeCost;
    }

    public void setTimeCost(long timeCost) {
        mTimeCost = timeCost;
    }

    public String formatInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append(KEY_NAME).append(EQUALS).append(mMethodName).append(SEPARATOR);
        sb.append(KEY_START_TIME).append(EQUALS).append(mStartMilliTime).append(SEPARATOR);
        sb.append(KEY_END_TIME).append(EQUALS).append(mEndMilliTime).append(SEPARATOR);
        sb.append(KEY_EXCEED_TIME).append(EQUALS).append(mExceedMilliTime).append(SEPARATOR);
        sb.append(KEY_TIME_COST).append(EQUALS).append(mTimeCost).append(SEPARATOR);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "TimeCostInfo{" +
                "name='" + mMethodName + '\'' +
                ", mStartMilliTime=" + mStartMilliTime +
                ", mEndMilliTime=" + mEndMilliTime +
                ", mExceedMilliTime=" + mExceedMilliTime +
                '}';
    }

    public boolean isExceed() {
        return mTimeCost > mExceedMilliTime;
    }
}
