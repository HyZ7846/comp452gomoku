package assign3.gomokugame;

import java.io.Serializable;
import java.util.Arrays;

public class State implements Serializable {
    private static final long serialVersionUID = 1L;
    private int[][] board;

    public State(int[][] board) {
        this.board = deepCopy(board);
    }

    public int[][] getBoard() {
        return deepCopy(board);
    }

    public static int[][] deepCopy(int[][] original) {
        if (original == null) {
            return null;
        }

        final int[][] result = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Arrays.deepEquals(board, state.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
