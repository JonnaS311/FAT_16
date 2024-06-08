package org.fat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Arrays;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Configuracion.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 400);
        stage.setTitle("Simulador FAT-16");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        TablaFAT test = new TablaFAT(65524,64, 0.0);

        test.CreateFile(62*65524*512);
        test.CreateFile(160000);
        test.CreateFile(56);
        test.DeleteFile(63477);
        System.out.println(Arrays.toString(test.getTable()));
        System.out.println(test.getTable_first_cluster());

        for (int[] array : test.getFormattedTable()) {
            for (int num : array) {
                System.out.print(num + " ");
            }
            System.out.println();
        }

        launch();

    }
}
