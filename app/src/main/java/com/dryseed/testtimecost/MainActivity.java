package com.dryseed.testtimecost;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.dryseed.timecost.annotations.TimeCost;
import com.example.testaar.TestAar;
import com.example.testlibrary.TestLibrary;
import com.example.testmodule.TestModule;

public class MainActivity extends Activity {

    @Override
    @TimeCost(name = "onCreate")
    protected void onCreate(Bundle savedInstanceState) {
        //long var2 = System.currentTimeMillis();
        //long var4 = SystemClock.currentThreadTimeMillis();
        //TimeCostCanary.get().setStartTime("onCreate", var2, var4);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        //TimeCostCanary.get().setEndTime("onCreate", var2, var4, 0L, false);
    }

    /**
     * ThreadExceedTime=0 | ExceedTime = 1200
     * ThreadExceedTime是否work
     *
     * @param view
     */
    @TimeCost(name = "myMethod", milliTime = 500L)
    public void myMethod(View view) {
        new SomeTest().someM();
    }

    /**
     * 子线程sleep
     * monitorOnlyMainThread是否work
     *
     * @param view
     */
    @TimeCost(name = "myMethod2")
    public void myMethod2(View view) {
        new Thread(new Runnable() {
            @Override
            @TimeCost(name = "myMethod4_run", monitorOnlyMainThread = true)
            public void run() {
                myMehtod2Inner();
            }
        }).start();
    }

    @TimeCost(monitorOnlyMainThread = true)
    public void myMehtod2Inner() {
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test Jar
     *
     * @param view
     */
    public void myMethod3(View view) {
        TestLibrary testLibrary = new TestLibrary();
        testLibrary.testLibraryMethod();
    }

    /**
     * Test Module
     *
     * @param view
     */
    public void myMethod4(View view) {
        TestModule testModule = new TestModule();
        testModule.testModuleMethod();
    }

    /**
     * Test AAR
     *
     * @param view
     */
    public void myMethod5(View view) {
        new TestAar().testAarMethod();
    }

    /**
     * log:
     * 时钟时长，表示方法执行所消耗的时钟时间，即使方法没有占用 cpu，仅等待另一线程的完成，时长也会被记录。
     * Cpu 时长，表示方法执行所消耗的 cpu 时间，当方法没有占用 cpu 时，时间不会被记录。
     * <p>
     * 07-17 18:55:06.834 12954-12954/com.dryseed.timecost D/MMM: myMethod6 Time : 1531824906834 | ThreadTime : 220
     * 07-17 18:55:08.834 12954-12954/com.dryseed.timecost D/MMM: TimeInterval : 2000 | ThreadTimeInterval : 0
     */
    public void myMethod6(View view) {
        long timeStart = System.currentTimeMillis();
        long threadTimeStart = SystemClock.currentThreadTimeMillis();
        Log.d("MMM", String.format("myMehtod8 Time : %d | ThreadTime : %d", timeStart, threadTimeStart));

        new SomeTest().someM();

        Log.d("MMM", String.format("TimeInterval : %d | ThreadTimeInterval : %d", System.currentTimeMillis() - timeStart, SystemClock.currentThreadTimeMillis() - threadTimeStart));

        /**
         * log:
         * 07-17 18:59:24.204 13190-13190/com.dryseed.timecost D/MMM: myMehtod8 Time : 1531825164204 | ThreadTime : 259
         * 07-17 18:59:26.157 13190-13190/com.dryseed.timecost D/MMM: TimeInterval : 1953 | ThreadTimeInterval : 1465
         */
    }

    /**
     * 多任务同时进行
     *
     * @param view
     */
    public void myMethod7(View view) {
        for (int i = 0; i < 15; i++) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    new SomeTest().someM();
                }
            });
        }
    }


}
