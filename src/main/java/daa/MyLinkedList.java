package daa;


public class MyLinkedList implements IntSequence {
    private static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private final Metrics metrics = new Metrics();

    @Override
    public void add(int value) {
        Node node = new Node(value);
        if (size == 0) {
            head = node;
            metrics.moves++;
        } else {
            tail.next = node;
            metrics.moves++;
        }
        tail = node;
        metrics.moves++;
        size++;
    }

    @Override
    public void add(int index, int value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index: " + index);
        }
        if (index == size) {
            add(value);
            return;
        }
        Node node = new Node(value);
        if (index == 0) {
            node.next = head;
            head = node;
            metrics.moves += 2;
        } else {
            Node previous = nodeAt(index - 1);
            node.next = next(previous);
            previous.next = node;
            metrics.moves += 2;
        }
        size++;
    }

    @Override
    public int remove(int index) {
        checkIndex(index);
        Node removed;
        if (index == 0) {
            removed = head;
            head = next(head);
            metrics.moves++;
            if (size == 1) {
                tail = null;
                metrics.moves++;
            }
        } else {
            Node previous = nodeAt(index - 1);
            removed = next(previous);
            previous.next = next(removed);
            metrics.moves++;
            if (removed == tail) {
                tail = previous;
                metrics.moves++;
            }
        }
        size--;
        return removed.value;
    }

    @Override
    public int get(int index) {
        checkIndex(index);
        return nodeAt(index).value;
    }

    @Override
    public boolean contains(int value) {
        Node current = head;
        while (current != null) {
            metrics.comparisons++;
            if (current.value == value) {
                return true;
            }
            current = next(current);
        }
        return false;
    }

    @Override
    public Metrics metrics() {
        return metrics;
    }

    private Node nodeAt(int index) {
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = next(current);
        }
        return current;
    }

    private Node next(Node node) {
        metrics.steps++;
        return node.next;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index);
        }
    }
}
