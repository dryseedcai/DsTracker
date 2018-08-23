package com.dryseed.timecost;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String s = "com/dryseed/timecost/TimeCostCanary$2";
        System.out.print(s.contains("/"));
        System.out.print(s.replace("/", "."));
    }

}