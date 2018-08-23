package com.example.testmodule;

import com.dryseed.timecost.annotations.TimeCost;

/**
 * @author caiminming
 */
public class TestModule {
    @TimeCost(milliTime = 300L)
    public void testModuleMethod() {
        long i = 10000L;
        String a = "a";
        for (int n = 0; n < i; n++) {
            a += n;
        }
    }
}
