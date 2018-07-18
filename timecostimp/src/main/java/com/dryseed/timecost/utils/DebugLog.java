package com.dryseed.timecost.utils;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * @author caiminming
 */
public class DebugLog {
    public static final String TAG = "TimeCost";

    @SuppressLint("UseLogDirectly")
    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    @SuppressLint("UseLogDirectly")
    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    @SuppressLint("UseLogDirectly")
    public static void w(String tag, String msg) {
        Log.w(tag, msg);
    }

    @SuppressLint("UseLogDirectly")
    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    @SuppressLint("UseLogDirectly")
    public static void e(String tag, String msg, Throwable e) {
        Log.e(tag, msg, e);
    }
}
