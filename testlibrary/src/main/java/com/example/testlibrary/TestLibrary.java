package com.example.testlibrary;

import com.dryseed.dstracker.annotations.TimeCost;

public class TestLibrary {
    @TimeCost(milliTime = 300L)
    public void testLibraryMethod() {
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}