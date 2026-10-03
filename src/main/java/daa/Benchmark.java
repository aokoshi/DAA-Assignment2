package daa;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;


public class Benchmark {
    private static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    private static final int RUNS = 5;
    private static volatile long resultSink;

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("results"));
        saveEnvironment();
        try (PrintWriter summary = writer("results/results.csv");
             PrintWriter raw = writer("results/runs.csv")) {
            summary.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");
            raw.println("workload,variant,structure,n,run,time_ns,steps,moves,comparisons");
            for (int n : SIZES) {
                Input input = new Input(n);
                for (int kind = 0; kind < 2; kind++) {
                    measure("W1", "-", kind, input, summary, raw);
                    measure("W2", "-", kind, input, summary, raw);
                    measure("W3", "head", kind, input, summary, raw);
                    measure("W3", "middle", kind, input, summary, raw);
                }
                measure("W4", "-", 2, input, summary, raw);
            }
        }
        System.out.println("Finished 36 cases: each has 1 warm-up + 5 measured runs.");
        System.out.println("W2 checked 500 hits; W3 checked removed values; W4 checked sorted output.");
    }

    private static void measure(String workload, String variant, int kind, Input input,
                                PrintWriter summary, PrintWriter raw) {
        String structure = kind == 0 ? "DynamicArray" : kind == 1 ? "MyLinkedList" : "MinHeap";
        long[] times = new long[RUNS];
        Result first = null;
        for (int run = 0; run <= RUNS; run++) {
            Result result = runCase(workload, variant, kind, input);
            String runName = run == 0 ? "warmup" : Integer.toString(run);
            raw.println(workload + "," + variant + "," + structure + "," + input.data.length
                    + "," + runName + "," + result.nanos + "," + result.steps + ","
                    + result.moves + "," + result.comparisons);
            if (run > 0) {
                times[run - 1] = result.nanos;
                if (first == null) {
                    first = result;
                } else if (first.steps != result.steps || first.moves != result.moves
                        || first.comparisons != result.comparisons) {
                    throw new IllegalStateException("Counters must be identical across repetitions");
                }
            }
        }
        
        for (int i = 1; i < times.length; i++) {
            long value = times[i];
            int j = i - 1;
            while (j >= 0 && times[j] > value) {
                times[j + 1] = times[j];
                j--;
            }
            times[j + 1] = value;
        }
        double medianMs = times[RUNS / 2] / 1_000_000.0;
        String row = workload + "," + variant + "," + structure + "," + input.data.length
                + "," + medianMs + "," + first.steps + "," + first.moves + "," + first.comparisons;
        summary.println(row);
        summary.flush();
        raw.flush();
        System.out.println(row);
    }

    private static Result runCase(String workload, String variant, int kind, Input input) {
        if (kind == 2) {
            return runHeap(input);
        }
        IntSequence sequence = kind == 0 ? new DynamicArray() : new MyLinkedList();
        for (int value : input.data) {
            sequence.add(value);
        }
        sequence.metrics().reset();
        long checksum = 0;
        long start = System.nanoTime();
        if (workload.equals("W1")) {
            for (int index : input.indices) {
                checksum += sequence.get(index);
            }
        } else if (workload.equals("W2")) {
            for (int query : input.queries) {
                if (sequence.contains(query)) {
                    checksum++;
                }
            }
        } else {
            int index = variant.equals("head") ? 0 : input.data.length / 2;
            for (int value : input.insertions) {
                sequence.add(index, value);
            }
            for (int i = 0; i < input.insertions.length; i++) {
                checksum += sequence.remove(index);
            }
        }
        long elapsed = System.nanoTime() - start;
        resultSink = checksum;
        long expected = workload.equals("W1") ? input.accessSum
                : workload.equals("W2") ? 500 : input.insertionSum;
        if (checksum != expected) {
            throw new IllegalStateException("Unexpected workload result for " + workload);
        }
        return new Result(elapsed, sequence.metrics());
    }

    private static Result runHeap(Input input) {
        MinHeap heap = new MinHeap();
        int[] output = new int[input.data.length];
        long start = System.nanoTime();
        for (int value : input.data) {
            heap.insert(value);
        }
        for (int i = 0; i < output.length; i++) {
            output[i] = heap.extractMin();
        }
        long elapsed = System.nanoTime() - start;
        long checksum = 0;
        for (int i = 0; i < output.length; i++) {
            if (i > 0 && output[i - 1] > output[i]) {
                throw new IllegalStateException("Heap output is not sorted");
            }
            checksum += output[i];
        }
        if (checksum != input.dataSum) {
            throw new IllegalStateException("Heap values changed");
        }
        resultSink = checksum;
        return new Result(elapsed, heap.metrics());
    }

    private static class Input {
        final int[] data;
        final int[] indices = new int[10_000];
        final int[] queries = new int[1_000];
        final int[] insertions = new int[1_000];
        long accessSum;
        long insertionSum;
        long dataSum;

        Input(int n) {
            Random random = new Random(42);
            data = new int[n];
            for (int i = 0; i < n; i++) {
                data[i] = random.nextInt(1_000_000_000);
                dataSum += data[i];
            }
            for (int i = 0; i < indices.length; i++) {
                indices[i] = random.nextInt(n);
                accessSum += data[indices[i]];
            }
            for (int i = 0; i < queries.length; i++) {
                queries[i] = i % 2 == 0 ? data[random.nextInt(n)]
                        : -1 - random.nextInt(1_000_000_000);
            }
            for (int i = 0; i < insertions.length; i++) {
                insertions[i] = random.nextInt(1_000_000_000);
                insertionSum += insertions[i];
            }
        }
    }

    private static class Result {
        final long nanos;
        final long steps;
        final long moves;
        final long comparisons;

        Result(long nanos, Metrics metrics) {
            this.nanos = nanos;
            steps = metrics.steps;
            moves = metrics.moves;
            comparisons = metrics.comparisons;
        }
    }

    private static PrintWriter writer(String filename) throws IOException {
        return new PrintWriter(Files.newBufferedWriter(Path.of(filename)));
    }

    private static void saveEnvironment() throws IOException {
        try (PrintWriter out = writer("results/environment.txt")) {
            out.println("Java version: " + System.getProperty("java.version"));
            out.println("Java vendor: " + System.getProperty("java.vendor"));
            out.println("JVM: " + System.getProperty("java.vm.name"));
            out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
            out.println("Architecture: " + System.getProperty("os.arch"));
            out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
            out.println("Maximum JVM heap bytes: " + Runtime.getRuntime().maxMemory());
            out.println("Seed: new Random(42) for each n; shared input arrays across structures/runs");
            out.println("Per case: one discarded warm-up, then five measured fresh structures");
            out.println("Timer: System.nanoTime; counters enabled during timing; no forced GC");
            out.println("W1-W3 initial fill excluded; W4 inserts and extracts included");
            out.println("W4 output buffer allocated and sorted-order validation performed outside timer");
        }
    }
}
