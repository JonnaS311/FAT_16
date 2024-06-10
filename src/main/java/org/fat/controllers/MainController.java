package org.fat.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.fat.TablaDirectorios;
import org.fat.TablaFAT;

import java.io.IOException;

public class MainController {
    @FXML
    private Label Advertencia;

    @FXML
    private TextField txtnumSector;

    @FXML
    private TextField txtcluster;

    @FXML
    private TextField txtbad;

    //Comportamiento de los Botones para la Navegación

    @FXML
    protected void IniciarSimulacion(ActionEvent event) throws IOException {
        String numSectorText = txtnumSector.getText();
        String numClusterText = txtcluster.getText();
        String numBadText = txtbad.getText();

        try {
            int numSector = Integer.parseInt(numSectorText);
            int numCluster = Integer.parseInt(numClusterText);
            double numBad = Double.parseDouble(numBadText);

            if (numSector > 0 && numCluster > 0) {
                TablaFAT tablaFAT = new TablaFAT(numCluster, numSector, numBad);
                TablaDirectorios tablaDirectorios = new TablaDirectorios(tablaFAT);

                // Cargar la nueva escena
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../view.fxml"));
                Parent root = loader.load();

                // Obtener el controlador de la nueva escena y pasarle los datos
                TablaFATController tablaFATController = loader.getController();
                tablaFATController.setTablaFATData(tablaFAT, tablaDirectorios);

                // Mostrar la nueva escena
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setTitle("Simulador FAT16 - Tabla de Asignación de Archivos");
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();

                Advertencia.setText("");
            } else {
                Advertencia.setText("Por favor, ingresa números mayores a cero.");
            }
        } catch (NumberFormatException e) {
            Advertencia.setText("Por favor, ingresa números válidos en ambos campos.");
        }
    }
}