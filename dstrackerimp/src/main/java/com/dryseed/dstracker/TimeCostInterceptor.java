package com.dryseed.dstracker;

import android.content.Context;

/**
 * @author caiminming
 */
public interface TimeCostInterceptor {
    void onExceed(Context context, TimeCostInfo timeCostInfo);
}
