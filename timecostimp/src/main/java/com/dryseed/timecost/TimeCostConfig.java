package com.dryseed.timecost;

import com.dryseed.timecost.constants.TimeCostConstant;

/**
 * @author caiminming
 */
public class TimeCostConfig {
    private TimeCostConfig() {
    }

    private long mMilliExceedTime;

    private boolean mMonitorAllThread;

    private boolean mMonitorOnlyMainThread;

    private String mLogPath;

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

        private String mLogPath = TimeCostConstant.TIME_COST_DEFAULT_LOG_DIR;

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
            config.mLogPath = this.mLogPath;
            return config;
        }
    }
}
