package main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyThreadPool {
    MyThreadPool() {

    }

    public void testThreadPool() {
        // 1. Create a pool with a fixed maximum of 4 platform threads
        // Platform threads: It maps in a strict 1:1 relationship with an OS
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // 2. Submit 100 tasks to the pool
        for (int i=1; i<=100; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.println("Thread " + taskId + " executed by " + Thread.currentThread().getName());
            });
        }

        // 3. Gracefully shut down the pool when done
        executor.shutdown();
    }
}
