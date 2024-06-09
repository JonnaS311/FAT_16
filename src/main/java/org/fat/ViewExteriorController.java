package org.fat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Date;

public class ViewExteriorController {
    @FXML
    private TreeView<String> arbol;

    @FXML
    private Label labelRuta;

    @FXML
    private Label labelContenido;

    @FXML
    private TableView<ObservableList<Object>> tableArchivos;

    private TablaDirectorios tablaDirectorios;

    private TablaFAT tt;

    public void initialize(TablaFAT tablaFAT) {

        tt = tablaFAT;
        // Inicializa tu TablaDirectorios
        tablaDirectorios = new TablaDirectorios(tablaFAT);

        tablaDirectorios.crearSubdirectorio("Escritorio", "C:\\");
        tablaDirectorios.crearSubdirectorio("directorio2", "C:\\");
        tablaDirectorios.crearSubdirectorio("REDESII", "C:\\Escritorio");
        tablaDirectorios.crearSubdirectorio("TRABAJOS", "C:\\Escritorio\\REDESII");

        //Crea objetos de tipo FileFAT
        FileFAT archivo1 = new FileFAT("archivo1", "txt", new Date(), 32, 204869, "Contenido del archivo 1");
        FileFAT archivo2 = new FileFAT("archivo2", "pdf", new Date(), 32, 404869, "Contenido del archivo 2");
        FileFAT archivo3 = new FileFAT("archivo3", "doc", new Date(), 32, 404869, "Contenido del archivo 3");

        // Agregar archivos a los directorios
        tablaDirectorios.agregarEntrada(archivo1, "C:\\Escritorio");
        tablaDirectorios.agregarEntrada(archivo2, "C:\\Escritorio\\REDESII");
        tablaDirectorios.agregarEntrada(archivo3, "C:\\Escritorio\\REDESII");
        tablaDirectorios.agregarEntrada(archivo3, "C:\\Escritorio\\REDESII\\TRABAJOS");

        // Listar los archivos dentro de los directorios
        tablaDirectorios.listarEntradas("C:\\Escritorio\\REDESII");

        // Crear el árbol y establecerlo en el TreeView
        TreeItem<String> treeRoot = buildTree(tablaDirectorios);
        arbol.setRoot(treeRoot);


        arbol.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String selectedDirectoryName = obtenerRutaCompleta(newValue);
                labelRuta.setText(selectedDirectoryName);
                actualizarTabla(selectedDirectoryName);
            }
        });

        tableArchivos.setRowFactory(tv -> {
            TableRow<ObservableList<Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    ObservableList<Object> rowData = row.getItem();
                    if (rowData.get(8) == "Archivo"){
                        labelContenido.setText(rowData.get(9).toString());
                        TableView.TableViewSelectionModel<ObservableList<Object>> selectionModel = tableArchivos.getSelectionModel();
                        ObservableList<Object> selectedItem = selectionModel.getSelectedItem();

                        if (selectedItem != null) {
                            String nombreArchivo = selectedItem.get(0).toString();
                            int puntoIndex = nombreArchivo.indexOf('.');
                            if (puntoIndex != -1) {
                                nombreArchivo = nombreArchivo.substring(0, puntoIndex);
                            }
                            String extensionArchivo = selectedItem.get(1).toString();
                            String rutaActual = labelRuta.getText() + "\\\\";

                            // Abrir la entrada
                            tablaDirectorios.abrir(nombreArchivo, extensionArchivo, rutaActual);
                            actualizarTabla(labelRuta.getText());
                        }
                    } else if (rowData.get(8) == "Directorio"){
                        String rutamedia = labelRuta.getText() + "\\\\" + rowData.get(0);
                        actualizarTabla(rutamedia);
                        labelRuta.setText(rutamedia);
                    }
                }
            });
            return row;
        });

        tableArchivos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.get(8) != "Archivo" && oldValue != null ) {
                labelContenido.setText("Clickea dos veces en la tabla para abrir el archivo, clickea una vez para seleccionar y puedes eliminar o modificar el archivo");
            }
        });

        configurarTableView();
    }

    public String obtenerRutaCompleta(TreeItem<String> selectedItem) {
        StringBuilder ruta = new StringBuilder();
        TreeItem<String> currentItem = selectedItem;

        // Recorre los nodos padres concatenando los nombres de los directorios
        while (currentItem != null && currentItem.getParent() != null) {
            if (ruta.length() > 0) {
                ruta.insert(0, "\\\\"); // Utiliza una sola barra invertida
            }
            ruta.insert(0, currentItem.getValue());
            currentItem = currentItem.getParent();
        }

        return "C:\\\\" + ruta.toString(); // Agrega el prefijo de la unidad de disco
    }

    void actualizarTabla(String ruta) {
        Object[][] datos = tablaDirectorios.listarEntradasComoArray(ruta);
        ObservableList<ObservableList<Object>> data = FXCollections.observableArrayList();

        for (Object[] fila : datos) {
            ObservableList<Object> filaObservable = FXCollections.observableArrayList(fila);
            data.add(filaObservable);
        }
        tableArchivos.setItems(data);
    }

    private void configurarTableView() {
        String[] columnas = {"Nombre", "Extensión", "Primer Cluster", "Tamaño", "Fecha Creación", "Fecha Modificación", "Último Acceso", "Atributo", "Tipo", "Contenido"};
        for (int i = 0; i < columnas.length; i++) {
            TableColumn<ObservableList<Object>, Object> columna = new TableColumn<>(columnas[i]);
            final int colIndex = i;
            columna.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().get(colIndex)));
            tableArchivos.getColumns().add(columna);
        }
        tableArchivos.getColumns().get(9).setPrefWidth(0);
        tableArchivos.getColumns().get(9).setMaxWidth(0);
        tableArchivos.getColumns().get(9).setMinWidth(0);
        tableArchivos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Esto asegura que las columnas se ajusten al ancho de la tabla
        tableArchivos.autosize();
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

    void actualizarTreeView() {
        // Crear el árbol y establecerlo en el TreeView
        TreeItem<String> treeRoot = buildTree(tablaDirectorios);
        arbol.setRoot(treeRoot);
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

    @FXML
    protected void CrearArchivo(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Crear_Archivo.fxml"));
        Parent root = loader.load();


        CrearArchivoController crearArchivoController = loader.getController();
        crearArchivoController.setDatos(tablaDirectorios, this);

        // Mostrar la nueva escena
        Stage newStage = new Stage();
        Scene newScene = new Scene(root);
        newStage.setTitle("Simulador FAT16 - Crear Archivo");
        newStage.setScene(newScene);
        newStage.centerOnScreen();
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(((Node) event.getSource()).getScene().getWindow());
        newStage.show();
    }

    public Label getLabelRuta() {
        return labelRuta;
    }

    public void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    protected void CrearDirectorio(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Crear_Directorio.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la nueva escena y pasarle los datos
        CrearDirectorioController crearDirectorioController = loader.getController();
        crearDirectorioController.setDatos(tablaDirectorios, this);

        // Mostrar la nueva escena
        Stage newStage = new Stage();
        Scene newScene = new Scene(root);
        newStage.setTitle("Simulador FAT16 - Crear Directorio");
        newStage.setScene(newScene);
        newStage.centerOnScreen();
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(((Node) event.getSource()).getScene().getWindow());
        newStage.show();
    }


    @FXML
    protected void Modificar(ActionEvent event) throws IOException {
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

    @FXML
    protected void Eliminar(ActionEvent event) throws IOException {
        // Obtén la fila seleccionada
        TableView.TableViewSelectionModel<ObservableList<Object>> selectionModel = tableArchivos.getSelectionModel();
        ObservableList<Object> selectedItem = selectionModel.getSelectedItem();

        if (selectedItem != null) {
            String nombreArchivo = selectedItem.get(0).toString();
            int puntoIndex = nombreArchivo.indexOf('.');
            if (puntoIndex != -1) {
                nombreArchivo = nombreArchivo.substring(0, puntoIndex);
            }
            String extensionArchivo = selectedItem.get(1).toString();
            String rutaActual = labelRuta.getText() + "\\\\";

            // Intenta eliminar la entrada
            if (tablaDirectorios.eliminarEntrada(nombreArchivo, extensionArchivo, rutaActual)) {
                // Muestra la alerta de éxito
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Borrado de archivo");
                alert.setHeaderText("Atención");
                alert.setContentText("El archivo " + nombreArchivo + " se ha borrado con éxito.");
                alert.showAndWait();

                // Actualiza la tabla después de eliminar el archivo
                actualizarTabla(rutaActual);
                actualizarTreeView();
            } else {
                // Muestra una alerta si la eliminación falla
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de borrado");
                alert.setHeaderText("Error");
                alert.setContentText("No se pudo borrar el archivo " + nombreArchivo + ".");
                alert.showAndWait();
            }
        } else {
            // Muestra una alerta si no hay ninguna fila seleccionada
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("Atención");
            alert.setContentText("Por favor, selecciona un archivo para eliminar.");
            alert.showAndWait();
        }
    }

}
