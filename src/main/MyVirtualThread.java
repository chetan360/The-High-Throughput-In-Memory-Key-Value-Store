package main;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyVirtualThread {
    // Virtual Threads: Mapped M:N (millions of virtual threads run on a tiny pool of underlying carrier OS threads)
    MyVirtualThread() {

    }

    public void testVirtualThreads() {
        // Creating an executor that spawns a brand new virtual thread per task
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            System.out.println("100,000 tasks started..");
            for (int i = 1; i <= 100000; i++) {
                executor.submit(()-> {
                    // Simulating a blocking network/(I/O) call
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return;
                    }
                });
            }
        }
        // The try-with-resources block automatically closes and waits for all 100k tasks to finish
        System.out.println("Finished 100,000 tasks effortlessly!");
    }
}
