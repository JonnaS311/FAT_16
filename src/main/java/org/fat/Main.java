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
        System.out.println(Main.class.getResource("Configuracion.fxml"));
        Parent fxmlLoader = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("View.fxml")));
        Scene scene = new Scene(fxmlLoader, 800, 400);
        stage.setTitle("Simulador FAT-16");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        TablaFAT test = new TablaFAT(65524,64, 0.0);

        test.createFile(62*65524*512);
        test.createFile(160000);
        test.createFile(56);
        test.deleteFile(63477);
        System.out.println(Arrays.toString(test.getTable()));
        System.out.println(test.getTable_first_cluster());



        launch();

    }
}

