import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class RandomWord {

    public static void main(String[] args) {
        int i = 0;
        String champ = "";
        while (!StdIn.isEmpty()) {
            String curr = StdIn.readString();
            double p = 1.0 / (double) (++i);
            if (StdRandom.bernoulli(p))
                champ = curr;
        }
        StdOut.println(champ);
    }
}
