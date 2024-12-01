package com.example.a2048;

import java.util.ArrayList;
import java.util.List;

public class Game2048 {
    private int[][] board;
    private int score;
    private static final int BOARD_SIZE = 4;
    private boolean gameOver;

    public Game2048() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        score = 0;
        gameOver = false;
        addNewTile();
        addNewTile();
    }

    public void move(Direction direction) {
        if (gameOver) return;

        int[][] previousBoard = copyBoard();
        boolean moved = false;

        // First compress (move all tiles to the target direction)
        // Then merge same-value tiles
        // Then compress again to fill gaps created by merging
        switch (direction) {
            case LEFT:
                moved = moveLeft();
                break;
            case RIGHT:
                moved = moveRight();
                break;
            case UP:
                moved = moveUp();
                break;
            case DOWN:
                moved = moveDown();
                break;
        }

        // Only add a new tile if the board changed
        if (moved) {
            addNewTile();
            // Check for game over after adding new tile
            if (isGameOver()) {
                gameOver = true;
            }
        }
    }

    private boolean moveLeft() {
        boolean moved = false;
        for (int row = 0; row < BOARD_SIZE; row++) {
            // First compress
            moved |= compressRow(row, false);
            // Then merge
            moved |= mergeRow(row);
            // Compress again after merging
            compressRow(row, false);
        }
        return moved;
    }

    private boolean moveRight() {
        boolean moved = false;
        for (int row = 0; row < BOARD_SIZE; row++) {
            // First compress
            moved |= compressRow(row, true);
            // Then merge
            moved |= mergeRowReverse(row);
            // Compress again after merging
            compressRow(row, true);
        }
        return moved;
    }

    private boolean moveUp() {
        boolean moved = false;
        for (int col = 0; col < BOARD_SIZE; col++) {
            // First compress
            moved |= compressColumn(col, false);
            // Then merge
            moved |= mergeColumn(col);
            // Compress again after merging
            compressColumn(col, false);
        }
        return moved;
    }

    private boolean moveDown() {
        boolean moved = false;
        for (int col = 0; col < BOARD_SIZE; col++) {
            // First compress
            moved |= compressColumn(col, true);
            // Then merge
            moved |= mergeColumnReverse(col);
            // Compress again after merging
            compressColumn(col, true);
        }
        return moved;
    }

    private boolean compressRow(int row, boolean toRight) {
        boolean moved = false;
        int[] newRow = new int[BOARD_SIZE];
        int index = toRight ? BOARD_SIZE - 1 : 0;

        // Collect non-zero tiles
        for (int col = toRight ? BOARD_SIZE - 1 : 0;
             toRight ? col >= 0 : col < BOARD_SIZE;
             col += toRight ? -1 : 1) {
            if (board[row][col] != 0) {
                newRow[index] = board[row][col];
                if (col != index) moved = true;
                index += toRight ? -1 : 1;
            }
        }

        // Update the board row
        System.arraycopy(newRow, 0, board[row], 0, BOARD_SIZE);
        return moved;
    }

    private boolean compressColumn(int col, boolean toBottom) {
        boolean moved = false;
        int[] newCol = new int[BOARD_SIZE];
        int index = toBottom ? BOARD_SIZE - 1 : 0;

        // Collect non-zero tiles
        for (int row = toBottom ? BOARD_SIZE - 1 : 0;
             toBottom ? row >= 0 : row < BOARD_SIZE;
             row += toBottom ? -1 : 1) {
            if (board[row][col] != 0) {
                newCol[index] = board[row][col];
                if (row != index) moved = true;
                index += toBottom ? -1 : 1;
            }
        }

        // Update the board column
        for (int row = 0; row < BOARD_SIZE; row++) {
            board[row][col] = newCol[row];
        }
        return moved;
    }

    private boolean mergeRow(int row) {
        boolean merged = false;
        for (int col = 0; col < BOARD_SIZE - 1; col++) {
            if (board[row][col] != 0 && board[row][col] == board[row][col + 1]) {
                board[row][col] *= 2;
                score += board[row][col];
                board[row][col + 1] = 0;
                merged = true;
            }
        }
        return merged;
    }

    private boolean mergeRowReverse(int row) {
        boolean merged = false;
        for (int col = BOARD_SIZE - 1; col > 0; col--) {
            if (board[row][col] != 0 && board[row][col] == board[row][col - 1]) {
                board[row][col] *= 2;
                score += board[row][col];
                board[row][col - 1] = 0;
                merged = true;
            }
        }
        return merged;
    }

    private boolean mergeColumn(int col) {
        boolean merged = false;
        for (int row = 0; row < BOARD_SIZE - 1; row++) {
            if (board[row][col] != 0 && board[row][col] == board[row + 1][col]) {
                board[row][col] *= 2;
                score += board[row][col];
                board[row + 1][col] = 0;
                merged = true;
            }
        }
        return merged;
    }

    private boolean mergeColumnReverse(int col) {
        boolean merged = false;
        for (int row = BOARD_SIZE - 1; row > 0; row--) {
            if (board[row][col] != 0 && board[row][col] == board[row - 1][col]) {
                board[row][col] *= 2;
                score += board[row][col];
                board[row - 1][col] = 0;
                merged = true;
            }
        }
        return merged;
    }

    private void addNewTile() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }

        if (!emptyCells.isEmpty()) {
            int[] cell = emptyCells.get((int) (Math.random() * emptyCells.size()));
            board[cell[0]][cell[1]] = Math.random() < 0.9 ? 2 : 4;
        }
    }

    public boolean isGameOver() {
        // Check for empty cells
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }

        // Check for possible merges horizontally
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE - 1; j++) {
                if (board[i][j] == board[i][j + 1]) {
                    return false;
                }
            }
        }

        // Check for possible merges vertically
        for (int j = 0; j < BOARD_SIZE; j++) {
            for (int i = 0; i < BOARD_SIZE - 1; i++) {
                if (board[i][j] == board[i + 1][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    private int[][] copyBoard() {
        int[][] copy = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, BOARD_SIZE);
        }
        return copy;
    }

    public int[][] getBoard() {
        return board;
    }

    public int getScore() {
        return score;
    }

    public boolean isGameOverState() {
        return gameOver;
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}