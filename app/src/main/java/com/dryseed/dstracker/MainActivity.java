package com.dryseed.dstracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dryseed.dstracker.annotations.TimeCost;

public class MainActivity extends AppCompatActivity {

    @Override
    @TimeCost(name = "onCreateName")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
        Log.d("TimeCost", "onCreate");
    }

    @TimeCost(name = "myMethodName", milliTime = 500l)
    public void myMethod(View view) {
        Log.d("TimeCost", "myMethod");
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void myMethod2() {

    }
}
