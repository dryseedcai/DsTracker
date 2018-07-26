package com.dryseed.timecost.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class TimeCostLogInfo extends TimeCostInfo {
    private static final String TAG = "TimeCostLogInfo";

    public File mLogFile;
    public String mConcernStackString;

    public static TimeCostLogInfo parse(File file) {
        TimeCostLogInfo timeCostLogInfo = new TimeCostLogInfo();
        timeCostLogInfo.mLogFile = file;

        BufferedReader reader = null;
        try {
            InputStreamReader in = new InputStreamReader(new FileInputStream(file), "UTF-8");

            reader = new BufferedReader(in);
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.startsWith(KEY_NAME)) {
                    timeCostLogInfo.mMethodName = line.split(EQUALS)[1];
                } else if (line.startsWith(KEY_START_TIME)) {
                    timeCostLogInfo.mStartMilliTime = Long.valueOf(line.split(EQUALS)[1]);
                } else if (line.startsWith(KEY_END_TIME)) {
                    timeCostLogInfo.mEndMilliTime = Long.valueOf(line.split(EQUALS)[1]);
                } else if (line.startsWith(KEY_EXCEED_TIME)) {
                    timeCostLogInfo.mExceedMilliTime = Long.valueOf(line.split(EQUALS)[1]);
                } else if (line.startsWith(KEY_THREAD_EXCEED_TIME)) {
                    timeCostLogInfo.mExceedThreadMilliTime = Long.valueOf(line.split(EQUALS)[1]);
                } else if (line.startsWith(KEY_TIME_COST)) {
                    timeCostLogInfo.mTimeCost = Long.valueOf(line.split(EQUALS)[1]);
                } else if (line.startsWith(KEY_THREAD_TIME_COST)) {
                    timeCostLogInfo.mThreadTimeCost = Long.valueOf(line.split(EQUALS)[1]);
                }
            }
            reader.close();
            reader = null;
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return timeCostLogInfo;
    }
}
