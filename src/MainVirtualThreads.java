/**
 * Pushes virtual thread creation to its limit, using {@link LimitBenchmarkRunner}.
 * Expected to keep running well beyond what platform threads can sustain — intended
 * to be stopped manually (e.g., Ctrl+C) after an observation period.
 */
public class MainVirtualThreads {
    private static final int STEP = 500;
    private static final int ALIVE_TIME = 30000;

    public static void main(String[] args) {
        Runnable task = () -> {
            try {
                Thread.sleep(ALIVE_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        new LimitBenchmarkRunner("Virtual Threads",
                BenchmarkLimits::benchmarkVirtualThreads, task, STEP).run();
    }
}
