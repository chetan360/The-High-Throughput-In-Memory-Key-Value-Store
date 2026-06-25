# The High-Throughput In-Memory Key-Value Store

The Core System: Multiple client threads are trying to read, write, and update keys (strings, lists, or expires) at the same time.
Where Concurrency/Synchronization Shines: \* If two threads write to the exact same key simultaneously, your system will crash or corrupt without proper synchronization.

### Concurrent In-Memory Key-Value Storage Engine

A lightweight, high-throughput, thread-safe in-memory caching engine built in Java. This project demonstrates the hidden dangers of **Silent Data Corruption** when standard data structures are exposed to high-concurrency environments, and provides a structural evolution toward safe, optimized thread synchronization.

### 🔬 The Core Problem: Silent Data Corruption

When building applications designed to handle large-scale, parallel data streams (e.g., distributed caching systems, real-time logging infrastructure), data corruption often happens without throwing a single runtime exception or crash.

### Anatomy of a Race Condition

When multiple threads execute HashMap.put() at the exact same microsecond, they concurrently alter internal memory references. If two threads target the same bucket index simultaneously, one thread's write operation can completely overwrite the other's pointer. The JVM executes valid bytecode instructions, meaning the application stays alive, but data silently vanishes.

### 🛠️ Simulation Architecture

To reproduce this race condition reliably, this engine uses a CountDownLatch acting as a synchronized starting gate. All threads are initialized, held at a processing barrier, and released simultaneously to maximize the collision rate on the underlying storage bucket array.

## Project Screenshots 🖼️

### 01. Silent Data Corruption

![Screenshot 1](project-screenshots/Silent%20Data%20Corruption.png)

**Here is the step-by-step chronology of how those 25 elements vanished:**

- Thread 512 and Thread 1024 both try to insert a key into Bucket #42 at the exact same moment.

- Thread 512 reads Bucket #42 and sees it is empty. It prepares to link its new data node.

- Before Thread 512 can save its update, the CPU switches contexts. Thread 1024 reads Bucket #42. It also sees that it is empty because Thread 512 hasn't committed its link yet.

- Thread 512 writes its node to Bucket #42.

- Thread 1024 writes its node to Bucket #42 right on top of it, completely overwriting Thread 512's pointer.

### 02. Fixing the Leak

![Screenshot 2](project-screenshots/Silent%20Data%20Corruption%20Handled.png)

## 🚀 Advanced Optimization: Fine-Grained Concurrency & LRU Eviction (Currently Working on)

- 1. Read-Write Mutex (ReentrantReadWriteLock)
- 2. Thread-Safe LRU Cache Eviction Policy

## 🚀 How to Run Locally

```bash
# 1. clone repository
https://github.com/chetan360/The-High-Throughput-In-Memory-Key-Value-Store

# 2. Open in IntelliJ IDEA (or your preferred IDE).

# 3. Compile and Execute Main.java.
```

## 📝 License

Feel free to use this template for your own portfolio. Customize it to match your personal brand!

---

**Built with ❤️ using Java**

```

```
