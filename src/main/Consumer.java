package main;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Consumer {
    static HashMap<Integer, Integer> memory = new HashMap<>();
    static Scanner sc = new Scanner(System.in);
    static CountDownLatch latch = new CountDownLatch(1);
    static Random rand = new Random();

    Consumer() {

    }

    public void testSilentDataCorruption() {
        System.out.print("Enter number of threads (Try 5000+): ");
        int n = sc.nextInt();
        Thread[] threads = new Thread[n];
        for (int i = 0; i < n; i++) {
            final int key = i;
            final int value = rand.nextInt(100);
            //thread using Lambda expression
            threads[i] = new Thread(() -> {
                try {
                    latch.await();

                    // ensures only ONE thread cantouch 'memory' at a split second
                    synchronized (memory) {
                        memory.put(key, value);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threads[i].start();
        }
        System.out.println("Releasing all threads simultaneously...");
        latch.countDown();

        for (int i = 0; i < n; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Expected Map Size: " + n);
        System.out.println("Actual Map Size: " + memory.size());
    }
}
