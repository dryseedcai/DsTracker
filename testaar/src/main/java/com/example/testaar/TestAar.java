package com.example.testaar;

import com.dryseed.timecost.annotations.TimeCost;

public class TestAar {
    @TimeCost(milliTime = 300L)
    public void testAarMethod() {
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
