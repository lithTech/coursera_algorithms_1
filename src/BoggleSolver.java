import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.TST;

import java.util.HashSet;
import java.util.Set;

public class BoggleSolver
{
    private static final int PREFIX_CHECK_THRESHOLD = 3;
    private static final int WORD_MIN_LEN = 2;
    private final TST<Object> dict = new TST<>();
    private final int[] rate = {0, 0, 0, 1, 1, 2, 3, 5, 11};
    private boolean[] visited;
    private final Set<String> words = new HashSet<>();
    private char[][] curBoard;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        if (dictionary == null) throw new IllegalArgumentException();

        for (String word : dictionary)
            dict.put(word, word);
    }

    private int getRate(String word) {
        int length = word.length();
        if (rate.length <= length)
            length = rate.length - 1;
        return rate[length];
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) throw new IllegalArgumentException();

        words.clear();
        this.curBoard = new char[board.rows()][board.cols()];
        initBoard(board);

        visited = new boolean[board.cols() * board.rows()];

        for (int r = 0; r < board.rows(); r++) {
            for (int c = 0; c < board.cols(); c++) {
                visitNext(r, c, new StringBuilder());
            }
        }

        return words;
    }

    private void initBoard(BoggleBoard board) {
        for (int r = 0; r < board.rows(); r++) {
            for (int c = 0; c < board.cols(); c++) {
                curBoard[r][c] = board.getLetter(r, c);
            }
        }
    }

    private void visitNext(int r, int c, StringBuilder path) {
        if (curBoard[0].length <= c || c < 0 || curBoard.length <= r || r < 0)
            return;
        int index = c + r * curBoard[0].length;
        if (visited[index]) return;
        visited[index] = true;

        char letter = curBoard[r][c];
        path.append(letter);
        if (letter == 'Q') path.append('U');

        boolean shouldVisit = true;
        if (path.length() > WORD_MIN_LEN) {
            String candidate = path.toString();
            if (dict.get(candidate) != null)
                words.add(candidate);
        }
        // apply optimisation based on prefix
        if (path.length() > PREFIX_CHECK_THRESHOLD)
            shouldVisit = dict.keysWithPrefix(path.toString()).iterator().hasNext();

        if (shouldVisit) {
            visitNext(r + 1, c, path);
            visitNext(r - 1, c, path);
            visitNext(r, c + 1, path);
            visitNext(r, c - 1, path);
            visitNext(r - 1, c - 1, path);
            visitNext(r + 1, c + 1, path);
            visitNext(r + 1, c - 1, path);
            visitNext(r - 1, c + 1, path);
        }

        visited[index] = false;
        path.deleteCharAt(path.length() - 1);
        if (letter == 'Q') path.deleteCharAt(path.length() - 1);
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null) throw new IllegalArgumentException();
        if (!dict.contains(word)) return 0;
        return getRate(word);
    }

    public static void main(String[] args) {
        In dictionary = new In(args[0]);
        // BoggleBoard board = new BoggleBoard(args[1]);
        BoggleSolver solver = new BoggleSolver(dictionary.readAllLines());

        long start = System.currentTimeMillis();
        int cnt = 0;
        int score = 0;
         while (System.currentTimeMillis() - start < 1000) {
             BoggleBoard board = new BoggleBoard(4, 4);
            for (String w : solver.getAllValidWords(board)) {
                score += solver.scoreOf(w);
            }
            cnt++;
         }
        System.out.println("Speed is ~" + cnt + " per second. Score sum " + score);
    }
}
