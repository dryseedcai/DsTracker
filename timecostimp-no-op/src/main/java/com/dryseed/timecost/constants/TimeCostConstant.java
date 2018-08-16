package com.dryseed.timecost.constants;

public class TimeCostConstant {
    public static final long TIME_COST_DEFAULT_EXCEED_TIME = 1000L;
    public static final long TIME_COST_DEFAULT_THREAD_EXCEED_TIME = 1000L;
    public static final String TIME_COST_DEFAULT_LOG_DIR = "/timecostcanary/";
    public static final int TIME_COST_MAX_LOG_COUNT = 500;
    public static final long TIME_COST_NOTIFY_INTERVAL = 3000;

    public static final int CONFIG_SORT_TYPE_START_TIME = 0;
    public static final int CONFIG_SORT_TYPE_TIME_COST = 1;
    public static final int CONFIG_SORT_TYPE_THREAD_TIME_COST = 2;
}
