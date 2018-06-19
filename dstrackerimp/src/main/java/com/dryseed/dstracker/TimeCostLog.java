package com.dryseed.dstracker;

import android.os.Looper;
import android.util.Log;

import com.dryseed.dstracker.annotations.TimeCost;

import java.util.HashMap;
import java.util.Map;


public class TimeCostLog {
    public static final String TAG = "MMM";
    public static Map<String, Long> sStartTime = new HashMap<>();
    public static Map<String, Long> sEndTime = new HashMap<>();

    /**
     * 设置开始时间，内部使用
     *
     * @param methodName
     * @param time
     */
    public static void setStartTime(String methodName, long time) {
        sStartTime.put(methodName, time);
    }

    /**
     * 设置结束时间，内部使用
     *
     * @param methodName
     * @param time
     */
    public static void setEndTime(String methodName, long time) {
        sEndTime.put(methodName, time);
        handleCost(methodName);

    }

    private static void handleCost(String methodName) {
        long start = sStartTime.get(methodName);
        long end = sEndTime.get(methodName);
        long cost = Long.valueOf(end - start) / (1000 * 1000);
        String log = "Usopp TimeCostUtil Method:==> " + methodName + " ==>Cost:" + cost + " ms";
        try {

            //监听
            if (TimeCostUtil.mOnLogListener != null) {
                TimeCostUtil.mOnLogListener.log(log, methodName, cost);
            } else {
                Log.d(TAG, log);
            }
            //收集日志
            if (TimeCostUtil.isOpenLogCache)
                TimeCostUtil.mLogCache.add(log);
            //显示UI
            if (TimeCostUtil.isOpenLogUI) {
                if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                   /*if(logView==null){
                        logView = new LogView(TimeCostUtil.mContext);
                    }
                    if(cost>50){
                        String costLogs = "<font color='#ff74b9'>"+cost+"</font>";
                        logView.appendLog(methodName+":"+costLogs+" ms");
                    }else {
                        logView.appendLog(methodName+":"+cost+" ms");
                    }*/
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 统计静态类方法
     *
     * @param log
     */
    public static void countStaticClass(String classname, String methodname, String log) {
        if (TimeCostUtil.mCountStaticClassListener != null) {
            TimeCostUtil.mCountStaticClassListener.log(classname, methodname, log);
        } else {
            System.out.println(log);
        }
    }


    @TimeCost
    public static boolean isOk() {
        return true;
    }

    //private static LogView logView;

}
