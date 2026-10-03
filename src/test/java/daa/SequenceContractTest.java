package daa;

import java.util.ArrayList;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


abstract class SequenceContractTest {
    abstract IntSequence createSequence();

    @Test
    void emptySequenceRejectsInvalidOperations() {
        IntSequence actual = createSequence();
        assertFalse(actual.contains(42));
        assertThrows(IndexOutOfBoundsException.class, () -> actual.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> actual.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> actual.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> actual.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> actual.add(-1, 42));
        assertThrows(IndexOutOfBoundsException.class, () -> actual.add(1, 42));
    }

    @Test
    void oneElementCanBeRemovedAndSequenceReused() {
        IntSequence actual = createSequence();
        actual.add(0, 17);
        assertEquals(17, actual.get(0));
        assertTrue(actual.contains(17));
        assertEquals(17, actual.remove(0));
        assertFalse(actual.contains(17));
        assertThrows(IndexOutOfBoundsException.class, () -> actual.get(0));
        actual.add(23);
        assertEquals(23, actual.get(0));
        assertEquals(23, actual.remove(0));
    }

    @Test
    void insertAtFirstMiddleAndLastIndexPreservesOrder() {
        IntSequence actual = createSequence();
        actual.add(20);
        actual.add(0, 10);
        actual.add(2, 40);
        actual.add(2, 30);
        assertValues(actual, 10, 20, 30, 40);
    }

    @Test
    void removeFirstMiddleAndLastPreservesOrder() {
        IntSequence actual = createSequence();
        for (int value = 10; value <= 60; value += 10) {
            actual.add(value);
        }
        assertEquals(10, actual.remove(0));
        assertEquals(40, actual.remove(2));
        assertEquals(60, actual.remove(3));
        assertValues(actual, 20, 30, 50);
        actual.add(70);
        assertValues(actual, 20, 30, 50, 70);
    }

    @Test
    void duplicateValuesRemainIndependent() {
        IntSequence actual = createSequence();
        actual.add(7);
        actual.add(7);
        actual.add(0, 7);
        assertEquals(7, actual.remove(1));
        assertValues(actual, 7, 7);
        assertTrue(actual.contains(7));
        actual.remove(0);
        actual.remove(0);
        assertFalse(actual.contains(7));
    }

    @Test
    void supportsFullIntRange() {
        IntSequence actual = createSequence();
        actual.add(Integer.MIN_VALUE);
        actual.add(0);
        actual.add(Integer.MAX_VALUE);
        actual.add(-1);
        assertValues(actual, Integer.MIN_VALUE, 0, Integer.MAX_VALUE, -1);
        assertTrue(actual.contains(Integer.MIN_VALUE));
        assertTrue(actual.contains(Integer.MAX_VALUE));
        assertFalse(actual.contains(1));
    }

    @Test
    void manyAppendsAndRemovalsPreserveValues() {
        IntSequence actual = createSequence();
        for (int i = 0; i < 1025; i++) {
            actual.add(i * 3);
        }
        for (int i = 0; i < 1025; i++) {
            assertEquals(i * 3, actual.get(i));
        }
        for (int i = 1024; i >= 0; i--) {
            assertEquals(i * 3, actual.remove(i));
        }
        assertThrows(IndexOutOfBoundsException.class, () -> actual.get(0));
    }

    @Test
    void randomOperationsMatchArrayList() {
        IntSequence actual = createSequence();
        ArrayList<Integer> expected = new ArrayList<>();
        Random random = new Random(42);
        for (int operation = 0; operation < 3000; operation++) {
            int choice = random.nextInt(5);
            int value = random.nextInt(101) - 50;
            if (choice == 0 || expected.isEmpty()) {
                actual.add(value);
                expected.add(value);
            } else if (choice == 1) {
                int index = random.nextInt(expected.size() + 1);
                actual.add(index, value);
                expected.add(index, value);
            } else if (choice == 2) {
                int index = random.nextInt(expected.size());
                assertEquals(expected.remove(index).intValue(), actual.remove(index));
            } else if (choice == 3) {
                int index = random.nextInt(expected.size());
                assertEquals(expected.get(index).intValue(), actual.get(index));
            } else {
                assertEquals(expected.contains(value), actual.contains(value));
            }
            if (operation % 50 == 0) {
                assertMatches(actual, expected);
            }
        }
        assertMatches(actual, expected);
    }

    @Test
    void invalidIndexesLeaveContentsUnchanged() {
        IntSequence actual = createSequence();
        actual.add(10);
        actual.add(20);
        for (int index : new int[] {-1, Integer.MIN_VALUE, 2, Integer.MAX_VALUE}) {
            assertThrows(IndexOutOfBoundsException.class, () -> actual.get(index));
            assertThrows(IndexOutOfBoundsException.class, () -> actual.remove(index));
        }
        for (int index : new int[] {-1, Integer.MIN_VALUE, 3, Integer.MAX_VALUE}) {
            assertThrows(IndexOutOfBoundsException.class, () -> actual.add(index, 99));
        }
        assertValues(actual, 10, 20);
        actual.add(2, 30);
        assertValues(actual, 10, 20, 30);
    }

    @Test
    void containsFindsHeadMiddleTailAndMissingValue() {
        IntSequence actual = createSequence();
        actual.add(11);
        actual.add(22);
        actual.add(33);
        assertTrue(actual.contains(11));
        assertTrue(actual.contains(22));
        assertTrue(actual.contains(33));
        assertFalse(actual.contains(44));
    }

    private static void assertValues(IntSequence actual, int... expected) {
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual.get(i), "index " + i);
        }
        assertThrows(IndexOutOfBoundsException.class, () -> actual.get(expected.length));
    }

    private static void assertMatches(IntSequence actual, ArrayList<Integer> expected) {
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).intValue(), actual.get(i), "index " + i);
        }
        assertThrows(IndexOutOfBoundsException.class, () -> actual.get(expected.size()));
    }
}
