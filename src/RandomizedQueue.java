import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private static final int CAPACITY = 20;
    private Object[] items = null;
    private int[] shuffledId = null;
    private int size = 0;
    private int startShuffleId = -1;

    // construct an empty randomized queue
    public RandomizedQueue() {

    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return size;
    }

    private void resize(int length) {
        Object[] old = items;
        items = new Object[length];
        shuffledId = new int[length];
        if (old != null) {
            for (int i = 0; i < size; i++) {
                items[i] = old[i];
                shuffledId[i] = i;
            }
        }
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException();

        if (items == null || items.length - 1 == size)
            resize(size + 1 + CAPACITY);
        if (startShuffleId == -1)
            startShuffleId = size;

        items[size] = item;
        shuffledId[size] = size;

        size++;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException();

        if (size < items.length - CAPACITY*1.5)
            resize(size + CAPACITY);
        if (startShuffleId > -1)
        {
            StdRandom.shuffle(shuffledId, startShuffleId, size);
            startShuffleId = -1;
        }
        size--;
        int id = shuffledId[size];

        return (Item) items[id];
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException();

        int random = StdRandom.uniform(size);
        return (Item) items[shuffledId[random]];
    }

    private class RandomIterator<T> implements Iterator<T> {
        private int[] itItems = null;
        private int n = 0;

        public RandomIterator() {
            if (size == 0) return;
            itItems = new int[size];
            for (int i = 0; i < size; i++) {
                int id = shuffledId[i];
                itItems[i] = id;
            }
            StdRandom.shuffle(itItems);
        }

        @Override
        public boolean hasNext() {
            return n < size;
        }

        @Override
        public T next() {
            if (!hasNext()) throw new NoSuchElementException();
            return (T) items[itItems[n++]];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomIterator<>();
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Integer> queue = new RandomizedQueue<>();
        StdOut.println(queue.iterator().hasNext());

        fillWithIntegersAndPrint(queue, 1000);
        StdOut.println("\nThe size and isEmpty: "+queue.size()+" : "+queue.isEmpty());
        StdOut.println("\nTesting dequeue with deletion");
        dequeuePrint(queue);
        StdOut.println("\nThe size and isEmpty should be 0 and true: "+queue.size()+" : "+queue.isEmpty());
        fillWithIntegersAndPrint(queue, 2000);
        StdOut.println("\nTesting dequeue sample (without deletion)");
        for (int i = 0; i < queue.size(); i++) {
            StdOut.print(queue.sample());
            StdOut.print(" ");
        }
        StdOut.println("\nThe size and isEmpty: "+queue.size()+" : "+queue.isEmpty());
        StdOut.println("\nTesting another dequeue");
        dequeuePrint(queue);
        StdOut.println("\nThe size and isEmpty: "+queue.size()+" : "+queue.isEmpty());

        StdOut.println("Enqueue and dequeue");
        for (int i = 0; i < 10000; i++) {
            queue.enqueue(i);
            if (i == 5000) {
                for (int j = 0; j < 1000; j++) {
                    queue.dequeue();
                }
            }
        }



    }

    private static void dequeuePrint(RandomizedQueue<Integer> queue) {
        while (!queue.isEmpty()) {
            StdOut.print(queue.dequeue());
            StdOut.print(" ");
        }
    }

    private static void fillWithIntegersAndPrint(RandomizedQueue<Integer> queue, int size) {
        for (int i = 0; i < size; i++) {
            queue.enqueue(i);
        }
        StdOut.println("First iterator example");
        for (Integer v : queue) {
            StdOut.print(v);
            StdOut.print(" ");
        }
        StdOut.println();
        StdOut.println("Second iterator example");
        for (Integer v : queue) {
            StdOut.print(v);
            StdOut.print(" ");
        }
    }

}