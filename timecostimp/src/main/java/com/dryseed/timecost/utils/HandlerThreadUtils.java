package com.dryseed.timecost.utils;

import android.os.Handler;
import android.os.HandlerThread;

public class HandlerThreadUtils {

    private static HandlerThreadWrapper sWriteLogThread = new HandlerThreadWrapper("writer");

    private HandlerThreadUtils() {
        throw new InstantiationError("Must not instantiate this class");
    }

    public static Handler getWriteLogThreadHandler() {
        return sWriteLogThread.getHandler();
    }

    private static class HandlerThreadWrapper {
        private Handler handler = null;

        public HandlerThreadWrapper(String threadName) {
            HandlerThread handlerThread = new HandlerThread("TimeCostCanary-" + threadName);
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
        }

        public Handler getHandler() {
            return handler;
        }
    }
}
