/**
 * A method that creates and joins a batch of threads (platform or virtual),
 * as implemented by {@link BenchmarkLimits}. The role of this interface is
 * to allow running a generic limit-pushing benchmark without caring which
 * kind of thread it creates.
 */
public interface ThreadBatchCreator {
    /**
     * Creates, starts, and joins {@code numWorkers} threads, each running
     * {@code task}.
     *
     * @param label      Label used only in the printout
     * @param task       The task each thread should execute
     * @param numWorkers Number of threads to create in this batch
     * @throws InterruptedException if interrupted while waiting for the batch to finish
     * @throws OutOfMemoryError     if the JVM/OS cannot allocate resources for one more thread
     */
    void createBatch(String label, Runnable task, int numWorkers)
            throws InterruptedException, OutOfMemoryError;
}
