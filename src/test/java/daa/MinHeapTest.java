package daa;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MinHeapTest {
    @Test
    void emptyHeapRejectsPeekAndExtract() {
        MinHeap heap = new MinHeap();
        assertTrue(heap.hasHeapProperty());
        assertThrows(IllegalStateException.class, heap::peekMin);
        assertThrows(IllegalStateException.class, heap::extractMin);
    }

    @Test
    void oneElementCanBeExtractedAndHeapReused() {
        MinHeap heap = new MinHeap();
        heap.insert(17);
        assertTrue(heap.hasHeapProperty());
        assertEquals(17, heap.peekMin());
        assertEquals(17, heap.peekMin());
        assertEquals(17, heap.extractMin());
        assertTrue(heap.hasHeapProperty());
        assertThrows(IllegalStateException.class, heap::peekMin);
        heap.insert(-7);
        assertTrue(heap.hasHeapProperty());
        assertEquals(-7, heap.extractMin());
        assertTrue(heap.hasHeapProperty());
    }

    @Test
    void duplicatesAndExtremeIntsAreExtractedInOrder() {
        checkSortedExtraction(new int[] {
            Integer.MAX_VALUE, 0, -1, Integer.MIN_VALUE,
            7, 7, Integer.MIN_VALUE, Integer.MAX_VALUE, 0
        });
    }

    @Test
    void ascendingInputPreservesHeapProperty() {
        int[] values = new int[257];
        for (int i = 0; i < values.length; i++) {
            values[i] = i;
        }
        checkSortedExtraction(values);
    }

    @Test
    void descendingInputPreservesHeapProperty() {
        int[] values = new int[257];
        for (int i = 0; i < values.length; i++) {
            values[i] = values.length - i;
        }
        checkSortedExtraction(values);
    }

    @Test
    void randomInputProducesSortedOutputAcrossResizes() {
        Random random = new Random(42);
        int[] values = new int[2000];
        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextInt();
        }
        checkSortedExtraction(values);
    }

    @Test
    void randomMixedOperationsMatchPriorityQueue() {
        MinHeap actual = new MinHeap();
        PriorityQueue<Integer> expected = new PriorityQueue<>();
        Random random = new Random(123);
        for (int operation = 0; operation < 5000; operation++) {
            if (expected.isEmpty() || random.nextBoolean()) {
                int value = random.nextInt(101) - 50;
                actual.insert(value);
                expected.add(value);
            } else {
                assertEquals(expected.remove().intValue(), actual.extractMin());
            }
            assertTrue(actual.hasHeapProperty(), "operation " + operation);
            if (!expected.isEmpty()) {
                assertEquals(expected.element().intValue(), actual.peekMin());
            }
        }
        while (!expected.isEmpty()) {
            assertEquals(expected.remove().intValue(), actual.extractMin());
            assertTrue(actual.hasHeapProperty());
        }
        assertThrows(IllegalStateException.class, actual::extractMin);
    }

    @Test
    void peekCountsOneReadWithoutChangingHeap() {
        MinHeap heap = new MinHeap();
        heap.insert(20);
        assertTrue(heap.hasHeapProperty());
        heap.insert(10);
        assertTrue(heap.hasHeapProperty());
        heap.metrics().reset();
        assertEquals(10, heap.peekMin());
        assertEquals(1, heap.metrics().steps);
        assertEquals(0, heap.metrics().moves);
        assertEquals(0, heap.metrics().comparisons);
        assertTrue(heap.hasHeapProperty());
        assertEquals(10, heap.extractMin());
        assertTrue(heap.hasHeapProperty());
        assertEquals(20, heap.extractMin());
        assertTrue(heap.hasHeapProperty());
    }

    @Test
    void bubbleUpCountsComparisonReadsAndBothMovedElements() {
        MinHeap heap = new MinHeap();
        heap.insert(20);
        assertTrue(heap.hasHeapProperty());
        heap.metrics().reset();
        heap.insert(10);
        assertTrue(heap.hasHeapProperty());
        assertEquals(2, heap.metrics().steps);
        assertEquals(2, heap.metrics().moves);
        assertEquals(1, heap.metrics().comparisons);
        assertEquals(10, heap.peekMin());
    }

    @Test
    void bubbleDownCountsReplacementAndChildSelection() {
        MinHeap heap = new MinHeap();
        for (int value : new int[] {1, 2, 3, 4}) {
            heap.insert(value);
            assertTrue(heap.hasHeapProperty());
        }
        heap.metrics().reset();
        assertEquals(1, heap.extractMin());
        assertTrue(heap.hasHeapProperty());
        assertEquals(5, heap.metrics().steps);
        assertEquals(3, heap.metrics().moves);
        assertEquals(2, heap.metrics().comparisons);
        assertEquals(2, heap.peekMin());
    }

    private static void checkSortedExtraction(int[] values) {
        MinHeap heap = new MinHeap();
        int[] expected = values.clone();
        Arrays.sort(expected);
        for (int value : values) {
            heap.insert(value);
            assertTrue(heap.hasHeapProperty(), "after insert " + value);
        }
        for (int value : expected) {
            assertEquals(value, heap.peekMin());
            assertEquals(value, heap.extractMin());
            assertTrue(heap.hasHeapProperty(), "after extract " + value);
        }
        assertThrows(IllegalStateException.class, heap::extractMin);
    }
}
