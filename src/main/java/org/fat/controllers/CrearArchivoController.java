package org.fat.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.fat.FileFAT;
import org.fat.TablaDirectorios;

import java.util.Date;

public class CrearArchivoController {

    @FXML
    private TextField fieldArchivo;

    @FXML
    private TextField extensionField;

    @FXML
    private TextField atributoField;

    @FXML
    private TextField tamanoField;

    @FXML
    private TextArea areaContenido;

    private TablaDirectorios tablaDirectorios;
    private ViewExteriorController viewExteriorController;

    public void setDatos(TablaDirectorios tablaDirectorios, ViewExteriorController viewExteriorController) {
        this.tablaDirectorios = tablaDirectorios;
        this.viewExteriorController = viewExteriorController;
    }

    @FXML
    protected void BotonCrearArchivo() {
        String rutaActual = viewExteriorController.getLabelRuta().getText();
        String nomArchivo = fieldArchivo.getText();
        String extension = extensionField.getText();
        String atributo = atributoField.getText();
        String tamano = tamanoField.getText();
        String contenido = areaContenido.getText();

        if (rutaActual.isEmpty()) {
            mostrarAlerta("Atención", "Selecciona la ruta del directorio");
            return;
        }

        if (nomArchivo.isEmpty()) {
            mostrarAlerta("Atención", "Debes nombrar el archivo");
            return;
        }

        if (extension.isEmpty()) {
            mostrarAlerta("Atención", "Debes proporcionar una extensión para el archivo");
            return;
        }

        if (tamano.isEmpty()) {
            mostrarAlerta("Atención", "Debes proporcionar el tamaño del archivo");
            return;
        }

        // Convertir el tamaño a entero
        int tamanoInt;
        int atributoInt;
        try {
            tamanoInt = Integer.parseInt(tamano);
            atributoInt = Integer.parseInt(atributo);
        } catch (NumberFormatException e) {
            mostrarAlerta("Atención", "El tamaño y el atributo deben ser numericos");
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
