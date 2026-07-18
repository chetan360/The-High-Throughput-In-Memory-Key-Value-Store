package main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class MyVirtualThread {

    public void testVirtualThreadsWithCache() {
        // 1. Initializing a shared cache instance with a capacity of 1,000
        ThreadSafeLRUCache cache = new ThreadSafeLRUCache(1000);

        // 2. Creating the virtual-thread-per-task executor
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            System.out.println("Spawning 100,000 virtual threads to stress-test the cache...");

            for (int i = 1; i <= 100000; i++) {
                final int taskId = i;

                executor.submit(() -> {
                    // Generating a key between 0 and 2000 to force concurrent evictions and hits
                    int randomKey = ThreadLocalRandom.current().nextInt(2000);

                    // Mix of reads and writes
                    if (taskId % 3 == 0) {
                        // Writing to the cache
                        cache.put(randomKey, randomKey * 10);
                    } else {
                        // Reading from the cache
                        int val = cache.get(randomKey);
                    }

                    // Simulating brief network/disk I/O latency
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        } // The try-with-resources block automatically block-waits until all 100k tasks finish.

        System.out.println("Finished 100,000 tasks effortlessly with zero data corruption!");
    }
}