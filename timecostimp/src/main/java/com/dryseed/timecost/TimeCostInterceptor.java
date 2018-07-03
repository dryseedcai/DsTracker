package com.dryseed.timecost;

import android.content.Context;

/**
 * @author caiminming
 */
public interface TimeCostInterceptor {
    void onExceed(Context context, TimeCostInfo timeCostInfo);
}
