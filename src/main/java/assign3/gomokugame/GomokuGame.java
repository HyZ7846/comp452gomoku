package assign3.gomokugame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GomokuGame extends Application {

    private static final int SIZE = 7;
    private static final int TILE_SIZE = 80;
    private final Circle[][] board = new Circle[SIZE][SIZE];
    private Label messageLabel;
    private QLearningAI whiteAI;
    private final int[][] gameState = new int[SIZE][SIZE];

    public static void main(String[] args) {
        GomokuLearning learningProcess = new GomokuLearning();
        learningProcess.selfLearn(1000); // Train the AI for 1000 games before starting the game

        Application.launch(args); // Launch the JavaFX application
    }

    @Override
    public void start(Stage primaryStage) {
        GomokuLearning learningProcess = new GomokuLearning();
        whiteAI = learningProcess.getWhiteAI();

        GridPane gridPane = createGrid();

        StackPane root = new StackPane();
        root.getChildren().add(gridPane);

        messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 24px; -fx-background-color: white; -fx-padding: 10px;");
        messageLabel.setVisible(false);
        root.getChildren().add(messageLabel);

        Scene scene = new Scene(root, SIZE * TILE_SIZE, SIZE * TILE_SIZE);
        primaryStage.setTitle("Gomoku Game - Game Start");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Create the game grid
    private GridPane createGrid() {
        GridPane gridPane = new GridPane();

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE);
                tile.setFill(Color.LIGHTGRAY); // Background color
                tile.setStroke(Color.BLACK); // Grid lines

                final int r = row;
                final int c = col;
                tile.setOnMouseClicked(event -> placeChip(gridPane, r, c, true));

                gridPane.add(tile, col, row);
            }
        }

        return gridPane;
    }

    // Place a chip on the board
    private void placeChip(GridPane gridPane, int row, int col, boolean isPlayer) {
        if (board[row][col] == null && !messageLabel.isVisible()) {
            Circle chip = new Circle((double) TILE_SIZE / 2, isPlayer ? Color.BLACK : Color.WHITE);
            board[row][col] = chip;
            gameState[row][col] = isPlayer ? 1 : -1;
            gridPane.add(chip, col, row);

            // Check for a win or a tie
            if (checkWin(row, col)) {
                String winner = isPlayer ? "Black" : "White";
                messageLabel.setText(winner + " wins!");
                messageLabel.setVisible(true);
                saveQTables();
            } else if (isBoardFull()) {
                messageLabel.setText("This game is a tie!!");
                messageLabel.setVisible(true);
                saveQTables();
            } else if (isPlayer) {
                aiMove(gridPane);
            }
        }
    }

    // AI makes a move
    private void aiMove(GridPane gridPane) {
        State currentState = new State(gameState);
        int[] action = whiteAI.chooseAction(currentState);
        placeChip(gridPane, action[0], action[1], false);
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

    // Save Q-tables
    private void saveQTables() {
        whiteAI.saveQTable("whiteAI_qtable.ser");
    }
}
