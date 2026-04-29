package com.connect4;

import java.util.ArrayList;
import java.util.List;

public class Board implements Cloneable {
    public static final int ROWS = 6;
    public static final int COLS = 7;
    public static final char EMPTY_SLOT = '.';
    public static final char PLAYER_1 = 'X'; // Human
    public static final char PLAYER_2 = 'O'; // AI

    private char[][] grid;

    public Board() {
        grid = new char[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c] = EMPTY_SLOT;
            }
        }
    }

    public void setCell(int r, int c, char piece) {
        if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
            grid[r][c] = piece;
        }
    }
    
    public char getCell(int r, int c) {
        return grid[r][c];
    }

    public void printBoard() {
        System.out.println();
        for (int r = 0; r < ROWS; r++) {
            System.out.print("| ");
            for (int c = 0; c < COLS; c++) {
                System.out.print(grid[r][c] + " | ");
            }
            System.out.println();
        }
        System.out.println("-----------------------------");
        System.out.println("  1   2   3   4   5   6   7  ");
        System.out.println();
    }

    public boolean isColumnFull(int col) {
        if (col < 0 || col >= COLS) return true;
        return grid[0][col] != EMPTY_SLOT;
    }

    public boolean isFull() {
        for (int c = 0; c < COLS; c++) {
            if (!isColumnFull(c)) {
                return false;
            }
        }
        return true;
    }

    public boolean dropPiece(int col, char piece) {
        if (col < 0 || col >= COLS || isColumnFull(col)) {
            return false;
        }

        for (int r = ROWS - 1; r >= 0; r--) {
            if (grid[r][col] == EMPTY_SLOT) {
                grid[r][col] = piece;
                return true;
            }
        }
        return false;
    }

    public boolean checkWin(char piece) {
        // Vízszintes ellenőrzés
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS - 3; c++) {
                if (grid[r][c] == piece && grid[r][c+1] == piece &&
                    grid[r][c+2] == piece && grid[r][c+3] == piece) {
                    return true;
                }
            }
        }

        // Függőleges ellenőrzés
        for (int c = 0; c < COLS; c++) {
            for (int r = 0; r < ROWS - 3; r++) {
                if (grid[r][c] == piece && grid[r+1][c] == piece &&
                    grid[r+2][c] == piece && grid[r+3][c] == piece) {
                    return true;
                }
            }
        }

        // Főátló ellenőrzés
        for (int r = 0; r < ROWS - 3; r++) {
            for (int c = 0; c < COLS - 3; c++) {
                if (grid[r][c] == piece && grid[r+1][c+1] == piece &&
                    grid[r+2][c+2] == piece && grid[r+3][c+3] == piece) {
                    return true;
                }
            }
        }

        // Mellékátló ellenőrzés
        for (int r = 0; r < ROWS - 3; r++) {
            for (int c = 3; c < COLS; c++) {
                if (grid[r][c] == piece && grid[r+1][c-1] == piece &&
                    grid[r+2][c-2] == piece && grid[r+3][c-3] == piece) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<Integer> getValidMoves() {
        List<Integer> validMoves = new ArrayList<>();
        for (int c = 0; c < COLS; c++) {
            if (!isColumnFull(c)) {
                validMoves.add(c);
            }
        }
        return validMoves;
    }

    public void removePiece(int col) {
        for (int r = 0; r < ROWS; r++) {
            if (grid[r][col] != EMPTY_SLOT) {
                grid[r][col] = EMPTY_SLOT;
                break;
            }
        }
    }

    @Override
    public Board clone() {
        Board newBoard = new Board();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                newBoard.grid[r][c] = this.grid[r][c];
            }
        }
        return newBoard;
    }

    public int evaluate(char aiPiece) {
        char oppPiece = (aiPiece == PLAYER_1) ? PLAYER_2 : PLAYER_1;
        int score = 0;

        // Értékeljük a középső oszlopot (nagyon értékes)
        int centerCount = 0;
        int centerCol = COLS / 2;
        for (int r = 0; r < ROWS; r++) {
            if (grid[r][centerCol] == aiPiece) {
                centerCount++;
            }
        }
        score += centerCount * 3;

        // Vízszintes
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS - 3; c++) {
                char[] window = {grid[r][c], grid[r][c+1], grid[r][c+2], grid[r][c+3]};
                score += evaluateWindow(window, aiPiece, oppPiece);
            }
        }

        // Függőleges
        for (int c = 0; c < COLS; c++) {
            for (int r = 0; r < ROWS - 3; r++) {
                char[] window = {grid[r][c], grid[r+1][c], grid[r+2][c], grid[r+3][c]};
                score += evaluateWindow(window, aiPiece, oppPiece);
            }
        }

        // Főátló
        for (int r = 0; r < ROWS - 3; r++) {
            for (int c = 0; c < COLS - 3; c++) {
                char[] window = {grid[r][c], grid[r+1][c+1], grid[r+2][c+2], grid[r+3][c+3]};
                score += evaluateWindow(window, aiPiece, oppPiece);
            }
        }

        // Mellékátló
        for (int r = 0; r < ROWS - 3; r++) {
            for (int c = 3; c < COLS; c++) {
                char[] window = {grid[r][c], grid[r+1][c-1], grid[r+2][c-2], grid[r+3][c-3]};
                score += evaluateWindow(window, aiPiece, oppPiece);
            }
        }

        return score;
    }

    private int evaluateWindow(char[] window, char aiPiece, char oppPiece) {
        int score = 0;
        int aiCount = 0;
        int oppCount = 0;
        int emptyCount = 0;

        for (char p : window) {
            if (p == aiPiece) aiCount++;
            else if (p == oppPiece) oppCount++;
            else emptyCount++;
        }

        if (aiCount == 4) {
            score += 100;
        } else if (aiCount == 3 && emptyCount == 1) {
            score += 5;
        } else if (aiCount == 2 && emptyCount == 2) {
            score += 2;
        }

        if (oppCount == 3 && emptyCount == 1) {
            score -= 4;
        }

        return score;
    }
}
