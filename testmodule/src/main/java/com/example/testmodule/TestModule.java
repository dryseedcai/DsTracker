package com.example.testmodule;

import com.dryseed.timecost.annotations.TimeCost;

/**
 * @author caiminming
 */
public class TestModule {
    @TimeCost(milliTime = 300L)
    public void testModuleMethod() {
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
