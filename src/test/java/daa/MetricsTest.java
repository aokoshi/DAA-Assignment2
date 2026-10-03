package daa;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MetricsTest {
    @Test
    void resetClearsAllCountersWithoutChangingStoredValues() {
        DynamicArray array = new DynamicArray();
        array.add(10);
        array.add(20);
        array.add(0, 30);
        assertTrue(array.contains(20));
        assertTrue(array.metrics().steps > 0);
        assertTrue(array.metrics().moves > 0);
        assertTrue(array.metrics().comparisons > 0);
        array.metrics().reset();
        assertEquals(0, array.metrics().steps);
        assertEquals(0, array.metrics().moves);
        assertEquals(0, array.metrics().comparisons);
        assertEquals(30, array.get(0));
        assertEquals(10, array.get(1));
        assertEquals(20, array.get(2));
    }

    @Test
    void countersBelongToTheirOwnStructure() {
        DynamicArray first = new DynamicArray();
        DynamicArray second = new DynamicArray();
        first.add(1);
        second.add(2);
        first.metrics().reset();
        second.metrics().reset();
        assertEquals(1, first.get(0));
        assertEquals(1, first.metrics().steps);
        assertEquals(0, second.metrics().steps);
        assertEquals(0, second.metrics().moves);
        assertEquals(0, second.metrics().comparisons);
    }
}
