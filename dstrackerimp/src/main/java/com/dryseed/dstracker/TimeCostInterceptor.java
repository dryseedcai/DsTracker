package com.dryseed.dstracker;

import android.content.Context;

public interface TimeCostInterceptor {
    void onExceed(Context context, TimeCostInfo timeCostInfo);
}
