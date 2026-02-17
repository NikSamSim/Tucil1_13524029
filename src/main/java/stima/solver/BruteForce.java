package stima.solver;

import stima.model.Board;
import javafx.application.Platform;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BruteForce implements SolverAlgorithm {
    private Board board;
    private int delay_ms;
    private long iteration_cnt;
    private long start_time;
    private Consumer<Solver.SolverStatus> on_update;
    private Runnable on_finish;
    private boolean visualize;
    
    private volatile boolean running = true;
    private boolean solution_found = false;
    
    private int total_regions;
    private int board_size;
    private int total_cells;

    public BruteForce(Board board, int delay_ms, boolean visualize, Consumer<Solver.SolverStatus> on_update, Runnable on_finish) {
        this.board = board;
        this.delay_ms = delay_ms;
        this.visualize = visualize;
        this.on_update = on_update;
        this.on_finish = on_finish;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        start_time = System.currentTimeMillis();
        iteration_cnt = 0;
        board.clearQueens();
        this.board_size = board.getSize();
        this.total_cells = board_size * board_size;
        
        List<Character> regions = new ArrayList<>();
        for(int r=0; r<board_size; r++) {
            for(int c=0; c<board_size; c++) {
                char reg = board.getRegion(r, c);
                if(!regions.contains(reg)) regions.add(reg);
            }
        }
        this.total_regions = regions.size();

        solveCombination(0, 0);
        long end_time = System.currentTimeMillis();

        Platform.runLater(() -> {
            String msg;
            if (total_regions < board_size) {
                msg = "Jumlah wilayah < N. Tidak ada solusi karena akan terdapat kolom/baris yang tidak memiliki queen.";
            } 
            else if (total_regions > board_size) {
                 msg = "Jumlah wilayah > N. Tidak ada solusi karena selalu terdapat sebuah baris dan kolom yang memiliki queen > 1 dalam setiap konfigurasi.";
            } 
            else if (solution_found) {
                msg = "Solusi Ditemukan!";
            } 
            else {
                msg = "Tidak ada solusi.";
            }         
            on_update.accept(new Solver.SolverStatus(iteration_cnt, end_time - start_time, true, msg));
            on_finish.run();
        });
    }

    private void solveCombination(int start_cell_idx, int queens_placed) {
        if (!running || solution_found) return;

        if (queens_placed == total_regions) {
            iteration_cnt++;
            if (visualize) updateUI();
            if (isValidConfiguration()) {
                solution_found = true;
            }
            return;
        }

        for (int i=start_cell_idx; i<total_cells; i++) {
            int r = i / board_size;
            int c = i % board_size;
            board.setQueen(r, c, true);
            solveCombination(i+1, queens_placed+1);
            if (solution_found || !running) return;
            board.setQueen(r, c, false);
        }
    }

    private boolean isValidConfiguration() {
        List<int[]> queens = new ArrayList<>();
        for (int r=0; r<board_size; r++) {
            for (int c=0; c<board_size; c++) {
                if (board.hasQueen(r, c)) {
                    queens.add(new int[]{r, c});
                }
            }
        }

        for (int i=0; i<queens.size(); i++) {
            for (int j=i+1; j<queens.size(); j++) {
                int[] q1 = queens.get(i);
                int[] q2 = queens.get(j);
                int r1 = q1[0], c1 = q1[1];
                int r2 = q2[0], c2 = q2[1];
                if (r1 == r2) return false;
                if (c1 == c2) return false;
                if (board.getRegion(r1, c1) == board.getRegion(r2, c2)) return false;
                if (Math.abs(r1 - r2) <= 1 && Math.abs(c1 - c2) <= 1) return false;
            }
        }
        return true;
    }

    private void updateUI() {
        if (delay_ms > 50 || iteration_cnt % 1000 == 0) {
            long currentTime = System.currentTimeMillis();
            Platform.runLater(() -> {on_update.accept(new Solver.SolverStatus(iteration_cnt, currentTime - start_time, false, "Solving..."));});
            
            if (delay_ms > 0) {
                try {
                    Thread.sleep(delay_ms);
                }
                catch (InterruptedException e) {
                    running = false;
                }
            }
        }
    }
}