package com.connect4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Game {
    private final Board board;
    private final Scanner scanner;
    private final MinimaxAI ai;

    public Game() {
        this.board = new Board();
        this.scanner = new Scanner(System.in);
        // Minimax AI inicializálása a 2-es játékosként (O), 8-as mélységgel
        this.ai = new MinimaxAI(Board.PLAYER_2, Board.PLAYER_1, 8);
    }

    public void start() {
        System.out.print("Szeretnél beolvasni egy kezdőállást fájlból? (i/n): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        if (answer.equals("i") || answer.equals("igen")) {
            System.out.print("Add meg a fájl nevét (pl. initial_state.txt): ");
            String filename = scanner.nextLine().trim();
            loadBoardFromFile(filename);
        }

        boolean gameRunning = true;
        boolean isPlayer1Turn = isPlayer1TurnInitial();

        while (gameRunning) {
            board.printBoard();

            if (board.checkWin(Board.PLAYER_1)) {
                System.out.println("Gratulálok! Te nyertél (" + Board.PLAYER_1 + ")!");
                break;
            } else if (board.checkWin(Board.PLAYER_2)) {
                System.out.println("Az AI nyert (" + Board.PLAYER_2 + ")!");
                break;
            } else if (board.isFull()) {
                System.out.println("Döntetlen! A tábla megtelt.");
                break;
            }
            
            if (isPlayer1Turn) {
                System.out.print("Te következel (" + Board.PLAYER_1 + "). Válassz egy oszlopot (1-7): ");
                String input = scanner.nextLine();
                int col;
                try {
                    col = Integer.parseInt(input) - 1; // 0-indexed beállítás
                } catch (NumberFormatException e) {
                    System.out.println("Érvénytelen bemenet! Kérlek számot adj meg.");
                    continue;
                }

                if (col < 0 || col >= Board.COLS) {
                    System.out.println("Érvénytelen oszlop! Kérlek 1 és 7 közötti számot adj meg.");
                    continue;
                }

                if (board.isColumnFull(col)) {
                    System.out.println("Ez az oszlop már megtelt! Válassz másikat.");
                    continue;
                }

                board.dropPiece(col, Board.PLAYER_1);
            } else {
                System.out.println("Az AI (" + Board.PLAYER_2 + ") gondolkodik (mélység: 8)...");
                long startTime = System.currentTimeMillis();
                int col = ai.getBestMove(board);
                long endTime = System.currentTimeMillis();
                System.out.println("Az AI a(z) " + (col + 1) + ". oszlopot választotta. (" + (endTime - startTime) + " ms)");
                board.dropPiece(col, Board.PLAYER_2);
            }

            isPlayer1Turn = !isPlayer1Turn;
        }
    }

    private void loadBoardFromFile(String filename) {
        if (!Files.exists(Paths.get(filename))) {
            System.out.println("A fájl nem található: " + filename + ". Üres táblával indulunk.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            int row = 0;
            String line;
            while ((line = br.readLine()) != null && row < Board.ROWS) {
                line = line.replace(" ", "");
                for (int col = 0; col < Math.min(line.length(), Board.COLS); col++) {
                    char c = line.charAt(col);
                    if (c == Board.PLAYER_1 || c == Board.PLAYER_2 || c == Board.EMPTY_SLOT) {
                        board.setCell(row, col, c);
                    }
                }
                row++;
            }
            System.out.println("Kezdőállás sikeresen betöltve!");
        } catch (IOException e) {
            System.out.println("Hiba történt a fájl olvasásakor. Üres táblával indulunk. Hiba: " + e.getMessage());
        }
    }

    private boolean isPlayer1TurnInitial() {
        int p1Count = 0;
        int p2Count = 0;
        for (int r = 0; r < Board.ROWS; r++) {
            for (int c = 0; c < Board.COLS; c++) {
                if (board.getCell(r, c) == Board.PLAYER_1) p1Count++;
                else if (board.getCell(r, c) == Board.PLAYER_2) p2Count++;
            }
        }
        return p1Count <= p2Count; // Ha X-ből nincs több mint O-ból, akkor X (Player 1) jön.
    }
}
