package assign3.gomokugame;

public class GomokuLearning {
    private static final int SIZE = 7;
    private QLearningAI blackAI;
    private QLearningAI whiteAI;
    private int[][] gameState;

    public GomokuLearning() {
        blackAI = QLearningAI.loadQTable("blackAI_qtable.ser");
        whiteAI = QLearningAI.loadQTable("whiteAI_qtable.ser");
    }

    // Run self-learning for a specified number of episodes
    public void selfLearn(int episodes) {
        for (int episode = 0; episode < episodes; episode++) {
            gameState = new int[SIZE][SIZE];
            boolean blackTurn = true;
            while (true) {
                QLearningAI currentAI = blackTurn ? blackAI : whiteAI;
                State oldState = new State(gameState);
                int[] action = currentAI.chooseAction(oldState);
                gameState[action[0]][action[1]] = blackTurn ? 1 : -1;

                double reward = currentAI.evaluateMove(gameState, action[0], action[1]);
                State newState = new State(gameState);

                // Check if the move leads to a win
                if (checkWin(action[0], action[1])) {
                    reward = 1;
                    currentAI.updateQValues(oldState, action, newState, reward);
                    break;
                }

                // Check if the board is full, resulting in a tie
                if (isBoardFull()) {
                    currentAI.updateQValues(oldState, action, newState, 0);
                    break;
                }

                // Update Q-values for the move
                currentAI.updateQValues(oldState, action, newState, reward);

                blackTurn = !blackTurn;
            }
            // Display training progress
            double progress = ((double) (episode + 1) / episodes) * 100;
            System.out.printf("Training: %.2f%%%n", progress);
        }

        // Save the trained Q-tables
        blackAI.saveQTable("blackAI_qtable.ser");
        whiteAI.saveQTable("whiteAI_qtable.ser");
    }

    // Check if the move leads to a win
    private boolean checkWin(int row, int col) {
        return checkDirection(row, col, 1, 0) || // Horizontal
                checkDirection(row, col, 0, 1) || // Vertical
                checkDirection(row, col, 1, 1) || // Diagonal down-right
                checkDirection(row, col, 1, -1); // Diagonal down-left
    }

    // Check the direction for potential win
    private boolean checkDirection(int row, int col, int dRow, int dCol) {
        int count = 1;
        count += countChips(row, col, dRow, dCol);
        count += countChips(row, col, -dRow, -dCol);
        return count >= 5;
    }

    // Count chips in the given direction
    private int countChips(int row, int col, int dRow, int dCol) {
        int r = row + dRow;
        int c = col + dCol;
        int count = 0;
        while (r >= 0 && r < SIZE && c >= 0 && c < SIZE && gameState[r][c] == gameState[row][col]) {
            count++;
            r += dRow;
            c += dCol;
        }
        return count;
    }

    // Check if the board is full
    private boolean isBoardFull() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (gameState[row][col] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public QLearningAI getWhiteAI() {
        return whiteAI;
    }
}
