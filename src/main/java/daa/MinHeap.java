package daa;


public class MinHeap {
    private int[] data = new int[8];
    private int size;
    private final Metrics metrics = new Metrics();

    public void insert(int value) {
        ensureCapacity();
        int index = size;
        data[size++] = value;
        while (index > 0) {
            int parent = (index - 1) / 2;
            int parentValue = read(parent);
            int childValue = read(index);
            metrics.comparisons++;
            if (parentValue <= childValue) {
                break;
            }
            data[parent] = childValue;
            data[index] = parentValue;
            metrics.moves += 2;
            index = parent;
        }
    }

    public int peekMin() {
        checkNotEmpty();
        return read(0);
    }

    public int extractMin() {
        checkNotEmpty();
        int result = read(0);
        size--;
        if (size == 0) {
            return result;
        }
        data[0] = read(size);
        metrics.moves++;
        int index = 0;
        while (2 * index + 1 < size) {
            int child = 2 * index + 1;
            int childValue = read(child);
            int right = child + 1;
            if (right < size) {
                int rightValue = read(right);
                metrics.comparisons++;
                if (rightValue < childValue) {
                    child = right;
                    childValue = rightValue;
                }
            }
            int currentValue = read(index);
            metrics.comparisons++;
            if (currentValue <= childValue) {
                break;
            }
            data[index] = childValue;
            data[child] = currentValue;
            metrics.moves += 2;
            index = child;
        }
        return result;
    }

    public Metrics metrics() {
        return metrics;
    }

    
    boolean hasHeapProperty() {
        for (int child = 1; child < size; child++) {
            if (data[(child - 1) / 2] > data[child]) {
                return false;
            }
        }
        return true;
    }

    private int read(int index) {
        metrics.steps++;
        return data[index];
    }

    private void ensureCapacity() {
        if (size == data.length) {
            int[] larger = new int[data.length * 2];
            for (int i = 0; i < size; i++) {
                larger[i] = read(i);
                metrics.moves++;
            }
            data = larger;
        }
    }

    private void checkNotEmpty() {
        if (size == 0) {
            throw new IllegalStateException("heap is empty");
        }
    }
}
