package org.skyfish.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Multithreading {

    private static final AtomicInteger counter = new AtomicInteger(0);

    public static ThreadPoolExecutor POOL = new ThreadPoolExecutor(10, 30,
            0L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            r -> new Thread(r, "SkyFish Thread " + counter.incrementAndGet()));

    public static void runAsync(Runnable runnable) {
        POOL.execute(runnable);
    }

}