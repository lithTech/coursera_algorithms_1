import java.util.Arrays;

public class CircularSuffixArray {

    private static class Suff implements Comparable<Suff> {
        private final String text;
        private final int index;

        public Suff(String text, int index) {
            this.text = text;
            this.index = index;
        }

        private int length() {
            return text.length() - index;
        }
        private char charAt(int i) {
            return text.charAt(index + i);
        }

        public int compareTo(Suff that) {
            if (this == that) return 0;  // optimization
            int n = Math.min(this.length(), that.length());
            for (int i = 0; i < n; i++) {
                if (this.charAt(i) < that.charAt(i)) return -1;
                if (this.charAt(i) > that.charAt(i)) return +1;
            }
            return this.length() - that.length();
        }
    }

    private final Suff[] array;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();
        array = new Suff[s.length()];
        for (int i = 0; i < s.length(); i++) {
            array[i] = new Suff(s, i);
        }
        Arrays.sort(array);
    }

    // length of s
    public int length() {
        return array.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length()) throw new IllegalArgumentException();

        return array[i].index;
    }

    // unit testing (required)
    public static void main(String[] args) {
        String s = "ABRACADABRA!";
        CircularSuffixArray array = new CircularSuffixArray(s);
        for (int i = 0; i < array.length(); i++) {
            System.out.println(("" + s.charAt(array.index(i))) + " " + array.index(i));
        }
    }

}