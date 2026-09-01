# Pushing the Limits for Platform and Virtual Threads in Java

[![Java](https://img.shields.io/badge/Java-21%2B-orange?logo=java)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
![Build](https://img.shields.io/badge/build-manual-lightgrey)

This benchmark explores how many platform and virtual threads Java can create and keep alive simultaneously, subject to available computing resources. The goal is to empirically demonstrate the scalability difference between OS-backed (platform) threads and JVM-managed virtual threads.

This project is part of the **Concurrent Programming** module at the [Federal University of Rio Grande do Norte (UFRN)](https://www.ufrn.br), Natal, Brazil.

## 📃 Description

The benchmark repeatedly attempts to create an increasing number of threads in batches of 500 (i.e., 500, 1,000, 1,500, and so on). Each thread in a batch stays alive by sleeping, so the whole batch is genuinely alive at once before the next, larger batch is attempted.

- **Platform threads** are expected to eventually fail, typically with a [`java.lang.OutOfMemoryError`](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/lang/OutOfMemoryError.html) or a failure to start a native OS thread once the operating system can no longer allocate resources for one more thread.
- **Virtual threads** are expected to scale far beyond what platform threads can sustain, and this benchmark is not expected to fail on its own. It is intended to be stopped manually (e.g., with Ctrl+C) after some time.

Either way the run ends (failure or manual interruption), it prints a summary reporting the largest batch successfully created and the total elapsed time.

## 📂 Repository Structure

```
.
├── doc/                           # Javadoc documentation
├── src
│   ├── BenchmarkLimits.java       # Creates and joins one batch of platform or virtual threads
│   ├── ThreadBatchCreator.java    # Functional interface shared by both batch creation methods
│   ├── LimitBenchmarkRunner.java  # Shared harness: runs increasing batches until failure or interruption
│   ├── MainPlatformThreads.java   # Entry point for the platform thread limit benchmark
│   ├── MainVirtualThreads.java    # Entry point for the virtual thread limit benchmark
│   ├── Task.java                  # Intentionally trivial task executed by each thread
└── README.md
```

## 🚀 Getting Started

Prerequisites:

- **JDK 21 or later** (due to the use of virtual threads)
- A terminal or IDE (IntelliJ, Eclipse, VS Code, etc.)

## ▶️ Running

Compile:

```bash
javac -d out src/limits/*.java
```

Run the platform threads benchmark:

```bash
java -cp out limits.MainPlatformThreads
```

Run the virtual threads benchmark:

```bash
java -cp out limits.MainVirtualThreads
```

⚠️ **Resource warning:** both benchmarks deliberately push thread creation to the point of failure (platform threads) or run for an extended, open-ended period (virtual threads). Run this on a machine that is not stressed (not a shared or production system) and expect noticeably high memory and CPU usage while it runs.

### Expected Output — Platform Threads

Each batch prints its result and the elapsed time so far, until creation fails and a final summary is printed.

```
Benchmarking the limits to create platform threads:
Started at 27/08/2026 14:02:10

Platform Threads: successfully created 500 platform threads (elapsed: 30.0 s)
Platform Threads: successfully created 1000 platform threads (elapsed: 60.1 s)
Platform Threads: successfully created 1500 platform threads (elapsed: 90.1 s)
Platform Threads: successfully created 2000 platform threads (elapsed: 120.2 s)
Platform Threads: successfully created 2500 platform threads (elapsed: 150.3 s)
Platform Threads: successfully created 3000 platform threads (elapsed: 180.5 s)
Platform Threads: successfully created 3500 platform threads (elapsed: 210.7 s)
Platform Threads: successfully created 4000 platform threads (elapsed: 240.9 s)
[241.2s][warning][os,thread] Failed to start thread "Unknown thread" - pthread_create failed (EAGAIN) for attributes: stacksize: 2048k, guardsize: 16k, detached.
[241.2s][warning][os,thread] Failed to start the native thread for java.lang.Thread "Thread-22065"

Platform threads benchmark failed for more than 4000 units after running for 241.226 s
```

**Note:** the `[os,thread]` warnings come from the JVM itself (not from the program's own output) and may print in a different order relative to the summary line since they go to a different output stream. This behavior is normal and not a benchmark bug.

### Expected Output — Virtual Threads

This run is expected to continue well beyond the point where the platform thread benchmark fails and does not stop on its own. Stop it manually (e.g., Ctrl+C) after some time; the summary is printed on exit regardless of how the run ends.

```
Benchmarking the limits to create virtual threads:
Started at 27/08/2026 14:10:03

Virtual Threads: successfully created 500 virtual threads (elapsed: 30.0 s)
Virtual Threads: successfully created 1000 virtual threads (elapsed: 60.1 s)
Virtual Threads: successfully created 1500 virtual threads (elapsed: 90.1 s)
...
Virtual Threads: successfully created 190000 virtual threads (elapsed: 22801.4 s)
^C
Virtual threads benchmark ran for 22801.482 s, created 190000 units, and did not stop
```

Benchmark duration depends on how long each thread stays alive (`ALIVE_TIME` in each `Main` class) and the batch step size (`STEP`). Both are configurable constants (see the Javadoc on `LimitBenchmarkRunner` and each `Main` class for details).

## 🤝 Contributing

Contributions are welcome! Fork this repository and submit a pull request 🚀

## 📜 License

This project is licensed under the [MIT License](LICENSE).
