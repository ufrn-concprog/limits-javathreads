import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Repeatedly attempts to create an increasing number of threads (platform or
 * virtual, depending on the {@link ThreadBatchCreator} supplied), in
 * successive batches, until creation fails or the run is stopped manually.
 *
 * <p>Reports the largest batch successfully created and the total elapsed
 * time whenever the run ends — whether that is due to an actual failure
 * (e.g., {@link OutOfMemoryError}) or a manual interruption (e.g., Ctrl+C),
 * via a JVM shutdown hook.
 */
public class LimitBenchmarkRunner {
    private final String label;
    private final ThreadBatchCreator creator;
    private final Runnable task;
    private final int step;

    private final AtomicInteger lastSuccessful = new AtomicInteger(0);
    private volatile boolean failed = false;
    private final long experimentStart = System.nanoTime();

    /**
     * @param label   Descriptive name for this benchmark used in all console output
     * @param creator The batch-creation method to stress-test
     * @param task    The task each created thread should execute
     * @param step    Number of additional threads attempted in each successive batch
     */
    public LimitBenchmarkRunner(String label, ThreadBatchCreator creator, Runnable task, int step) {
        this.label = label;
        this.creator = creator;
        this.task = task;
        this.step = step;
    }

    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::report));

        System.out.println("Benchmarking the limits to create " + label.toLowerCase() + ":");
        System.out.println("Started at " + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n");

        int maxThreads = step;
        while (true) {
            try {
                creator.createBatch(label, task, maxThreads);
                lastSuccessful.set(maxThreads);

                double elapsedSoFar = (System.nanoTime() - experimentStart) / 1e9;
                System.out.printf(" (elapsed: %.3f s)%n", elapsedSoFar);

                maxThreads += step;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (OutOfMemoryError e) {
                failed = true;
                break;
            }
        }

        System.out.println("Execution terminated.");
    }

    /**
     * Prints a summary of the benchmark's outcome. Wording depends on whether
     * the run actually failed or was still running when stopped.
     */
    private void report() {
        double elapsedSeconds = (System.nanoTime() - experimentStart) / 1e9;
        if (failed) {
            System.out.printf("%n%s benchmark failed for more than %d units "
                    + "after running for %.3f s%n", label, lastSuccessful.get(), elapsedSeconds);
        } else {
            System.out.printf("%n%s benchmark ran for %.3f s, created %d units, "
                    + "and did not stop%n", label, elapsedSeconds, lastSuccessful.get());
        }
    }
}
