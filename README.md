# Mini Redis (Concurrent In-Memory Cache Engine)
A high-throughput, thread-safe key-value storage engine built in Java,
demonstrating race condition reproduction, synchronized eviction policies,
and virtual thread optimization under 100,000 concurrent request streams.

### The "Why Redis is Popular" Section
- **Caching:** It temporarily stores frequently accessed data...
- **Speed:** Because it reads and writes from computer memory...
- **Rich Data Structures:** It goes beyond simple key-value storage...

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

**Here is the steps to fix:**

- Wrap the **memory.put(key, vaule)** inside **synchronized** block.
- This ensures only ONE thread can touch 'memory' at a split second

### 03. The Executor Framework (Pooled Platform Threads)

- Executor Framework: In production environments, manually creating new Thread() is highly discouraged.
- You should pass Runnable tasks directly into an ExecutorService thread pool to reuse system resources.
- Resource Reuse: Reuses existing threads, completely eliminating the high CPU overhead of thread creation.
- Throttling: Prevents your application from crashing due to Out-Of-Memory (OOM) errors by limiting the maximum number of concurrent threads.
- Lifecycle Management: Automatically handles thread crashes, tracking, and shutdown sequences

![Screenshot 3](project-screenshots/Creating%20Thread%20pool.png)

---

![Screenshot 4](project-screenshots/Testing%20thread%20pool.png)

---

![Screenshot 5](project-screenshots/Output%20of%20thread%20pool.png)

### 04. Project Loom & Virtual Threads

- Platform Threads: Mapped 1:1 to Operating System threads. They take up about 1 MB of memory each. A typical server can crash if it attempts to run more than a few thousand of them.
- Virtual Threads: Mapped M:N (millions of virtual threads run on a tiny pool of underlying carrier OS threads). They take up only a few hundred bytes of memory.
- When a traditional thread makes a database call or network request, it "blocks" and sits idle, wasting OS resources.
- When a Virtual Thread blocks on I/O, the JVM automatically detaches it from the underlying OS thread, allowing another virtual thread to execute. The blocked virtual thread safely parks in the JVM heap memory until the network or database response returns, making I/O-heavy applications massively scalable.
- Code Example: Spawning 100,000 Threads Instantly. Because virtual threads are so cheap, you do not pool them. You simply create a new one every single time you need it.
- Use code with caution.

![Screenshot 6](project-screenshots/Testing%20Virtual%20Threads.png)

## 🚀 Advanced Optimization: Fine-Grained Concurrency & LRU Eviction

### 1. Read-Write Mutex (`ReentrantReadWriteLock`)

* **Virtual Thread Optimization:** Replaced coarse global synchronization with a fine-grained read-write lock mechanism. Unlike traditional `synchronized` blocks that permanently "pin" virtual threads to physical carrier OS threads, this framework allows virtual threads to gracefully unmount from the carrier thread during lock contention, maximizing CPU efficiency.


* **Concurrency Architecture:** Guarantees absolute mutual exclusion for writers while permitting infinite simultaneous reader threads, drastically minimizing lock contention under high-read infrastructure workloads.



### 2. Thread-Safe LRU Cache Eviction Policy

* **Constant Time Bounds:** Pairs a standard Java `HashMap` with a custom structural Doubly-Linked List to enforce strict `O(1)` lookups and eviction ejections under hard memory limits.


* **Pointer Mutation Protection:** Because reading an LRU cache requires moving the accessed node to the head of the list, the `get()` operation fundamentally alters the underlying pointer layout. This architecture safely wraps both `get` and `put` methods in a write lock to prevent race conditions during concurrent pointer updates.
```bash
HEAD ◄──► [Node C] ◄──► [Node A] ◄──► [Node B] ◄──► TAIL
      (Most Recent)                (Evict This)

After get("A"):
HEAD ◄──► [Node A] ◄──► [Node C] ◄──► [Node B] ◄──► TAIL
```
---

### 📊 Concurrency Performance Metrics

A rigorous stress test was executed using a `newVirtualThreadPerTaskExecutor` framework to validate structural integrity and throughput under massive thread saturation:

| Metric | Workload Configuration | Performance Result |
| --- | --- | --- |
| **Concurrent Request Streams** | 100,000 Virtual Threads | 100,000 Tasks handled effortlessly |
| **Cache Capacity Bound** | 1,000 Hard Limit | Managed dynamically via LRU evictions|
| **Average Execution Time** | 100k Tasks (with 10ms I/O sleep) | ~3030 ms total processing window |
| **Structural Anomalies** | 50,000+ Parallel Mutations| **0** Silent Data Corruption cases detected |
---
![Screenshot 7](project-screenshots/Deling%20with%20100000%20virtual%20threads%20with%20LRU%20cache.png)

---
## 🚀 How to Run Locally

```bash
# Prerequisites: Java 21+ required (Virtual Threads need JDK 21)

# 1. Clone the repository
git clone https://github.com/chetan360/Mini-Redis

# 2. Open in IntelliJ IDEA

# 3. Run specific demonstrations:
#    → Race Condition Demo  : main.Consumer.java
#    → Thread Pool Demo     : main.MyThreadPool.java  
#    → Virtual Thread Demo  : main.MyVirtualThread.java
#    → LRU Benchmark        : main.Main.java
#    → LRU Implementation   : main.ThreadSafeLRUCache

````

## 📝 License
```bash
MIT License — feel free to fork and build upon this project.
```

**Built with ❤️ using Java**
