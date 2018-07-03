package com.dryseed.dstracker;

/**
 * @author caiminming
 */
public class TimeCostInfo {
    /**
     * Method Name
     */
    private String name;
    /**
     * Start Time
     */
    private long mStartMilliTime;
    /**
     * End Time
     */
    private long mEndMilliTime;
    /**
     * Exceed Time
     */
    private long mExceedMilliTime = 0;
    /**
     * Cost Time
     */
    private long mTimeCost;

    public TimeCostInfo(String name, long curMilliTime) {
        this.name = name;
        this.mStartMilliTime = curMilliTime;
    }

    public TimeCostInfo(String name, long curMilliTime, long excceedMilliTime) {
        this.name = name;
        this.mStartMilliTime = curMilliTime;
        this.mExceedMilliTime = excceedMilliTime;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static TimeCostInfo parse(String name, long curMilliTime) {
        return new TimeCostInfo(name, curMilliTime, TimeCostCanary.get().getConfig().getMilliExceedTime());
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

    @Override
    public String toString() {
        return "TimeCostInfo{" +
                "name='" + name + '\'' +
                ", mStartMilliTime=" + mStartMilliTime +
                ", mEndMilliTime=" + mEndMilliTime +
                ", mExceedMilliTime=" + mExceedMilliTime +
                '}';
    }

    public boolean isExceed() {
        return mTimeCost > mExceedMilliTime;
    }
}
