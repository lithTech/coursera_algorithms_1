import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.Arrays;

public class BurrowsWheeler {

    private static final int R = 256;

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray cSuff = new CircularSuffixArray(s);
        int len = s.length();
        char[] out = new char[len];
        int first = 0;

        for (int i = 0; i < len; i++) {
            int index = cSuff.index(i);
            if (index == 0)
                first = i;

            int newId = index - 1;
            if (newId < 0) newId = len + newId;
            out[i] = s.charAt(newId);
        }

        BinaryStdOut.write(first);
        for (int i = 0; i < out.length; i++)
            BinaryStdOut.write(out[i]);

        BinaryStdOut.close();
    }

    private static char[] fillIndexes(char[] enc, int[] outCounts, int[][] outMap) {
        int[] r = new int[R];
        char[] sorted = new char[enc.length];
        System.arraycopy(enc, 0, sorted, 0, enc.length);
        Arrays.sort(sorted);

        for (int i = 0; i < sorted.length; i++)
            outCounts[i] = r[sorted[i]]++;

        for (int i = 0; i < enc.length; i++) {
            if (outMap[enc[i]] == null) {
                outMap[enc[i]] = new int[r[enc[i]]];
                r[enc[i]] = 0;
            }
            outMap[enc[i]][r[enc[i]]] = i;
            r[enc[i]]++;
        }
        return sorted;
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String encodedStr = BinaryStdIn.readString();
        char[] enc = encodedStr.toCharArray();

        int[] counts = new int[enc.length];
        int[][] findMap = new int[R][];
        char[] sorted = fillIndexes(enc, counts, findMap);

        for (int i = 0; i < enc.length; i++) {
            char c = sorted[first];
            BinaryStdOut.write(c);

            first = findMap[c][counts[first]];
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) BurrowsWheeler.transform();
        else if (args[0].equals("+")) BurrowsWheeler.inverseTransform();
    }

}