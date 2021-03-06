package com.dryseed.timecost;

import android.content.Context;
import android.util.Log;

/**
 * @author caiminming
 */
public class TimeCostContext {
    private static final String TAG = "TimeCostContext";
    private static Context sApplicationContext;

    public static void init(Context applicationContext) {
        sApplicationContext = applicationContext;
    }

    public static Context getContext() {
        if (sApplicationContext == null) {
            throw new RuntimeException("Please call TimeCostCanary.install() first");
        } else {
            return sApplicationContext;
        }
    }
}
