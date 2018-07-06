package com.dryseed.timecost;

import android.content.Context;

import com.dryseed.timecost.entity.TimeCostInfo;

/**
 * @author caiminming
 */
public interface TimeCostInterceptor {
    void onExceed(Context context, TimeCostInfo timeCostInfo);
}
