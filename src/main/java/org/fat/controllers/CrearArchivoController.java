package org.fat.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.fat.FileFAT;
import org.fat.TablaDirectorios;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrearArchivoController {

    @FXML
    private TextField fieldArchivo;

    @FXML
    private TextField extensionField;

    @FXML
    private ComboBox cmbAtributo;

    @FXML
    private TextField tamanoField;

    @FXML
    private TextArea areaContenido;

    private TablaDirectorios tablaDirectorios;
    private ViewExteriorController viewExteriorController;

    private final Map<String, String> atributoMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Llenar el ComboBox con opciones
        cmbAtributo.setItems(FXCollections.observableArrayList("Solo lectura", "Oculto", "Etiqueta volumen", "Archivado", "Reservado"));

        // Mapear opciones a sus representaciones
        atributoMap.put("Solo lectura", "0x01");
        atributoMap.put("Oculto", "0x04");
        atributoMap.put("Etiqueta volumen", "0x08");
        atributoMap.put("Archivado", "0x20");
        atributoMap.put("Reservado", "0x40");
    }

    public void setDatos(TablaDirectorios tablaDirectorios, ViewExteriorController viewExteriorController) {
        this.tablaDirectorios = tablaDirectorios;
        this.viewExteriorController = viewExteriorController;
    }

    @FXML
    protected void BotonCrearArchivo() {
        String rutaActual = viewExteriorController.getLabelRuta().getText();
        String nomArchivo = fieldArchivo.getText();
        String extension = extensionField.getText();
        String atributo = (String) cmbAtributo.getValue();
        String tamano = tamanoField.getText();
        String contenido = areaContenido.getText();

        if (rutaActual.isEmpty()) {
            mostrarAlerta("Atención", "Selecciona la ruta del directorio");
            return;
        }

        if (nomArchivo.isEmpty() || nomArchivo.length() > 8) {
            mostrarAlerta("Atención", "Debes nombrar el archivo (menos de 8 caracteres)");
            return;
        }

        if (extension.isEmpty() || extension.length() > 3) {
            mostrarAlerta("Atención", "Debes proporcionar una extensión para el archivo (menos de 3 caracteres)");
            return;
        }

        if (tamano.isEmpty()) {
            mostrarAlerta("Atención", "Debes proporcionar el tamaño del archivo");
            return;
        }

        // Convertir el tamaño a entero
        int tamanoInt;
        String atributoInt;
        try {
            tamanoInt = Integer.parseInt(tamano);
            atributoInt = atributoMap.get(atributo);
        } catch (NumberFormatException e) {
            mostrarAlerta("Atención", "El tamaño deben ser numericos");
            return;
        }

        // Crear el archivo y agregarlo al directorio
        FileFAT nuevoArchivo = new FileFAT(nomArchivo, extension, new Date(), atributoInt, tamanoInt,contenido);
        if (tablaDirectorios.agregarEntrada(nuevoArchivo, rutaActual) == "true"){
            viewExteriorController.actualizarTabla(rutaActual);
            viewExteriorController.actualizarTreeView();
        } else {
            mostrarAlerta("Atención", tablaDirectorios.agregarEntrada(nuevoArchivo, rutaActual));
        }

        // Cerrar la ventana
        Stage stage = (Stage) fieldArchivo.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
