package daa;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayTest extends SequenceContractTest {
    @Override
    IntSequence createSequence() {
        return new DynamicArray();
    }

    @Test
    void getCountsOneCellRead() {
        DynamicArray array = new DynamicArray();
        array.add(10);
        array.add(20);
        array.metrics().reset();
        assertEquals(20, array.get(1));
        assertEquals(1, array.metrics().steps);
        assertEquals(0, array.metrics().moves);
        assertEquals(0, array.metrics().comparisons);
    }

    @Test
    void containsCountsReadAndComparisonForEachVisitedElement() {
        DynamicArray array = new DynamicArray();
        array.add(10);
        array.add(20);
        array.add(30);
        array.metrics().reset();
        assertTrue(array.contains(20));
        assertEquals(2, array.metrics().steps);
        assertEquals(2, array.metrics().comparisons);
        assertEquals(0, array.metrics().moves);
        array.metrics().reset();
        assertFalse(array.contains(40));
        assertEquals(3, array.metrics().steps);
        assertEquals(3, array.metrics().comparisons);
    }

    @Test
    void middleInsertionCountsEachShift() {
        DynamicArray array = new DynamicArray();
        array.add(10);
        array.add(30);
        array.add(40);
        array.metrics().reset();
        array.add(1, 20);
        assertEquals(2, array.metrics().steps);
        assertEquals(2, array.metrics().moves);
        assertEquals(0, array.metrics().comparisons);
    }

    @Test
    void middleRemovalCountsReturnedReadAndEachShift() {
        DynamicArray array = new DynamicArray();
        for (int value : new int[] {10, 20, 30, 40}) {
            array.add(value);
        }
        array.metrics().reset();
        assertEquals(20, array.remove(1));
        assertEquals(3, array.metrics().steps);
        assertEquals(2, array.metrics().moves);
        assertEquals(0, array.metrics().comparisons);
    }

    @Test
    void growthCountsEveryCopiedExistingElement() {
        DynamicArray array = new DynamicArray();
        for (int i = 0; i < 8; i++) {
            array.add(i);
        }
        array.metrics().reset();
        array.add(8);
        assertEquals(8, array.metrics().steps);
        assertEquals(8, array.metrics().moves);
        assertEquals(0, array.metrics().comparisons);
        array.metrics().reset();
        array.add(9);
        assertEquals(0, array.metrics().steps);
        assertEquals(0, array.metrics().moves);
    }
}
