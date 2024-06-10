package org.fat.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.fat.TablaDirectorios;
import org.fat.TablaFAT;

import java.io.IOException;
import java.util.ArrayList;

public class TablaFATController {
    @FXML
    private TableView<ObservableList<String>> tablaFATvisual;

    private TablaDirectorios VartablaDirectorios;

    private TablaFAT vartablaFat;

    @FXML
    protected void IrGestor(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Vista_exterior.fxml"));
        Parent root = loader.load();

        ViewExteriorController viewExteriorController = loader.getController();
        viewExteriorController.initialize(vartablaFat, VartablaDirectorios);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

    }

    public void setTablaFATData(TablaFAT tablaFAT, TablaDirectorios tablaDirectorios) {
        vartablaFat = tablaFAT;
        VartablaDirectorios = tablaDirectorios;

        // Crear una lista observable para almacenar los datos de la tabla
        ArrayList<int[]> datosTabla = tablaFAT.getFormattedTable();

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        for (int[] fila : datosTabla) {
            ObservableList<String> filaObservable = FXCollections.observableArrayList();
            for (int valor : fila) {
                String hexa = Integer.toHexString(valor);
                filaObservable.add(hexa);
            }
            data.add(filaObservable);
        }

        // Configurar las columnas de la tabla
        tablaFATvisual.getColumns().clear(); // Limpiar las columnas existentes
        int numColumnas = datosTabla.get(0).length; // Asegurar que no excedamos el número de columnas en los datos de la tabla
        for (int i = 0; i < numColumnas; i++) {
            final int columnIndex = i;
            TableColumn<ObservableList<String>, String> columna = new TableColumn<>("Columna " + (i + 1));
            columna.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get(columnIndex)));
            tablaFATvisual.getColumns().add(columna);
        }

        // Asignar los datos a la tabla
        tablaFATvisual.setItems(data);
    }
}
