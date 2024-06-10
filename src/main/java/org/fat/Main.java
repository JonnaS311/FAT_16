package org.fat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent fxmlLoader = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("Configuracion.fxml")));
        Scene scene = new Scene(fxmlLoader);
        stage.setTitle("Simulador FAT-16");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}

