package org.fat.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.fat.TablaDirectorios;

public class ModificarDirectorioController {

    @FXML
    private TextField fieldDirectorio;

    private TablaDirectorios tablaDirectorios;
    private ViewExteriorController viewExteriorController;

    private String nombre;

    public void setDatos(TablaDirectorios tablaDirectorios, ViewExteriorController viewExteriorController, String nombre) {
        this.tablaDirectorios = tablaDirectorios;
        this.viewExteriorController = viewExteriorController;
        this.nombre = nombre;
        fieldDirectorio.setText(nombre);
    }

    @FXML
    protected void BotonModificarDirectorio() {
        String rutaActual = viewExteriorController.getLabelRuta().getText();
        String nomDirectorio = fieldDirectorio.getText();

        if (rutaActual.isEmpty()) {
            viewExteriorController.mostrarAlerta("Atención", "Asegúrate de seleccionar la ruta del directorio");
            return;
        }

        if (nomDirectorio.isEmpty()) {
            viewExteriorController.mostrarAlerta("Atención", "Debes nombrar el directorio");
            return;
        }

        rutaActual += "\\\\";
        tablaDirectorios.modificarSubdirectorios(rutaActual, this.nombre, nomDirectorio);
        viewExteriorController.actualizarTabla(rutaActual);
        viewExteriorController.actualizarTreeView();

        // Cerrar la ventana
        Stage stage = (Stage) fieldDirectorio.getScene().getWindow();
        stage.close();
    }
}
