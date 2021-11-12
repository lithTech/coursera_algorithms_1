import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private class Node {
        Item item;
        Node next;
        Node prev;

        public Node(Item item) {
            this.item = item;
        }
    }

    private Node first = null;
    private int size = 0;
    private Node last = null;

    // construct an empty deque
    public Deque() {

    }

    // is the deque empty?
    public boolean isEmpty() {
        return first == null;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException();
        ++size;
        Node prev = first;
        first = new Node(item);
        if (prev != null) {
            first.next = prev;
            prev.prev = first;
        }
        else
            last = first;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException();
        ++size;
        Node prev = last;
        last = new Node(item);
        if (prev != null) {
            last.prev = prev;
            prev.next = last;
        }
        else
            first = last;
    }

    private void clear() {
        first = null;
        last = null;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException();
        --size;
        Item item = first.item;
        if (first.next != null) {
            Node newFirst = first.next;
            first.next = null;
            newFirst.prev = null;
            first = newFirst;
        }
        else
            clear();

        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException();
        --size;
        Item item = last.item;

        if (last.prev != null) {
            Node newLast = last.prev;
            last.prev = null;
            last = newLast;
            last.next = null;
        }
        else
            clear();

        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new Iterator<Item>() {
            Node current = first;
            @Override
            public boolean hasNext() {
                return current != null;
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            @Override
            public Item next() {
                if (!hasNext()) throw new NoSuchElementException();
                Item item = current.item;
                current = current.next;
                return item;
            }
        };
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<String> deque = new Deque<>();
        while (!StdIn.isEmpty()) {
            String s = StdIn.readString();
            String op = s.substring(0, 1);
            String v = s.substring(1);
            switch (op) {
                case ">":
                    deque.addFirst(v);
                    break;
                case "<":
                    deque.addLast(v);
                    break;
                case "-":
                    StdOut.println(String.format("Remove first: %s", deque.removeFirst()));
                    break;
                case "~":
                    StdOut.println(String.format("Remove first: %s", deque.removeLast()));
                    break;
            }

            StdOut.println("Elements:");
            for (String item : deque) {
                StdOut.println("Element: " + item);
            }
        }
    }

}