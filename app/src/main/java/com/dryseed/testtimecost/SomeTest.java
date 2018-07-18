package com.dryseed.testtimecost;

public class SomeTest implements Runnable {
    @Override
    public void run() {
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int someM() {
        long i = 10000L;
        String a = "a";
        for (int n = 0; n < i; n++) {
            a += n;
        }
        return 9999;
    }
}
