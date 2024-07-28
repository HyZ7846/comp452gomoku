package assign3.gomokugame;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class QLearningAI implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final int SIZE = 7;
    private static final double LEARNING_RATE = 0.1;
    private static final double DISCOUNT_FACTOR = 0.9;
    private static final double WIN_REWARD = 2.0;
    private static final double DEFENSIVE_REWARD = 1.5;
    private static final double NORMAL_REWARD = 1.0;

    private Map<State, double[][]> qTable;

    public QLearningAI() {
        qTable = new HashMap<>();
    }

    // Choose the best action based on the Q-values and evaluation function
    public int[] chooseAction(State state) {
        int[][] board = state.getBoard();
        int[] bestAction = null;
        double maxReward = Double.NEGATIVE_INFINITY;

        // Evaluate all possible moves and choose the one with the highest reward
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    double reward = evaluateMove(board, i, j);
                    if (reward > maxReward) {
                        maxReward = reward;
                        bestAction = new int[]{i, j};
                    }
                }
            }
        }
        return bestAction;
    }

    // Evaluate a move based on the current board state
    double evaluateMove(int[][] board, int row, int col) {
        if (canWin(board, row, col, -1)) {
            return WIN_REWARD; // Prioritize winning moves
        }
        if (canBlock(board, row, col)) {
            return DEFENSIVE_REWARD; // Then prioritize blocking moves
        }
        return NORMAL_REWARD; // Otherwise, normal move
    }

    // Check if the move can win the game
    private boolean canWin(int[][] board, int row, int col, int player) {
        return checkPotentialWin(board, row, col, 1, 0, player) || // Horizontal
                checkPotentialWin(board, row, col, 0, 1, player) || // Vertical
                checkPotentialWin(board, row, col, 1, 1, player) || // Diagonal down-right
                checkPotentialWin(board, row, col, 1, -1, player); // Diagonal down-left
    }

    // Check if the move can block the opponent
    private boolean canBlock(int[][] board, int row, int col) {
        return checkPotentialBlock(board, row, col, 1, 0) || // Horizontal
                checkPotentialBlock(board, row, col, 0, 1) || // Vertical
                checkPotentialBlock(board, row, col, 1, 1) || // Diagonal down-right
                checkPotentialBlock(board, row, col, 1, -1); // Diagonal down-left
    }

    // Check potential winning move for the given direction
    private boolean checkPotentialWin(int[][] board, int row, int col, int dRow, int dCol, int player) {
        for (int i = -4; i <= 0; i++) {
            int rStart = row + i * dRow;
            int cStart = col + i * dCol;
            if (isValidPosition(rStart, cStart) && isValidPosition(rStart + 4 * dRow, cStart + 4 * dCol)) {
                int count = 0;
                for (int j = 0; j < 5; j++) {
                    int r = rStart + j * dRow;
                    int c = cStart + j * dCol;
                    if (r == row && c == col) {
                        count++;
                    } else if (board[r][c] == player) {
                        count++;
                    } else if (board[r][c] != 0) {
                        break;
                    }
                }
                if (count == 5) {
                    return true;
                }
            }
        }
        return false;
    }

    // Check potential blocking move for the given direction
    private boolean checkPotentialBlock(int[][] board, int row, int col, int dRow, int dCol) {
        for (int i = -4; i <= 0; i++) {
            int rStart = row + i * dRow;
            int cStart = col + i * dCol;
            if (isValidPosition(rStart, cStart) && isValidPosition(rStart + 4 * dRow, cStart + 4 * dCol)) {
                int count = 0;
                for (int j = 0; j < 5; j++) {
                    int r = rStart + j * dRow;
                    int c = cStart + j * dCol;
                    if (r == row && c == col) {
                        continue;
                    } else if (board[r][c] == 1) {  // Check for black chip (1)
                        count++;
                    } else if (board[r][c] != 0) {
                        break;
                    }
                }
                if (count >= 3) {
                    return true;
                }
            }
        }
        return false;
    }

    // Check if the position is within the board boundaries
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    // Update the Q-values after making a move
    public void updateQValues(State oldState, int[] action, State newState, double reward) {
        double[][] oldQValues = qTable.computeIfAbsent(oldState, k -> new double[SIZE][SIZE]);
        double[][] newQValues = qTable.computeIfAbsent(newState, k -> new double[SIZE][SIZE]);

        double oldQ = oldQValues[action[0]][action[1]];
        double maxFutureQ = Double.NEGATIVE_INFINITY;

        for (double[] row : newQValues) {
            for (double value : row) {
                if (value > maxFutureQ) {
                    maxFutureQ = value;
                }
            }
        }

        double newQ = oldQ + LEARNING_RATE * (reward + DISCOUNT_FACTOR * maxFutureQ - oldQ);
        oldQValues[action[0]][action[1]] = newQ;
    }

    // Save the Q-table to a file
    public void saveQTable(String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load the Q-table from a file
    public static QLearningAI loadQTable(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (QLearningAI) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new QLearningAI();
        }
    }
}
