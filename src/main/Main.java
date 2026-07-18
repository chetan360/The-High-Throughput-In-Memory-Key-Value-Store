package main;

public class Main {
    public static void main(String[] args) throws InterruptedException {
//        Consumer c = new Consumer();
//        c.testSilentDataCorruption();

//        MyThreadPool tp = new MyThreadPool();
//        tp.testThreadPool();

        System.out.println("=== Launching Google-Scale Concurrency Stress Test ===");

        // 1. Instantiating virtual thread stress-testing framework
        MyVirtualThread stressTester = new MyVirtualThread();

        // 2. Trying to run the workload tracking 100,000 concurrent operations
        long startTime = System.currentTimeMillis();
        stressTester.testVirtualThreadsWithCache();
        long endTime = System.currentTimeMillis();

        // 3. Printing out the total execution metrics
        System.out.println("=====================================================");
        System.out.println("Execution Time: " + (endTime - startTime) + " ms");
        System.out.println("=== Test Completed Successfully ===");
    }
}