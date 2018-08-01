package com.dryseed.timecost.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Environment;
import android.util.Log;

import com.dryseed.timecost.TimeCostCanary;
import com.dryseed.timecost.TimeCostContext;
import com.dryseed.timecost.entity.TimeCostInfo;


public class CanaryLogUtils {
    private static final String TAG = "TimeCostLogWriter";
    private static final Object SAVE_DELETE_LOCK = new Object();
    private static final SimpleDateFormat FILE_NAME_FORMATTER
            = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS", Locale.US);
    private static final SimpleDateFormat TIME_FORMATTER
            = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private static final long OBSOLETE_DURATION = 2 * 24 * 3600 * 1000L;

    /**
     * Save log to file
     *
     * @param str block info string
     * @return log file path
     */
    public static String save(String str) {
        String path;
        synchronized (SAVE_DELETE_LOCK) {
            path = save("looper", str);
        }
        return path;
    }

    private static String save(String logFileName, String str) {
        String path = "";
        BufferedWriter writer = null;
        try {
            File file = detectedLogDirectory();
            long time = System.currentTimeMillis();
            path = String.format("%s/%s-%s.log", file.getAbsolutePath(), logFileName, FILE_NAME_FORMATTER.format(time));
            //DebugLog.d(TAG, "path : " + path);
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(path, true), "UTF-8");

            writer = new BufferedWriter(out);

            writer.write(TimeCostInfo.SEPARATOR);
            writer.write("**********************");
            writer.write(TimeCostInfo.SEPARATOR);
            writer.write(TIME_FORMATTER.format(time) + "(write log time)");
            writer.write(TimeCostInfo.SEPARATOR);
            writer.write(TimeCostInfo.SEPARATOR);
            writer.write(str);
            writer.write(TimeCostInfo.SEPARATOR);

            writer.flush();
            writer.close();
            writer = null;

        } catch (Throwable t) {
            DebugLog.e(TAG, "save: ", t);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {
                DebugLog.e(TAG, "save: ", e);
            }
        }
        return path;
    }

    private static String getPath() {
        // Log : /storage/emulated/0
        //DebugLog.d(TAG, String.format("Environment.getExternalStorageDirectory() : %s", Environment.getExternalStorageDirectory()));
        // Log : /data/data/com.dryseed.timecost/files
        //DebugLog.d(TAG, String.format("TimeCostContext.getContext().getFilesDir() : %s", TimeCostContext.getContext().getFilesDir()));

        String state = Environment.getExternalStorageState();
        // Log : /storage/emulated/0/timecostcanary/
        String logPath = TimeCostContext.getContext() == null ? "" : TimeCostCanary.get().getConfig().getLogPath();

        if (Environment.MEDIA_MOUNTED.equals(state)
                && Environment.getExternalStorageDirectory().canWrite()) {
            return Environment.getExternalStorageDirectory().getPath() + logPath;
        }
        return TimeCostContext.getContext().getFilesDir() + TimeCostCanary.get().getConfig().getLogPath();
    }

    private static File detectedLogDirectory() {
        //DebugLog.d(TAG, "getPath : " + getPath());
        File directory = new File(getPath());
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    public static File[] getLogFiles() {
        File f = detectedLogDirectory();
        if (f.exists() && f.isDirectory()) {
            return f.listFiles(new LogFileFilter());
        }
        return null;
    }

    private static class LogFileFilter implements FilenameFilter {

        private String TYPE = ".log";

        LogFileFilter() {

        }

        @Override
        public boolean accept(File dir, String filename) {
            return filename.endsWith(TYPE);
        }
    }

    /**
     * Delete obsolete log files, which is by default 2 days.
     */
    public static void cleanObsolete() {
        HandlerThreadUtils.getWriteLogThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                File[] f = CanaryLogUtils.getLogFiles();
                if (f != null && f.length > 0) {
                    synchronized (SAVE_DELETE_LOCK) {
                        for (File aF : f) {
                            if (now - aF.lastModified() > OBSOLETE_DURATION) {
                                aF.delete();
                            }
                        }
                    }
                }
            }
        });
    }


    public static void deleteAll() {
        synchronized (SAVE_DELETE_LOCK) {
            try {
                File[] files = CanaryLogUtils.getLogFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        file.delete();
                    }
                }
            } catch (Throwable e) {
                DebugLog.e(TAG, "deleteAll: ", e);
            }
        }
    }
}
