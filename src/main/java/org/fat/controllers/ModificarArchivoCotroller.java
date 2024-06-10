package org.fat.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.fat.TablaDirectorios;

public class ModificarArchivoCotroller {
    @FXML
    private TextField fieldArchivo;
    @FXML
    private TextArea areaContenido;

    private TablaDirectorios tablaDirectorios;
    private ViewExteriorController viewExteriorController;

    private String nombre;

    private String extension;
    private String contenido;

    public void setDatos(TablaDirectorios tablaDirectorios, ViewExteriorController viewExteriorController, String nombre, String extension, String contenido) {
        this.tablaDirectorios = tablaDirectorios;
        this.viewExteriorController = viewExteriorController;
        this.nombre = nombre;
        this.extension = extension;
        this.contenido = contenido;
        fieldArchivo.setText(nombre);
        areaContenido.setText(contenido);
    }

    @FXML
    protected void BotonModificarArchivo() {
        String rutaActual = viewExteriorController.getLabelRuta().getText();
        String nomArchivo = fieldArchivo.getText();
        String contArchivo = areaContenido.getText();

        if (rutaActual.isEmpty()) {
            viewExteriorController.mostrarAlerta("Atención", "Asegúrate de seleccionar la ruta del directorio");
            return;
        }

        if (nomArchivo.isEmpty()) {
            viewExteriorController.mostrarAlerta("Atención", "Debes nombrar el archivo");
            return;
        }

        rutaActual += "\\\\";
        tablaDirectorios.modificarEntradas(this.nombre, this.extension, rutaActual, nomArchivo, contArchivo);
        viewExteriorController.actualizarTabla(rutaActual);
        viewExteriorController.actualizarTreeView();

        // Cerrar la ventana
        Stage stage = (Stage) fieldArchivo.getScene().getWindow();
        stage.close();
    }
}

