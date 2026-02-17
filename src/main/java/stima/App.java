package stima;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/stima/layout.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        stage.setTitle("LinkedIn Queens Solver");
        
        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/stima/logo.png")));
            stage.getIcons().add(icon);
        }
        catch (Exception e) {
            System.out.println("Gagal memuat logo: " + e.getMessage());
        }

        stage.setWidth(1100); 
        stage.setHeight(750);
        
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}