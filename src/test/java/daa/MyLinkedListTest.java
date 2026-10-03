package daa;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyLinkedListTest extends SequenceContractTest {
    @Override
    IntSequence createSequence() {
        return new MyLinkedList();
    }

    @Test
    void getCountsExactlyIndexNextPointerTraversals() {
        MyLinkedList list = new MyLinkedList();
        for (int i = 0; i < 5; i++) {
            list.add(i * 10);
        }
        for (int i = 0; i < 5; i++) {
            list.metrics().reset();
            assertEquals(i * 10, list.get(i));
            assertEquals(i, list.metrics().steps);
            assertEquals(0, list.metrics().moves);
            assertEquals(0, list.metrics().comparisons);
        }
    }

    @Test
    void containsCountsVisitedValuesAndNextPointerTraversals() {
        MyLinkedList list = new MyLinkedList();
        list.add(10);
        list.add(20);
        list.add(30);
        list.metrics().reset();
        assertTrue(list.contains(20));
        assertEquals(1, list.metrics().steps);
        assertEquals(2, list.metrics().comparisons);
        assertEquals(0, list.metrics().moves);
        list.metrics().reset();
        assertFalse(list.contains(40));
        assertEquals(3, list.metrics().steps);
        assertEquals(3, list.metrics().comparisons);
    }

    @Test
    void headInsertAndRemoveCountPointerUpdates() {
        MyLinkedList list = new MyLinkedList();
        list.add(20);
        list.metrics().reset();
        list.add(0, 10);
        assertEquals(2, list.metrics().moves, "new next and head updates");
        assertEquals(0, list.metrics().steps);
        assertEquals(0, list.metrics().comparisons);
        list.metrics().reset();
        assertEquals(10, list.remove(0));
        assertEquals(1, list.metrics().moves, "head update");
        assertEquals(1, list.metrics().steps);
        assertEquals(0, list.metrics().comparisons);
    }
}
