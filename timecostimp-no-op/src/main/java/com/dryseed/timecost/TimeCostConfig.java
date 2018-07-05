package com.dryseed.timecost;

/**
 * @author caiminming
 */
public class TimeCostConfig {
    private TimeCostConfig() {
    }

    private long mMilliExceedTime;

    private boolean mMonitorAllThread;

    private boolean mMonitorOnlyMainThread;

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
        private long milliExceedTime = Integer.MAX_VALUE;

        private boolean mMonitorAllThread = true;

        private boolean mMonitorOnlyMainThread = false;

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

        public TimeCostConfig build() {
            final TimeCostConfig config = create();
            return config;
        }

        private TimeCostConfig create() {
            TimeCostConfig config = new TimeCostConfig();
            config.mMilliExceedTime = this.milliExceedTime;
            config.mMonitorAllThread = this.mMonitorAllThread;
            config.mMonitorOnlyMainThread = this.mMonitorOnlyMainThread;
            return config;
        }
    }
}
