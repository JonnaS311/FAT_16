package org.fat;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CrearDirectorioController {

    @FXML
    private TextField fieldDirectorio;

    private TablaDirectorios tablaDirectorios;
    private ViewExteriorController viewExteriorController;

    public void setDatos(TablaDirectorios tablaDirectorios, ViewExteriorController viewExteriorController) {
        this.tablaDirectorios = tablaDirectorios;
        this.viewExteriorController = viewExteriorController;
    }

    @FXML
    protected void BotonCrearDirectorio() {
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
        tablaDirectorios.crearSubdirectorio(nomDirectorio, rutaActual);
        viewExteriorController.actualizarTabla(rutaActual);
        viewExteriorController.actualizarTreeView();

        // Cerrar la ventana
        Stage stage = (Stage) fieldDirectorio.getScene().getWindow();
        stage.close();
    }
}