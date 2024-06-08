package org.fat;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    private Label Advertencia;

    @FXML
    private TextField txtnumSector;

    @FXML
    private TextField txtcluster;
    @FXML
    private TableView<SimpleIntegerProperty> tablaFATvisual;




    //Comportamiento de los Botones para la Navegación

    @FXML
    protected void IniciarSimulacion(ActionEvent event) throws IOException {

        String numSectorText = txtnumSector.getText();
        String numClusterText = txtcluster.getText();

        try {
            int numSector = Integer.parseInt(numSectorText);
            int numCluster = Integer.parseInt(numClusterText);

            if (numSector > 0 && numCluster > 0) {

                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("view.fxml"));
                Scene scene = new Scene(root);
                stage.setTitle("Simulador FAT16 - Tabla de Asignación de Archivos");
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();

                Advertencia.setText("Error al iniciar");
            } else {
                mostrarAlerta("Por favor, ingresa números mayores a cero.");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Por favor, ingresa números válidos en ambos campos.");
        }
    }

    @FXML
    protected void IrGestor(ActionEvent event) throws IOException {

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Vista_exterior.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    protected void Regresar(ActionEvent event) throws IOException {

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("view.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de validación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}