# DAA Assignment 2 — Data Structures

Student: **Yelnur Pashanov**  
Group: **SE-2523**  
GitHub repository: https://github.com/aokoshi/DAA-Assignment2

Java 17 Maven project for the required Week 4 structures and workloads.
`DynamicArray`, `MyLinkedList` and `MinHeap` store primitive `int` values.
The implementations use their own arrays/nodes; Java collections appear only in tests.

## Build and test

Use JDK 17 and Maven, with `java`, `javac` and `mvn` on your PATH.
Both are already available on the computer where this project was prepared.
In PowerShell, enable those existing tools for this terminal only:

```powershell
$env:JAVA_HOME = 'C:\Users\kirit\.jdks\ms-17.0.20.1'
$assignmentMaven = 'C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.3\plugins\maven-plugin\lib\maven3\bin'
$env:Path = "$env:JAVA_HOME\bin;$assignmentMaven;$env:Path"
```

On another computer, use its own JDK 17/Maven paths. From this project directory:

```text
mvn test
```

This compiles the project and runs the JUnit 5 tests. The POM fixes the JUnit and
plugin versions. Maven needs internet for dependencies on the first run; later
runs can use `mvn -o test` if all dependencies are cached. The submitted execution
log is `results/test-output.txt`; fresh detailed results appear in
`target/surefire-reports/`.

## Run the benchmark

After compiling with the command above, run from the project directory:

```text
java -cp target/classes daa.Benchmark
```

This regenerates `results/results.csv`, `results/runs.csv` and
`results/environment.txt`. It runs all 36 cases (9 combinations at each of
100, 1,000, 10,000 and 100,000 elements). Each case uses a fresh structure for
one discarded warm-up and five measured runs. The summary contains the median
of those five times; the raw CSV records every run, including the warm-up.
Counters must match across the five repetitions or execution fails.

At each size, `new Random(42)` generates the input and queries once, and the same
arrays are reused by both structures and all repetitions. Values stored initially
are nonnegative. In W2, 500 queries select existing values and 500 are negative,
so the second half is guaranteed absent. The queries alternate hit and miss.

| Workload | Operations in the timed interval |
| --- | --- |
| W1 | 10,000 random `get(index)` calls |
| W2 | 1,000 `contains(value)` calls |
| W3 head | 1,000 insertions at 0, then 1,000 removals at 0 |
| W3 middle | 1,000 insertions at fixed original `n / 2`, then 1,000 removals there |
| W4 | Insert all n values, then extract all n values from MinHeap |

Initial filling is outside timing and counters for W1–W3. W4 includes all inserts,
resizes and extractions. Its output buffer is allocated before timing; writing
the returned values into it is timed harness work but is not counted as structure
operations. Sorted-order checking is after timing. W1/W3 returned-value sums and
W2 hit counts are checked after timing. A volatile checksum prevents unused
results from being discarded. These are instrumented JVM wall-clock measurements,
not a claim of hardware-independent timings. No forced garbage collection is used.

## Counter definitions

- **steps:** one array-cell read, or reading/following one list `next` link;
  following the last link to `null` counts too. List value reads are not steps
  under the assignment's definition. Thus array `get(i)` costs one step and list
  `get(i)` costs i steps.
- **moves:** one existing element copied during resizing, shifted within an array,
  or relocated by a heap operation; swapping two elements counts two moves.
  Every assignment to list `head`, `tail` or `next` is one link update. A new node's
  implicit initial `next = null` is excluded. The first placement of a new integer
  in an array is not a shift.
- **comparisons:** one comparison between stored integer values, or a stored value
  and the searched value. Index bounds, loop conditions and null checks do not count.

All counters are incremented inside the structure methods. They exclude benchmark
input/output-array operations, correctness checks and test-only heap inspection.
`Metrics.reset()` clears counters without changing the stored values.

Both array structures start at capacity 8, double when full and do not shrink.
The singly linked list keeps a tail pointer for constant-time append.
`IntSequence` supplies the same five required methods for the array and list.

## Results and local report

The measured CSV files, environment details and four PNG charts are included in
`results/`. They come from the actual benchmark run performed before publication.
The publication copy removes Java comments only; the implementation is unchanged.
The 40 JUnit tests were rerun on this copy; the new log is `results/test-output.txt`.
All nine compiled production class files were also verified byte-for-byte equal
to the original measured build, so the retained benchmark results use the same code.

The full report and its generation script are retained locally with the assignment
materials at the student's request. They are not included in this public repository
or any of its commits. The local report contains the complexity table,
loop-invariant proofs, charts and discussion required for submission.

## Git and submission

The repository retains `main`, `feature/array`, `feature/list`, `feature/heap`
and `feature/metrics`; the completed publication version is tagged `v1.0`.
This is a new publication history importing the existing tested assignment in
separate steps. It does not claim to reproduce the original development history.
Commits use their actual creation dates and identify Codex AI Assistant.
No Moodle submission has been made. The submission archive should include the
separately retained local report and use the name
`DAA_Assignment2_Yelnur_Pashanov_SE-2523.zip`.

AI assistance: OpenAI Codex assisted with implementation, tests, benchmark setup
and documentation. All included execution results come from actual local runs.
