package com.dryseed.timecost;

import com.dryseed.timecost.constants.TimeCostConstant;

/**
 * @author caiminming
 */
public class TimeCostConfig {
    public static final int CONFIG_SORT_TYPE_START_TIME = 0;
    public static final int CONFIG_SORT_TYPE_TIME_COST = 1;
    public static final int CONFIG_SORT_TYPE_THREAD_TIME_COST = 2;

    private TimeCostConfig() {
    }

    private long mMilliExceedTime;

    private boolean mMonitorAllThread;

    private boolean mMonitorOnlyMainThread;

    private boolean mShowDetailUI;

    private String mLogPath;

    private int mSortType;

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

    public void setMilliExceedTime(long milliExceedTime) {
        mMilliExceedTime = milliExceedTime;
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

    public long getMilliExceedTime() {
        return mMilliExceedTime;
    }

    public static class Builder {
        private long milliExceedTime = TimeCostConstant.TIME_COST_DEFAULT_EXCEED_TIME;

        private boolean mMonitorAllThread = true;

        private boolean mMonitorOnlyMainThread = false;

        private boolean mShowDetailUI = true;

        private int mSortType = CONFIG_SORT_TYPE_START_TIME;

        private String mLogPath = TimeCostConstant.TIME_COST_DEFAULT_LOG_DIR;

        public Builder setSortType(int sortType) {
            mSortType = sortType;
            return this;
        }

        public Builder setShowDetailUI(boolean showDetailUI) {
            mShowDetailUI = showDetailUI;
            return this;
        }

        public Builder setMilliExceedTime(long milliExceedTime) {
            this.milliExceedTime = milliExceedTime;
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
            config.mMilliExceedTime = this.milliExceedTime;
            config.mMonitorAllThread = this.mMonitorAllThread;
            config.mMonitorOnlyMainThread = this.mMonitorOnlyMainThread;
            config.mShowDetailUI = this.mShowDetailUI;
            config.mLogPath = this.mLogPath;
            config.mSortType = this.mSortType;
            return config;
        }
    }
}
