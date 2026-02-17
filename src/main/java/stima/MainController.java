package stima;

import stima.model.Board;
import stima.utils.InputLoader;
import stima.solver.Solver;
import stima.solver.SolverAlgorithm;
import stima.solver.BruteForce;
import stima.solver.OptimationBruteForce;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class MainController {

    @FXML private GridPane board_grid;
    @FXML private Label status_label;
    @FXML private Label iteration_label;
    @FXML private Label time_label;
    @FXML private Slider speed_slider;
    @FXML private Label speed_label;
    @FXML private Button solve_button;
    @FXML private Button save_button;
    @FXML private ComboBox<Solver.AlgorithmType> algo_combo_box;
    @FXML private CheckBox visualize_check_box;
    @FXML private TextArea solution_text_area;

    private SolverAlgorithm active_solver;
    private Thread solver_thread;
    private Board current_board;

    private final String STYLE_SOLVE = "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20 10 20; -fx-background-radius: 5;";
    private final String STYLE_STOP  = "-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20 10 20; -fx-background-radius: 5;";
    
    @FXML
    public void initialize() {
        speed_slider.valueProperty().addListener((obs, old_val, new_val) -> speed_label.setText(String.format("%.0f ms", new_val)));
        algo_combo_box.getItems().setAll(Solver.AlgorithmType.values());
        algo_combo_box.getSelectionModel().select(Solver.AlgorithmType.BRUTE_FORCE);
        solve_button.setStyle(STYLE_SOLVE);
        solve_button.setDisable(true);
    }

    @FXML
    private void handleLoadFile() {
        save_button.setDisable(true);
        solve_button.setDisable(false);
        
        FileChooser file_chooser = new FileChooser();
        file_chooser.setTitle("Pilih File Test Case");
        file_chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        
        File file = file_chooser.showOpenDialog(board_grid.getScene().getWindow());
        
        if (file != null) {
            try {
                Board new_board = InputLoader.loadBoard(file);
                inputLoaded(new_board, "File: " + file.getName());
            }
            catch (IOException e) {
                handleInputError(e);
            }
        }
    }

    @FXML
    private void handleApplyInput() {
        String text_input = solution_text_area.getText();
        
        try {
            Board new_board = InputLoader.loadBoardFromString(text_input);
            inputLoaded(new_board, "Input Manual");
        }
        catch (IOException e) {
            handleInputError(e);
        }
        catch (Exception e) {
            handleInputError(new Exception("Terjadi kesalahan tak terduga: " + e.getMessage()));
        }
    }

    @FXML
    private void handleSolve() {
        if (current_board == null) {
            status_label.setText("Harap masukkan input terlebih dahulu!");
            status_label.setStyle("-fx-text-fill: red;");
            return;
        }

        if (active_solver != null && solver_thread != null && solver_thread.isAlive()) {
            active_solver.stop();
            status_label.setText("Dihentikan oleh pengguna.");
            status_label.setStyle("-fx-text-fill: black;");
            
            solve_button.setText("Solve");
            solve_button.setStyle(STYLE_SOLVE);
            return;
        }

        int delay = (int) speed_slider.getValue();
        boolean visualize = visualize_check_box.isSelected();

        if (!visualize) delay = 0;

        solve_button.setText("Stop");
        solve_button.setStyle(STYLE_STOP);
        
        save_button.setDisable(true);
        
        Solver.AlgorithmType selected_algo = algo_combo_box.getValue();
        
        if (selected_algo == Solver.AlgorithmType.BRUTE_FORCE) {
            active_solver = new BruteForce(current_board, delay, visualize, this::solverUpdate, this::solverFinished);
        }
        else {
            active_solver = new OptimationBruteForce(current_board, delay, visualize, this::solverUpdate, this::solverFinished);
        }

        solver_thread = new Thread(active_solver);
        solver_thread.setDaemon(true);
        solver_thread.start();
    }

    @FXML
    private void handleSave() {
        if (current_board == null) return;

        FileChooser file_chooser = new FileChooser();
        file_chooser.setTitle("Simpan Solusi");
        
        file_chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text File (*.txt)", "*.txt"),
            new FileChooser.ExtensionFilter("PNG Image (*.png)", "*.png")
        );

        file_chooser.setInitialFileName("solution");

        File file = file_chooser.showSaveDialog(board_grid.getScene().getWindow());

        if (file != null) {
            String file_name = file.getName().toLowerCase();
            
            if (file_name.endsWith(".png")) {
                saveAsImage(file);
            }
            else {
                saveAsText(file);
            }
        }
    }

    private void inputLoaded(Board board, String source_info) {
        this.current_board = board;
    
        drawBoard(current_board);
        solution_text_area.setText(current_board.getSolutionString());
        status_label.setText("Berhasil dimuat (" + source_info + "). Ukuran: " + current_board.getSize() + "x" + current_board.getSize());
        status_label.setStyle("-fx-text-fill: blue;");
        iteration_label.setText("0 kasus");
        time_label.setText("0 ms");
        solve_button.setDisable(false);
        solve_button.setText("Solve");
        solve_button.setStyle(STYLE_SOLVE);
        save_button.setDisable(true);
    }

    private void handleInputError(Exception e) {
        status_label.setText("Error: " + e.getMessage());
        status_label.setStyle("-fx-text-fill: red;");
        solve_button.setDisable(true);
        save_button.setDisable(true);
        board_grid.getChildren().clear();
        current_board = null;
        showErrorAlert(e.getMessage());
    }

    private void solverUpdate(Solver.SolverStatus status) {
        drawBoard(current_board);
        iteration_label.setText(status.iterations() + " kasus");
        time_label.setText(status.timeMs() + " ms");
        status_label.setText(status.message());
        
        if (status.finished() && status.message().contains("Ditemukan")) {
            status_label.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        }
        else if (status.finished() && status.message().contains("Tidak ada solusi")) {
            status_label.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }
        else {
            status_label.setStyle("-fx-text-fill: black;");
        }
    }

    private void solverFinished() {
        solve_button.setText("Solve");
        solve_button.setStyle(STYLE_SOLVE);
        
        solution_text_area.setText(current_board.getSolutionString());
        
        if (status_label.getText().contains("Ditemukan")) {
            save_button.setDisable(false);
        }
        else {
            save_button.setDisable(true);
        }
    }

    private void saveAsText(File file) {
        boolean success = false;

        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print(current_board.getSolutionString());
            success = true;
        }
        catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Gagal menyimpan file: " + e.getMessage());
        }

        if (success) {
            showSuccessAlert("Solusi teks berhasil disimpan di:\n" + file.getAbsolutePath());
        }
    }

    private void saveAsImage(File file) {
        try {
            WritableImage image = generateSnapshot(current_board); 
            
            boolean written = ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            
            if (written) {
                showSuccessAlert("Gambar papan berhasil disimpan di:\n" + file.getAbsolutePath());
            }
            else {
                showErrorAlert("Gagal menyimpan gambar. Format writer tidak ditemukan.");
            }
        }
        catch (IOException e) {
            showErrorAlert("Gagal menyimpan gambar: " + e.getMessage());
        }
        catch (Exception e) {
            showErrorAlert("Terjadi kesalahan tak terduga: " + e.getMessage());
        }
    }

    private WritableImage generateSnapshot(Board board) {
        int size = board.getSize();
        double fixed_cell_size = 64; 

        GridPane temp_grid = new GridPane();
        temp_grid.setHgap(1);
        temp_grid.setVgap(1);
        temp_grid.setStyle("-fx-background-color: #333; -fx-padding: 2;");

        for (int row=0; row<size; row++) {
            for (int col=0; col<size; col++) {
                char region_char = board.getRegion(row, col);
                Color region_color = getColorRegion(region_char);

                Rectangle rect = new Rectangle(fixed_cell_size, fixed_cell_size);
                rect.setFill(region_color);
                rect.setStroke(Color.BLACK);
                rect.setStrokeWidth(0.5);

                StackPane cell_pane = new StackPane();
                cell_pane.getChildren().add(rect);

                if (board.hasQueen(row, col)) {
                    Text queen_marker = new Text("♛");
                    queen_marker.setStyle("-fx-font-size: " + (fixed_cell_size * 0.6) + "px; -fx-fill: black;");
                    cell_pane.getChildren().add(queen_marker);
                }

                temp_grid.add(cell_pane, col, row);
            }
        }

        new javafx.scene.Scene(temp_grid); 
        SnapshotParameters params = new SnapshotParameters();
        params.setTransform(javafx.scene.transform.Transform.scale(1.0, 1.0)); 
        return temp_grid.snapshot(params, null);
    }

    public void drawBoard(Board board) {
        board_grid.getChildren().clear();
        int size = board.getSize();
        double cell_size = calculateCellSize(size);

        for (int row=0; row<size; row++) {
            for (int col=0; col<size; col++) {
                char region_char = board.getRegion(row, col);
                Color region_color = getColorRegion(region_char);

                Rectangle rect = new Rectangle(cell_size, cell_size);
                rect.setFill(region_color);
                rect.setStroke(Color.BLACK);
                rect.setStrokeWidth(0.5);

                StackPane cell_pane = new StackPane();
                cell_pane.getChildren().add(rect);

                if (board.hasQueen(row, col)) {
                    Text queen_marker = new Text("♛");
                    queen_marker.setStyle("-fx-font-size: " + (cell_size * 0.6) + "px; -fx-fill: black;");
                    cell_pane.getChildren().add(queen_marker);
                }

                board_grid.add(cell_pane, col, row);
            }
        }
    }

    private Color getColorRegion(char region) {
        double hue = ((region - 'A') * 67) % 360;
        return Color.hsb(hue, 0.6, 0.9);
    }
    
    private double calculateCellSize(int board_size) {
        if (board_size <= 10) return 50;
        if (board_size <= 20) return 35;
        return 20;
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Terjadi Kesalahan");
        alert.setContentText(message);
        alert.showAndWait();
    }
}