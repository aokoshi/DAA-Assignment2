package daa;


public class Metrics {
    long steps;
    long moves;
    long comparisons;

    public void reset() {
        steps = 0;
        moves = 0;
        comparisons = 0;
    }
}
