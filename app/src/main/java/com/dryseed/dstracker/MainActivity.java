package com.dryseed.dstracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dryseed.dstracker.annotations.TimeCost;

public class MainActivity extends AppCompatActivity {

    @Override
    @TimeCost(name = "onCreate")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @TimeCost(name = "myMethod", milliTime = 500L)
    public void myMethod(View view) {
        Log.d("TimeCost", "myMethod");
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @TimeCost(name = "myMethod2")
    private void myMethod2() {

    }
}
