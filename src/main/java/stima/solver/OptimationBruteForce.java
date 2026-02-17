package stima.solver;

import stima.model.Board;
import stima.solver.Solver.SolverStatus; 

import javafx.application.Platform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class OptimationBruteForce implements SolverAlgorithm {
    private Board board;
    private int delay_ms;
    private long iteration_cnt;
    private long start_time;
    private Consumer<SolverStatus> on_update;
    private Runnable on_finish;
    private volatile boolean running = true;
    private boolean solution_found = false;
    private Map<Character, List<Point>> region_cells;
    private List<Character> region_order;
    private record Point(int row, int col) {}
    private boolean visualize;

    public OptimationBruteForce(Board board, int delay_ms, boolean visualize, Consumer<SolverStatus> on_update, Runnable on_finish) {
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
        prepareRegions();
        solution_found = solveByRegion(0);
        long end_time = System.currentTimeMillis();
        
        Platform.runLater(() -> {
            String msg;
            int total_regions = region_order.size();
            int board_size = board.getSize();
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
            on_update.accept(new SolverStatus(iteration_cnt, end_time-start_time, true, msg));
            on_finish.run();
        });
    }

    private void prepareRegions() {
        region_cells = new HashMap<>();
        region_order = new ArrayList<>();
        int size = board.getSize();

        for (int r=0; r<size; r++) {
            for (int c=0; c<size; c++) {
                char region_char = board.getRegion(r, c);
                if (!region_cells.containsKey(region_char)) {
                    region_cells.put(region_char, new ArrayList<>());
                    region_order.add(region_char);
                }
                region_cells.get(region_char).add(new Point(r, c));
            }
        }
    }

    private boolean solveByRegion(int region_idx) {
        if (!running) return false;
        if (region_idx == region_order.size()) return true;

        char current_region_char = region_order.get(region_idx);
        List<Point> available_cells = region_cells.get(current_region_char);

        for (Point p : available_cells) {
            if (!running) return false;
            board.setQueen(p.row(), p.col(), true);
            iteration_cnt++;
            if (visualize) updateUI();
            if (!running) return false;
            board.setQueen(p.row(), p.col(), false);
            if (board.isSafe(p.row(), p.col())) {
                board.setQueen(p.row(), p.col(), true);
                if (solveByRegion(region_idx + 1)) return true;
                if (!running) return false;
                board.setQueen(p.row(), p.col(), false);
            }
        }

        return false;
    }

    private void updateUI() {
        if (delay_ms > 50 || iteration_cnt % 1000 == 0) {
            long currentTime = System.currentTimeMillis();
            Platform.runLater(() -> {on_update.accept(new SolverStatus(iteration_cnt, currentTime - start_time, false, "Solving..."));});

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