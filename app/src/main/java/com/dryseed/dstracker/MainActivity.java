package com.dryseed.dstracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dryseed.dstracker.annotations.TimeCost;

public class MainActivity extends AppCompatActivity {

    @Override
    @TimeCost
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
