import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for stress-testing how many platform or virtual threads can be
 * created and kept alive simultaneously, until the JVM can no longer sustain
 * the requested number — typically failing with {@link OutOfMemoryError} when
 * creating platform threads.
 */
public class BenchmarkLimits {
    /** Default constructor */
    public BenchmarkLimits() {}

    /**
     * Creates and starts {@code numWorkers} platform (OS-backed) threads, each
     * running {@code task}, then waits for all of them to finish.
     *
     * <p>Threads are created as daemons so that, if this method throws partway
     * through a batch (e.g., due to {@link OutOfMemoryError}), the JVM is not
     * blocked from exiting by threads left sleeping in the background. They
     * are simply discarded when the JVM shuts down.
     *
     * @param label      Label used only in the printout
     * @param task       The task each thread should execute
     * @param numWorkers Number of platform threads to create
     * @throws InterruptedException if interrupted while waiting for the batch to finish
     * @throws OutOfMemoryError     if the JVM/OS cannot allocate resources for one more platform thread
     */
    public static void benchmarkPlatformThreads(String label, Runnable task, int numWorkers)
            throws InterruptedException, OutOfMemoryError {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numWorkers; i++) {
            Thread t = new Thread(task);
            t.setDaemon(true);
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.printf("%s: successfully created %d platform threads%n", label, numWorkers);
    }

    /**
     * Creates and starts {@code numWorkers} virtual threads, each running
     * {@code task}, then waits for all of them to finish.
     *
     * <p>Virtual threads are always daemon threads by design, so no explicit
     * daemon flag is needed here.
     *
     * @param label      Label used only in the printout
     * @param task       The task each thread should execute
     * @param numWorkers Number of virtual threads to create
     * @throws InterruptedException if interrupted while waiting for the batch to finish
     * @throws OutOfMemoryError     if the JVM cannot allocate resources for one more virtual thread — not expected
     *                              to happen in practice, but declared for symmetry with the platform thread benchmark
     */
    public static void benchmarkVirtualThreads(String label, Runnable task, int numWorkers)
            throws InterruptedException, OutOfMemoryError {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numWorkers; i++) {
            Thread t = Thread.ofVirtual().unstarted(task);
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.printf("%s: successfully created %d virtual threads%n", label, numWorkers);
    }
}
