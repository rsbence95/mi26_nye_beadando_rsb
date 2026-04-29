package com.connect4;

import java.util.List;

public class MinimaxAI {
    private final char aiPiece;
    private final char opponentPiece;
    private final int maxDepth;

    public MinimaxAI(char aiPiece, char opponentPiece, int maxDepth) {
        this.aiPiece = aiPiece;
        this.opponentPiece = opponentPiece;
        this.maxDepth = maxDepth;
    }

    public int getBestMove(Board board) {
        List<Integer> validMoves = board.getValidMoves();
        int bestScore = Integer.MIN_VALUE;
        int bestCol = validMoves.get(0);

        for (int col : validMoves) {
            board.dropPiece(col, aiPiece);
            int score = minimax(board, maxDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            board.removePiece(col);

            if (score > bestScore) {
                bestScore = score;
                bestCol = col;
            }
        }
        return bestCol;
    }

    private int minimax(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        boolean isTerminal = board.checkWin(aiPiece) || board.checkWin(opponentPiece) || board.isFull();
        if (depth == 0 || isTerminal) {
            if (isTerminal) {
                if (board.checkWin(aiPiece)) {
                    return 10000000;
                } else if (board.checkWin(opponentPiece)) {
                    return -10000000;
                } else {
                    return 0; // Döntetlen
                }
            } else {
                return board.evaluate(aiPiece);
            }
        }

        if (maximizingPlayer) {
            int value = Integer.MIN_VALUE;
            for (int col : board.getValidMoves()) {
                board.dropPiece(col, aiPiece);
                value = Math.max(value, minimax(board, depth - 1, alpha, beta, false));
                board.removePiece(col);
                alpha = Math.max(alpha, value);
                if (alpha >= beta) {
                    break; // Béta vágás
                }
            }
            return value;
        } else {
            int value = Integer.MAX_VALUE;
            for (int col : board.getValidMoves()) {
                board.dropPiece(col, opponentPiece);
                value = Math.min(value, minimax(board, depth - 1, alpha, beta, true));
                board.removePiece(col);
                beta = Math.min(beta, value);
                if (alpha >= beta) {
                    break; // Alfa vágás
                }
            }
            return value;
        }
    }
}
