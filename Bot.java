package fifteen_divideandconq;

import java.util.*;

public class Bot {

    BoardState bs;

    // Frequency for penalty-based greedy
    HashMap<String, Integer> frequency = new HashMap<>();

    public Bot(BoardState b) {
        bs = b;
    }

    // cur = current board
    // blockState = board before player move
    public int[] chooseMove(int[] cur, int[] blockState) {

        List<int[]> list = bs.neighbors(cur);

        int[][] arr = new int[list.size()][];

        for (int i = 0; i < list.size(); i++)
            arr[i] = list.get(i);

        // Sort (Divide & Conquer)
        quickSort(arr, 0, arr.length - 1);

        int[] best = null;
        int bestScore = Integer.MIN_VALUE;

        // Find which rows are solved in current board
        boolean[] lockedRows = findLockedRows(cur);

        // Evaluate moves
        for (int[] st : arr) {

            // Block undo
            if (blockState != null &&
                Arrays.equals(st, blockState))
                continue;

            // Do not break solved rows
            if (breaksLockedRow(cur, st, lockedRows))
                continue;

            int score = adjustedScore(st);

            if (score > bestScore) {
                bestScore = score;
                best = st;
            }
        }

        if (best == null)
            best = arr[0];

        addFrequency(best);

        return best;
    }

    /* ================= ROW LOCKING ================= */

    // Find solved rows
    boolean[] findLockedRows(int[] st) {

        boolean[] locked = new boolean[4];

        for (int r = 0; r < 4; r++) {

            boolean ok = true;

            for (int c = 0; c < 4; c++) {

                int idx = r * 4 + c;
                int expected = r * 4 + c + 1;

                // Last cell = 0
                if (idx == 15)
                    expected = 0;

                if (st[idx] != expected) {
                    ok = false;
                    break;
                }
            }

            locked[r] = ok;
        }

        return locked;
    }

    // Check if move breaks locked row
    boolean breaksLockedRow(int[] oldS,
                            int[] newS,
                            boolean[] locked) {

        for (int r = 0; r < 4; r++) {

            if (!locked[r]) continue;

            // If any tile in locked row changed → bad
            for (int c = 0; c < 4; c++) {

                int idx = r * 4 + c;

                if (oldS[idx] != newS[idx])
                    return true;
            }
        }

        return false;
    }

    /* ================= PENALTY ================= */

    void addFrequency(int[] st) {

        String key = Arrays.toString(st);

        frequency.put(key,
                frequency.getOrDefault(key, 0) + 1);
    }

    int adjustedScore(int[] st) {

        String key = Arrays.toString(st);

        int penalty = frequency.getOrDefault(key, 0);

        return rowScore(st) - penalty;
    }

    /* ================= QUICK SORT ================= */

    void quickSort(int[][] arr, int l, int h) {

        if (l < h) {

            int p = partition(arr, l, h);

            quickSort(arr, l, p - 1);
            quickSort(arr, p + 1, h);
        }
    }

    int partition(int[][] arr, int l, int h) {

        int pivot = rowScore(arr[h]);

        int i = l - 1;

        for (int j = l; j < h; j++) {

            if (rowScore(arr[j]) >= pivot) {

                i++;
                swap(arr, i, j);
            }
        }

        swap(arr, i + 1, h);

        return i + 1;
    }

    void swap(int[][] arr, int i, int j) {

        int[] t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    /* ================= GREEDY ================= */

    // Row correctness score
    int rowScore(int[] st) {

        int score = 0;

        for (int i = 0; i < st.length; i++) {

            int v = st[i];

            if (v == 0) continue;

            int curRow = i / 4;
            int targetRow = (v - 1) / 4;

            if (curRow == targetRow)
                score++;
        }

        return score;
    }
}
