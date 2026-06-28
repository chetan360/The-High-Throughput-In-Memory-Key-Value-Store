package main;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Consumer c = new Consumer();
        c.testSilentDataCorruption();

        MyThreadPool tp = new MyThreadPool();
        tp.testThreadPool();

        MyVirtualThread vt = new MyVirtualThread();
        vt.testVirtualThreads();
    }
}