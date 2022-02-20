import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private final WordNet wordNet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        if (nouns == null) throw new IllegalArgumentException();
        for (String noun : nouns) if (noun == null) throw new IllegalArgumentException();

        int[] distances = new int[nouns.length];
        for (int i = 0; i < nouns.length; i++) {
            String n1 = nouns[i];
            int distSum = 0;
            for (String n2 : nouns) {
                int dist = wordNet.distance(n1, n2);
                distSum += dist;
            }
            distances[i] = distSum;
        }
        int maxId = -1, maxDist = -1;
        for (int i = 0; i < distances.length; i++) {
            int distance = distances[i];
            if (maxDist < distance) {
                maxDist = distance;
                maxId = i;
            }
        }

        return nouns[maxId];
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}