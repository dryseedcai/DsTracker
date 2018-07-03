package com.dryseed.timecost;

import java.util.concurrent.ThreadFactory;

/**
 * This is intended to only be used with a single thread executor.
 * @author caiminming
 */
final class SingleThreadFactory implements ThreadFactory {

    private final String threadName;

    SingleThreadFactory(String threadName) {
        this.threadName = "TimeCost-" + threadName;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(runnable, threadName);
    }
}
