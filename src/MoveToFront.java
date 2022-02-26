import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    private static final int R = 256;
    // basically for testing purpose, for example ABCDEF is R = 6 and B = 65
    private static final int B = 0;

    // init dict with R characters, 256 for example for extended ACII
    private static char[] initDict() {
        char[] dict = new char[MoveToFront.R];
        for (int i = 0; i < MoveToFront.R; i++) {
            dict[i] = (char) (i + B);
        }
        return dict;
    }

    // init pos array (where each character as index and value as position in dict
    private static int[] initPosAr() {
        int[] posAr = new int[MoveToFront.R];
        for (int i = 0; i < MoveToFront.R; i++) {
            posAr[i] = i;
        }
        return posAr;
    }

    /**
     * shift char to the front. Updates posAr and ar arrays
     * @param ar - array with characters (dict)
     * @param pos - position of character in ar
     * @param posAr - position array
     */
    private static void shift(char[] ar, int pos, int[] posAr) {
        if (pos == 0) return;
        char v = ar[pos];
        for (int i = pos; i > 0; i--) {
            posAr[ar[i - 1] - B] = i;
            ar[i] = ar[i - 1];
        }
        ar[0] = v;
        posAr[v - B] = 0;
    }

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] dict = initDict();
        int[] posAr = initPosAr();
        while (!BinaryStdIn.isEmpty()) {
            char ch = BinaryStdIn.readChar();

            int pos = posAr[ch - B];
            shift(dict, pos, posAr);
            BinaryStdOut.write((char) pos);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] dict = initDict();
        int[] posAr = initPosAr();
        while (!BinaryStdIn.isEmpty()) {
            int pos = BinaryStdIn.readChar();

            BinaryStdOut.write(dict[pos]);

            shift(dict, pos, posAr);
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) MoveToFront.encode();
        else if (args[0].equals("+")) MoveToFront.decode();
    }

}