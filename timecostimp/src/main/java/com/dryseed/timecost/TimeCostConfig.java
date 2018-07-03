package com.dryseed.timecost;

/**
 * @author caiminming
 */
public class TimeCostConfig {
    private TimeCostConfig() {
    }

    private long mMilliExceedTime;

    public long getMilliExceedTime() {
        return mMilliExceedTime;
    }

    public void setMilliExceedTime(int milliExceedTime) {
        mMilliExceedTime = milliExceedTime;
    }

    public static class Builder {
        private long milliExceedTime = TimeCostConstant.TIME_COST_DEFAULT_EXCEED_TIME;

        public Builder setMilliExceedTime(long milliExceedTime) {
            this.milliExceedTime = milliExceedTime;
            return this;
        }

        public TimeCostConfig build() {
            final TimeCostConfig config = create();
            return config;
        }

        private TimeCostConfig create() {
            TimeCostConfig config = new TimeCostConfig();
            config.mMilliExceedTime = this.milliExceedTime;
            return config;
        }
    }
}
