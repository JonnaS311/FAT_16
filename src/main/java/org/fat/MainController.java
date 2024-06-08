package org.fat;

import javafx.beans.property.SimpleObjectProperty;
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
    private TableView<ObservableList<Integer>> tablaFATvisual;




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

                TablaFAT tablaFAT = new TablaFAT(numCluster, numSector, 0.0);

                // Crear una lista observable para almacenar los datos de la tabla
                ObservableList<ObservableList<Integer>> data = FXCollections.observableArrayList();

                // Obtener los datos de la tabla y agregarlos a la lista observable
                for (int[] fila : tablaFAT.getFormattedTable()) {
                    ObservableList<Integer> filaObservable = FXCollections.observableArrayList();
                    for (int valor : fila) {
                        filaObservable.add(valor);
                    }
                    data.add(filaObservable);
                }

                // Configurar las columnas de la tabla
                tablaFATvisual.getColumns().clear(); // Limpiar las columnas existentes
                int numColumnas = 8;
                for (int i = 0; i < numColumnas; i++) {
                    final int columnIndex = i;
                    TableColumn<ObservableList<Integer>, Object> columna = new TableColumn<>("Columna " + i);
                    columna.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
                    tablaFATvisual.getColumns().add(columna);
                }

                // Asignar los datos a la tabla
                tablaFATvisual.setItems(data);

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