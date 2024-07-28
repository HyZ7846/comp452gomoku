# Gomoku Game

## Description

Gomoku is a strategy board game where two players take turns placing black and white chips on a 7x7 grid. The objective is to be the first to get five of your chips in a row, either horizontally, vertically, or diagonally. This implementation of Gomoku includes a player versus AI mode, where the player uses black chips and the AI uses white chips.

### Game Logic and Design

- **Player vs. AI**: The player places black chips, while the AI places white chips.
- **AI Training**: The game requires around 10 seconds to train AI models before the game start.
- **AI Strategy**:
    - **Winning Move Priority**: The AI first checks if it can place a chip to form a five-in-a-row to win the game.
    - **Defensive Move**: If the AI detects that the player has three chips in a row, it will prioritize blocking the player to prevent them from forming four-in-a-row or five-in-a-row.
    - **Normal Move**: If no immediate winning or defensive move is needed, the AI will place a chip in a position that provides the best potential for future winning moves.
- **Game End Conditions**:
    - A player wins if they place five chips in a row, either horizontally, vertically, or diagonally.
    - The game ends in a tie if the board is full and no player has achieved five in a row.

### Known Issues
- **AI Performance**: The AI might occasionally take longer to make a decision, especially as the board fills up.
- **Edge Cases**: There may be rare edge cases where the AI does not block optimally due to the complexity of board state evaluation.
- **Saved Q-Tables**: If the saved Q-tables become corrupted, the AI's performance may degrade. Ensure proper handling of these `qtable.ser` files.
