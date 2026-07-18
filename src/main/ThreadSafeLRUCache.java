package main;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeLRUCache {

    class Node {
        int key, value;
        Node prev, next;
        Node(int key, int value) { this.key = key; this.value = value; }
    }

    private final int capacity;
    private final HashMap<Integer, Node> map;
    private final Node head, tail;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ThreadSafeLRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        // Must use write lock because standard reading alters the underlying pointer layout
        lock.writeLock().lock();
        try {
            if (!map.containsKey(key)) return -1;
            Node node = map.get(key);
            moveToHead(node);
            return node.value;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void put(int key, int value) {
        lock.writeLock().lock();
        try {
            if (map.containsKey(key)) {
                Node node = map.get(key);
                node.value = value;
                moveToHead(node);
            } else {
                if (map.size() >= capacity) {
                    Node lru = tail.prev;
                    removeNode(lru);
                    map.remove(lru.key);
                }
                Node newNode = new Node(key, value);
                addNode(newNode);
                map.put(key, newNode);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    // --- Helper Methods (Assumes caller holds the appropriate lock) ---
    private void addNode(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addNode(node);
    }
}