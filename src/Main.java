import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Main {
    static HashMap<Integer, Integer> memory = new HashMap<>();
    static Scanner sc = new Scanner(System.in);

    static class Contributor extends Thread {
        int key, value;
        CountDownLatch latch;
        Contributor(int key, int value,  CountDownLatch latch) {
            this.key = key;
            this.value = value;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                latch.await();

                // ensures only ONE thread cantouch 'memoty' at a split second
                synchronized (memory) {
                    memory.put(key, value);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.print("Enter number of threads (Try 5000+): ");
        int n = sc.nextInt();
        Thread[] threads = new Thread[n];
        Random rand = new Random();
        CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < n; i++) {
            threads[i] = new Contributor(i, rand.nextInt(100),latch);
            threads[i].start();
        }
        System.out.println("Releasing all threads simultaneously...");
        latch.countDown();

        for (int i = 0; i < n; i++) {
            threads[i].join();
        }

        System.out.println("Expected Map Size: " + n);
        System.out.println("Actual Map Size: " + memory.size());
    }
}