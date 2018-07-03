package com.dryseed.dstracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dryseed.dstracker.annotations.TimeCost;
import com.example.testlibrary.TestLibrary;

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
}
