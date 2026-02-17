package stima.model;

public class Board {
    private int size;
    private char[][] region_map;
    private boolean[][] queens;

    public Board(int size, char[][] region_map) {
        this.size = size;
        this.region_map = region_map;
        this.queens = new boolean[size][size];
    }

    public int getSize() {
        return size;
    }

    public char getRegion(int row, int col) {
        return region_map[row][col];
    }

    public boolean hasQueen(int row, int col) {
        return queens[row][col];
    }

    public void setQueen(int row, int col, boolean placed) {
        queens[row][col] = placed;
    }

    public boolean isSafe(int row, int col) {
        char current_region = region_map[row][col];
        for (int r=0; r<size; r++) {
            for (int c=0; c<size; c++) {
                if (queens[r][c]) {
                    if (r == row || c == col) return false;
                    if (region_map[r][c] == current_region) return false;
                    if (Math.abs(r - row) <= 1 && Math.abs(c - col) <= 1) return false;
                }
            }
        }
        return true;
    }
    
    public String getSolutionString() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                if (queens[i][j]) sb.append("#");
                else sb.append(region_map[i][j]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public void clearQueens() {
        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++) {
                queens[i][j] = false;
            }
        }
    }
}