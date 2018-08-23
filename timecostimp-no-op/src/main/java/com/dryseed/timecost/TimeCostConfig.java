package com.dryseed.timecost;

/**
 * @author caiminming
 */

/**
 * @author caiminming
 */
public class TimeCostConfig {
    private TimeCostConfig() {
    }

    private long mExceedMilliTime;

    private long mExceedMaxMilliTIme;

    private long mThreadExceedMilliTime;

    private boolean mMonitorAllThread;

    private boolean mMonitorOnlyMainThread;

    private boolean mShowDetailUI;

    private boolean mShowNotifaction;

    private String mLogPath;

    private int mSortType;

    private long mNotifyInterval;

    private long mDelayStartMilliTime;

    public boolean isShowNotifaction() {
        return mShowNotifaction;
    }

    public void setShowNotifaction(boolean showNotifaction) {
        mShowNotifaction = showNotifaction;
    }

    public long getDelayStartMilliTime() {
        return mDelayStartMilliTime;
    }

    public void setDelayStartMilliTime(long delayStartMilliTime) {
        mDelayStartMilliTime = delayStartMilliTime;
    }

    public long getExceedMaxMilliTIme() {
        return mExceedMaxMilliTIme;
    }

    public void setExceedMaxMilliTIme(long exceedMaxMilliTIme) {
        mExceedMaxMilliTIme = exceedMaxMilliTIme;
    }

    public long getNotifyInterval() {
        return mNotifyInterval;
    }

    public void setNotifyInterval(long notifyInterval) {
        mNotifyInterval = notifyInterval;
    }

    public long getThreadExceedMilliTime() {
        return mThreadExceedMilliTime;
    }

    public void setThreadExceedMilliTime(long threadExceedMilliTime) {
        mThreadExceedMilliTime = threadExceedMilliTime;
    }

    public int getSortType() {
        return mSortType;
    }

    public void setSortType(int sortType) {
        mSortType = sortType;
    }

    public boolean isShowDetailUI() {
        return mShowDetailUI;
    }

    public void setShowDetailUI(boolean showDetailUI) {
        mShowDetailUI = showDetailUI;
    }

    public String getLogPath() {
        return mLogPath == null ? "" : mLogPath;
    }

    public void setLogPath(String logPath) {
        mLogPath = logPath;
    }

    public void setExceedMilliTime(long exceedMilliTime) {
        mExceedMilliTime = exceedMilliTime;
    }

    public boolean isMonitorAllThread() {
        return mMonitorAllThread;
    }

    public void setMonitorAllThread(boolean monitorAllThread) {
        mMonitorAllThread = monitorAllThread;
    }

    public boolean isMonitorOnlyMainThread() {
        return mMonitorOnlyMainThread;
    }

    public void setMonitorOnlyMainThread(boolean monitorOnlyMainThread) {
        mMonitorOnlyMainThread = monitorOnlyMainThread;
    }

    public long getExceedMilliTime() {
        return mExceedMilliTime;
    }

    public static class Builder {
        private long mExceedMilliTime = 0;

        private long mExceedMaxMilliTIme = Integer.MAX_VALUE;

        private long mThreadExceedMilliTime = 0;

        private boolean mMonitorAllThread = true;

        private boolean mMonitorOnlyMainThread = false;

        private boolean mShowDetailUI = true;

        private boolean mShowNotifaction = true;

        private int mSortType = 0;

        private String mLogPath = "";

        private long mNotifyInterval = 0;

        private long mDelayStartMilliTime = 0;

        public Builder setDelayStartMilliTime(long delayStartMilliTime) {
            mDelayStartMilliTime = delayStartMilliTime;
            return this;
        }

        public Builder setExceedMaxMilliTIme(long exceedMaxMilliTIme) {
            mExceedMaxMilliTIme = exceedMaxMilliTIme;
            return this;
        }

        public Builder setNotifyInterval(long notifyInterval) {
            mNotifyInterval = notifyInterval;
            return this;
        }

        public Builder setSortType(int sortType) {
            mSortType = sortType;
            return this;
        }

        public Builder setShowNotifaction(boolean showNotifaction) {
            mShowNotifaction = showNotifaction;
            return this;
        }

        public Builder setShowDetailUI(boolean showDetailUI) {
            mShowDetailUI = showDetailUI;
            return this;
        }

        public Builder setExceedMilliTime(long exceedMilliTime) {
            this.mExceedMilliTime = exceedMilliTime;
            return this;
        }

        public Builder setThreadExceedMilliTime(long exceedMilliTime) {
            this.mThreadExceedMilliTime = exceedMilliTime;
            return this;
        }

        public Builder setMonitorAllThread(boolean monitorAllThread) {
            if (monitorAllThread) {
                mMonitorOnlyMainThread = false;
            }
            mMonitorAllThread = monitorAllThread;
            return this;
        }

        public Builder setMonitorOnlyMainThread(boolean monitorOnlyMainThread) {
            if (monitorOnlyMainThread) {
                mMonitorAllThread = false;
            }
            mMonitorOnlyMainThread = monitorOnlyMainThread;
            return this;
        }

        public Builder setLogPath(String logPath) {
            this.mLogPath = logPath;
            return this;
        }

        public TimeCostConfig build() {
            final TimeCostConfig config = create();
            return config;
        }

        private TimeCostConfig create() {
            TimeCostConfig config = new TimeCostConfig();
            config.mExceedMilliTime = this.mExceedMilliTime;
            config.mExceedMaxMilliTIme = this.mExceedMaxMilliTIme;
            config.mThreadExceedMilliTime = this.mThreadExceedMilliTime;
            config.mMonitorAllThread = this.mMonitorAllThread;
            config.mMonitorOnlyMainThread = this.mMonitorOnlyMainThread;
            config.mShowDetailUI = this.mShowDetailUI;
            config.mShowNotifaction = this.mShowNotifaction;
            config.mLogPath = this.mLogPath;
            config.mSortType = this.mSortType;
            config.mNotifyInterval = this.mNotifyInterval;
            config.mDelayStartMilliTime = this.mDelayStartMilliTime;
            return config;
        }
    }
}
