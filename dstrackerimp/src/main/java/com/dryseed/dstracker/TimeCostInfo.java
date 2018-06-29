package com.dryseed.dstracker;

public class TimeCostInfo {
    private String name;
    private long mStartMilliTime;
    private long mEndMilliTime;
    private long exceedMilliTime = 0;

    public TimeCostInfo(String name, long curMilliTime) {
        this.name = name;
        this.mStartMilliTime = curMilliTime;
    }

    public TimeCostInfo(String name, long curMilliTime, long excceedMilliTime) {
        this.name = name;
        this.mStartMilliTime = curMilliTime;
        this.exceedMilliTime = excceedMilliTime;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static TimeCostInfo parse(String name, long curMilliTime) {
        return new TimeCostInfo(name, curMilliTime, 1000L);
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
    }

    public long getExceedMilliTime() {
        return exceedMilliTime;
    }

    public void setExceedMilliTime(long exceedMilliTime) {
        this.exceedMilliTime = exceedMilliTime;
    }

    @Override
    public String toString() {
        return "TimeCostInfo{" +
                "name='" + name + '\'' +
                ", mStartMilliTime=" + mStartMilliTime +
                ", mEndMilliTime=" + mEndMilliTime +
                ", exceedMilliTime=" + exceedMilliTime +
                '}';
    }
}
