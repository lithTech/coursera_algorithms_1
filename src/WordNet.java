import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WordNet {

    // the string key is actually integer in the task (id of element)
    // string[] value is a list of words
    private final Map<Integer, String[]> synsets = new LinkedHashMap<>();
    private final Map<String, List<Integer>> nouns = new HashMap<>();
    private SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException();

        In synsetsIn = new In(synsets);
        In hypernymsIn = new In(hypernyms);

        loadWords(synsetsIn, hypernymsIn);
    }

    private List<Integer> detectRoots(Digraph digraph) {
        List<Integer> roots = new ArrayList<>();
        for (int i = 0; i < digraph.V(); i++)
            if (digraph.outdegree(i) == 0) roots.add(i);

        return roots;
    }

    private void loadWords(In synsetsIn, In hypernymsIn) {
        while (synsetsIn.hasNextLine()) {
            String line = synsetsIn.readLine();
            String[] cells = line.split(",");
            String[] synArray = cells[1].split(" ");

            int id = Integer.parseInt(cells[0]);
            for (String s : synArray) {
                if (!nouns.containsKey(s))
                    nouns.put(s, new ArrayList<>());
                nouns.get(s).add(id);
            }

            synsets.put(id, synArray);
        }
        Digraph hypernyms = new Digraph(synsets.size());
        while (hypernymsIn.hasNextLine()) {
            String line = hypernymsIn.readLine();
            String[] cells = line.split(",");
            int id = Integer.parseInt(cells[0]);
            for (int i = 1; i < cells.length; i++) {
                String ancestor = cells[i];
                hypernyms.addEdge(id, Integer.parseInt(ancestor));
            }
        }
        if (detectRoots(hypernyms).size() != 1)
            throw new IllegalArgumentException();
        sap = new SAP(hypernyms);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return new ArrayList<>(nouns.keySet());
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException();

        return nouns.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        assertTwoNouns(nounA, nounB);

        return sap.length(nouns.get(nounA), nouns.get(nounB));
    }

    private String concat(String[] a) {
        StringBuilder s = new StringBuilder();
        for (String r : a) {
            s.append(r).append(" ");
        }
        return s.toString().trim();
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        assertTwoNouns(nounA, nounB);

        int ancestor = sap.ancestor(nouns.get(nounA), nouns.get(nounB));
        if (synsets.containsKey(ancestor))
            return concat(synsets.get(ancestor));
        return "";
    }

    private void assertTwoNouns(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException();
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("nounA/nounB is not a word.net noun");
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordNet = new WordNet(args[0], args[1]);
        while (!StdIn.isEmpty()) {
            String v = StdIn.readString();
            String w = StdIn.readString();
            int length   = wordNet.distance(v, w);
            String ancestor = wordNet.sap(v, w);
            StdOut.printf("length = %d, ancestor = %s\n", length, ancestor);
        }
    }
}