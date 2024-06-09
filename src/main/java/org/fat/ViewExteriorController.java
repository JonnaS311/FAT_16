package org.fat;

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
import java.util.Date;

public class ViewExteriorController {
    @FXML
    private TreeView<String> arbol;

    @FXML
    private Label labelRuta;

    @FXML
    private TableView<String> tableArchivos;

    private TablaDirectorios tablaDirectorios;

    private TablaFAT tt;

    public void initialize(TablaFAT tablaFAT) {

        tt = tablaFAT;
        // Inicializa tu TablaDirectorios
        tablaDirectorios = new TablaDirectorios(tablaFAT);

        // Crear algunos directorios y archivos de ejemplo
        tablaDirectorios.crearSubdirectorio("Escritorio", "C:\\");
        tablaDirectorios.crearSubdirectorio("directorio2", "C:\\");
        tablaDirectorios.crearSubdirectorio("REDESII", "C:\\Escritorio");
        tablaDirectorios.crearSubdirectorio("TRABAJOS", "C:\\Escritorio\\REDESII");

        FileFAT archivo1 = new FileFAT("archivo1", "txt", new Date(), 32, 204869, "Contenido del archivo 1");
        FileFAT archivo2 = new FileFAT("archivo2", "pdf", new Date(), 32, 404869, "Contenido del archivo 2");
        FileFAT archivo3 = new FileFAT("archivo3", "doc", new Date(), 32, 404869, "Contenido del archivo 3");

        tablaDirectorios.agregarEntrada(archivo1, "C:\\Escritorio");
        tablaDirectorios.agregarEntrada(archivo2, "C:\\Escritorio\\REDESII");
        tablaDirectorios.agregarEntrada(archivo3, "C:\\Escritorio\\REDESII");
        tablaDirectorios.agregarEntrada(archivo3, "C:\\Escritorio\\REDESII\\TRABAJOS");


        // Crear el árbol y establecerlo en el TreeView
        TreeItem<String> treeRoot = buildTree(tablaDirectorios);
        arbol.setRoot(treeRoot);

        arbol.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String selectedDirectoryName = obtenerRutaCompleta(newValue);
                labelRuta.setText(selectedDirectoryName);
                updateTableView(selectedDirectoryName);
            }
        });
    }

    public String obtenerRutaCompleta(TreeItem<String> selectedItem) {
        StringBuilder ruta = new StringBuilder();
        TreeItem<String> currentItem = selectedItem;

        // Recorre los nodos padres concatenando los nombres de los directorios
        while (currentItem != null) {
            if (ruta.length() > 0) {
                ruta.insert(0, "\\");
            }
            ruta.insert(0, currentItem.getValue());
            currentItem = currentItem.getParent();
        }

        return "C:\\" + ruta.toString(); // Agrega el prefijo de la unidad de disco
    }

    private void updateTableView(String directoryName) {
        //ObservableList<EntradaDirectorioModelo> entradas = tablaDirectorios.listarEntradasYSubdirectorios(ruta);
        //tableArchivos.setItems(entradas);
    }

    private TreeItem<String> buildTree(TablaDirectorios tablaDirectorios) {
        DirectorioFAT rootDir = tablaDirectorios.getRoot();
        TreeItem<String> rootNode = new TreeItem<>(rootDir.getNombre());
        buildTreeRecursively(rootDir, rootNode);
        return rootNode;
    }

    private void buildTreeRecursively(DirectorioFAT directory, TreeItem<String> parentItem) {
        // Agregar subdirectorios como nodos hijos
        for (DirectorioFAT subdir : directory.getSubdirectorios()) {
            TreeItem<String> subDirNode = new TreeItem<>(subdir.getNombre());
            parentItem.getChildren().add(subDirNode);
            buildTreeRecursively(subdir, subDirNode); // Recursión para agregar subdirectorios
        }

        // Agregar entradas de archivo como nodos hijos
        for (EntradaDirectorio entry : directory.getEntradas()) {
            TreeItem<String> fileNode = new TreeItem<>(entry.getNombre() + "." + entry.getExtension());
            parentItem.getChildren().add(fileNode);
        }
    }

    @FXML
    protected void Regresar(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la nueva escena y pasarle los datos
        TablaFATController tablaFATController = loader.getController();
        tablaFATController.setTablaFATData(tt);

        // Mostrar la nueva escena
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Simulador FAT16 - Tabla de Asignación de Archivos");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}

