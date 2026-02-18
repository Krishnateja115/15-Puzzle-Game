package fifteen_divideandconq;

import java.util.*;

public class BoardState {

    public static final int SIZE = 16;

    public int[] board = new int[SIZE];

    Random rand = new Random();

    static final int[] GOAL = {
            1,2,3,4,
            5,6,7,8,
            9,10,11,12,
            13,14,15,0
    };

    public BoardState() {
        reset();
        shuffle();
    }

    void reset() {
        System.arraycopy(GOAL, 0, board, 0, SIZE);
    }

    void shuffle() {

        for (int i = 0; i < 400; i++) {

            List<int[]> list = neighbors(board);

            int[] next = list.get(rand.nextInt(list.size()));

            setState(next);
        }
    }

    int[] copy() {
        return board.clone();
    }

    void setState(int[] st) {
        System.arraycopy(st, 0, board, 0, SIZE);
    }

    int zeroPos() {

        for (int i = 0; i < SIZE; i++)
            if (board[i] == 0)
                return i;

        return -1;
    }

    boolean isSolved() {
        return Arrays.equals(board, GOAL);
    }

    List<int[]> neighbors(int[] st) {

        List<int[]> res = new ArrayList<>();

        int z = 0;

        for (int i = 0; i < SIZE; i++)
            if (st[i] == 0)
                z = i;

        int r = z / 4;
        int c = z % 4;

        int[][] dir = {
                {1,0},{-1,0},{0,1},{0,-1}
        };

        for (int[] d : dir) {

            int nr = r + d[0];
            int nc = c + d[1];

            if (nr >= 0 && nr < 4 && nc >= 0 && nc < 4) {

                int p = nr * 4 + nc;

                int[] copy = st.clone();

                copy[z] = copy[p];
                copy[p] = 0;

                res.add(copy);
            }
        }

        return res;
    }
}
