/**
 * Pushes platform thread creation to its limit, using {@link LimitBenchmarkRunner}.
 * Expected to fail (typically with {@link OutOfMemoryError}) after a few thousand
 * threads.
 */
public class MainPlatformThreads {
    private static final int STEP = 500;
    private static final int ALIVE_TIME = 60000;

    public static void main(String[] args) {
        Runnable task = () -> {
            try {
                Thread.sleep(ALIVE_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        new LimitBenchmarkRunner("Platform Threads",
                BenchmarkLimits::benchmarkPlatformThreads, task, STEP).run();
    }
}
