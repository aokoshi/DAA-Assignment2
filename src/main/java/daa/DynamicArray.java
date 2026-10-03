package daa;

public class DynamicArray implements IntSequence {
    private int[] data = new int[8];
    private int size;
    private final Metrics metrics = new Metrics();

    @Override
    public void add(int value) {
        add(size, value);
    }

    @Override
    public void add(int index, int value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index: " + index);
        }
        ensureCapacity();
        for (int i = size; i > index; i--) {
            data[i] = read(i - 1);
            metrics.moves++;
        }
        data[index] = value;
        size++;
    }

    @Override
    public int remove(int index) {
        checkIndex(index);
        int removed = read(index);
        
        for (int i = index; i < size - 1; i++) {
            data[i] = read(i + 1);
            metrics.moves++;
        }
        size--;
        return removed;
    }

    @Override
    public int get(int index) {
        checkIndex(index);
        return read(index);
    }

    @Override
    public boolean contains(int value) {
        
        for (int i = 0; i < size; i++) {
            metrics.comparisons++;
            if (read(i) == value) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Metrics metrics() {
        return metrics;
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

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index);
        }
    }
}
