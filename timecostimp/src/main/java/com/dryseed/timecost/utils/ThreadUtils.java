package com.dryseed.timecost.utils;

import android.os.Looper;

public class ThreadUtils {
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
