package org.fat.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.HashMap;
import java.util.Map;

public class BootSectorController {

    @FXML
    private TableView<Map<String, Object>> tablaBootSector;
    @FXML
    private TableColumn<Map<String, Object>, String> fieldColumn;
    @FXML
    private TableColumn<Map<String, Object>, Integer> offsetColumn;
    @FXML
    private TableColumn<Map<String, Object>, Integer> sizeColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> defaultColumn;
    private TablaFAT tablaFAT;
    private Integer sector;
    private Integer cluster;
    private ViewExteriorController viewExteriorController;
    private TablaDirectorios VartablaDirectorios;

    public void setDatos(TablaFAT tablaFAT, ViewExteriorController viewExteriorController, TablaDirectorios VartablaDirectorios) {
        this.tablaFAT = tablaFAT;
        this.sector = tablaFAT.getSector();
        this.cluster = tablaFAT.getClusterNumber();
        this.viewExteriorController = viewExteriorController;
        this.VartablaDirectorios = VartablaDirectorios;
        initializeTable();
    }

    @FXML
    public void initialize() {
        fieldColumn.setCellValueFactory(cellData -> new SimpleStringProperty((String) cellData.getValue().get("field")));
        offsetColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty((Integer) cellData.getValue().get("offset")).asObject());
        sizeColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty((Integer) cellData.getValue().get("size (bytes)")).asObject());
        defaultColumn.setCellValueFactory(cellData -> new SimpleStringProperty((String) cellData.getValue().get("defaultValue")));
    }

    private void initializeTable() {
        if (this.tablaFAT == null) {
            System.out.println("tablaFAT is null");
            return;
        }

        System.out.println("Sector: " + this.sector + ", Cluster: " + this.cluster);
        ObservableList<Map<String, Object>> data = FXCollections.observableArrayList(
                createEntry("Jump", 0, 3, "0EB 03C 090"),
                createEntry("OEM ID", 3, 8, "MSWIN4.0"),
                createEntry("Bytes Per Sector", 11, 2, "512"),
                createEntry("Sectors Per Cluster", 13, 1, Integer.toHexString(this.sector)),
                createEntry("Reserved Sectors", 14, 2, "1"),
                createEntry("FAT's", 16, 1, "1"),
                createEntry("Root Entries", 17, 2, "512"),
                createEntry("Sectors", 19, 2, Integer.toHexString( (this.cluster * this.sector) * 512)),
                createEntry("Media Descriptor", 21, 1, "240"),
                createEntry("Sectors Per FAT", 22, 2, Integer.toHexString( (this.cluster * 1) / 512)),
                createEntry("Sectors Per Track", 24, 2, "12"),
                createEntry("Heads", 26, 2, "2"),
                createEntry("Hidden Sectors", 28, 4, "0"),
                createEntry("Sectors (large, for HDD)", 32, 4, "0"),
                createEntry("Physical Drive No.", 36, 1, "80"),
                createEntry("Current Head", 37, 1, "0"),
                createEntry("Signature", 38, 1, "41"),
                createEntry("Serial number (ID)", 39, 4, "4294"),
                createEntry("Volume Label", 43, 11, this.VartablaDirectorios.getRoot().getNombre()),
                createEntry("System ID (filesystem)", 54, 8, "FAT16"),
                createEntry("Total", 62, 0, "")
        );

        tablaBootSector.setItems(data);
    }

    private Map<String, Object> createEntry(String field, int offset, int size, String defaultValue) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("field", field);
        entry.put("offset", offset);
        entry.put("size (bytes)", size);
        entry.put("defaultValue", defaultValue);
        return entry;
    }

    public void IrGestor(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Vista_exterior.fxml"));
        Parent root = loader.load();

        ViewExteriorController viewExteriorController = loader.getController();
        viewExteriorController.initialize(this.tablaFAT, this.VartablaDirectorios);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}
