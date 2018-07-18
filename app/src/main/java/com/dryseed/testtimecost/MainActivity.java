package com.dryseed.testtimecost;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dryseed.timecost.annotations.TimeCost;
import com.dryseed.timecost.utils.DebugLog;
import com.example.testaar.TestAar;
import com.example.testlibrary.TestLibrary;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    @Override
    @TimeCost(name = "onCreate")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @TimeCost(name = "myMethod", milliTime = 500L)
    public void myMethod(View view) {
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @TimeCost(name = "myMethod2")
    public void myMethod2(View view) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void myMethod3(View view) {
        TestLibrary testLibrary = new TestLibrary();
        testLibrary.testLibraryMethod();
    }

    @TimeCost(name = "myMethod4")
    public void myMethod4(View view) {
        new Thread(new Runnable() {
            @Override
            @TimeCost(name = "myMethod4_run", monitorOnlyMainThread = true)
            public void run() {
                myMehtod4Inner();
            }
        }).start();
    }

    @TimeCost(name = "myMethod4_inner", monitorOnlyMainThread = true)
    public void myMehtod4Inner() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void myMethod5(View view) {
        new TestAar().testAarMethod();
    }

    public void myMethod6(View view) {
        long timeStart = System.currentTimeMillis();
        long threadTimeStart = SystemClock.currentThreadTimeMillis();
        DebugLog.d("MMM", String.format("myMethod6 Time : %d | ThreadTime : %d", timeStart, threadTimeStart));
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DebugLog.d("MMM", String.format("TimeInterval : %d | ThreadTimeInterval : %d", System.currentTimeMillis() - timeStart, SystemClock.currentThreadTimeMillis() - threadTimeStart));

        /**
         * log:
         * 时钟时长，表示方法执行所消耗的时钟时间，即使方法没有占用 cpu，仅等待另一线程的完成，时长也会被记录。
         * Cpu 时长，表示方法执行所消耗的 cpu 时间，当方法没有占用 cpu 时，时间不会被记录。
         *
         * 07-17 18:55:06.834 12954-12954/com.dryseed.timecost D/MMM: myMethod6 Time : 1531824906834 | ThreadTime : 220
         * 07-17 18:55:08.834 12954-12954/com.dryseed.timecost D/MMM: TimeInterval : 2000 | ThreadTimeInterval : 0
         */
    }

    public void myMethod7(View view) {
        long timeStart = System.currentTimeMillis();
        long threadTimeStart = SystemClock.currentThreadTimeMillis();
        DebugLog.d("MMM", String.format("myMethod7 Time : %d | ThreadTime : %d", timeStart, threadTimeStart));

        new Thread(new SomeTest()).start();

        DebugLog.d("MMM", String.format("TimeInterval : %d | ThreadTimeInterval : %d", System.currentTimeMillis() - timeStart, SystemClock.currentThreadTimeMillis() - threadTimeStart));

        /**
         * log:
         * 07-17 18:57:44.799 12954-12954/com.dryseed.timecost D/MMM: myMethod7 Time : 1531825064798 | ThreadTim
         * 07-17 18:57:44.799 12954-12954/com.dryseed.timecost D/MMM: TimeInterval : 1 | ThreadTimeInterval : 1
         */
    }

    public void myMethod8(View view) {
        long timeStart = System.currentTimeMillis();
        long threadTimeStart = SystemClock.currentThreadTimeMillis();
        DebugLog.d("MMM", String.format("myMehtod8 Time : %d | ThreadTime : %d", timeStart, threadTimeStart));

        new SomeTest().someM();

        DebugLog.d("MMM", String.format("TimeInterval : %d | ThreadTimeInterval : %d", System.currentTimeMillis() - timeStart, SystemClock.currentThreadTimeMillis() - threadTimeStart));

        /**
         * log:
         * 07-17 18:59:24.204 13190-13190/com.dryseed.timecost D/MMM: myMehtod8 Time : 1531825164204 | ThreadTime : 259
         * 07-17 18:59:26.157 13190-13190/com.dryseed.timecost D/MMM: TimeInterval : 1953 | ThreadTimeInterval : 1465
         */
    }
}
