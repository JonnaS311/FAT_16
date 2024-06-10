package org.fat.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fat.DirectorioFAT;
import org.fat.EntradaDirectorio;
import org.fat.TablaDirectorios;
import org.fat.TablaFAT;

import java.io.IOException;

public class ViewExteriorController {
    @FXML
    private TreeView<String> arbol;

    @FXML
    private Label labelRuta;

    @FXML
    private Label labelContenido;

    @FXML
    private TableView<ObservableList<Object>> tableArchivos;
    @FXML
    private Label labelEstadoMemoria;

    @FXML
    private ProgressBar pBarEstadoMemoria;

    private TablaDirectorios VartablaDirectorios;

    private TablaFAT vartablaFat;

    public void initialize(TablaFAT tablaFAT, TablaDirectorios tablaDirectorios) {

        vartablaFat = tablaFAT;
        VartablaDirectorios = tablaDirectorios;

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
                /*if (!row.isEmpty() && event.isSecondaryButtonDown()) {
                    ObservableList<Object> rowData = row.getItem();
                    System.out.println("Clic derecho del ratón en la fila: " + rowData.toString());
                }*/
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    ObservableList<Object> rowData = row.getItem();
                    if (rowData.get(8) == "Archivo"){
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
                        labelContenido.setText(rowData.get(9).toString());
                    } else if (rowData.get(8) == "Directorio"){
                        String currentPath = labelRuta.getText();
                        String newPathComponent = rowData.get(0).toString();

                        // Verificar si la ruta actual ya termina con "\\"
                        if (!currentPath.endsWith("\\\\")) {
                            currentPath += "\\\\";
                        }

                        String rutamedia = currentPath + newPathComponent;
                        actualizarTabla(rutamedia);
                        labelRuta.setText(rutamedia);
                    }
                }
            });
            return row;
        });

        tableArchivos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ((newValue == null || newValue.get(8) != "Archivo") && oldValue != null ) {
                labelContenido.setText("Clickea dos veces en la tabla para abrir el archivo. Un solo clic para seleccionarlo y así puedes eliminar o modificar el archivo seleccionado.");
            }
        });

        labelEstadoMemoria.setText(vartablaFat.getMemoriaRestante() + " B disponibles de " + vartablaFat.getMemoriaTotal() + " B");
        // Calcular el progreso como un porcentaje
        float progreso = 1 - (((vartablaFat.getMemoriaRestante() / (float) vartablaFat.getMemoriaTotal()) * 100) / 100);

        // Actualizar el progreso del ProgressBar
        pBarEstadoMemoria.setProgress(progreso);
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

    public void actualizarTabla(String ruta) {
        labelEstadoMemoria.setText(vartablaFat.getMemoriaRestante() + " B disponibles de " + vartablaFat.getMemoriaTotal() + " B");
        float progreso = 1 - (((vartablaFat.getMemoriaRestante() / (float) vartablaFat.getMemoriaTotal()) * 100) / 100);
        pBarEstadoMemoria.setProgress(progreso);
        Object[][] datos = VartablaDirectorios.listarEntradasComoArray(ruta);
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

        // Ocultar columnas
        tableArchivos.getColumns().get(9).setPrefWidth(0);
        tableArchivos.getColumns().get(9).setMaxWidth(0);
        tableArchivos.getColumns().get(9).setMinWidth(0);
        tableArchivos.getColumns().get(8).setPrefWidth(0);
        tableArchivos.getColumns().get(8).setMaxWidth(0);
        tableArchivos.getColumns().get(8).setMinWidth(0);

        tableArchivos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Esto asegura que las columnas se ajusten al ancho de la tabla
        tableArchivos.autosize();

        // Configurar la columna de imagen para mostrar diferentes imágenes según el tipo de entrada
        TableColumn<ObservableList<Object>, ImageView> imageColumn = new TableColumn<>("");
        imageColumn.setCellValueFactory(cellData -> {
            ImageView imageView = new ImageView();
            String type = cellData.getValue().get(8).toString();
            if ("Archivo".equals(type)) {
                imageView.setImage(new Image("/org/fat/Imagenes/expediente2.png"));
            } else if ("Directorio".equals(type)) {
                imageView.setImage(new Image("/org/fat/Imagenes/carpeta2.png"));
            }
            imageView.setFitWidth(20);
            imageView.setFitHeight(20);
            return new SimpleObjectProperty<>(imageView);
        });

        // Agregar las columnas a la tabla
        tableArchivos.getColumns().addAll(imageColumn);
        tableArchivos.getColumns().remove(imageColumn);
        tableArchivos.getColumns().add(0, imageColumn);
        tableArchivos.getColumns().get(0).setPrefWidth(25);
        tableArchivos.getColumns().get(0).setMaxWidth(25);
        tableArchivos.getColumns().get(0).setMinWidth(25);
    }

    private ObservableList<Object> obtenerElementoTableView(){
        // Obtén la fila seleccionada
        TableView.TableViewSelectionModel<ObservableList<Object>> selectionModel = tableArchivos.getSelectionModel();
        ObservableList<Object> selectedItem = selectionModel.getSelectedItem();
        return selectedItem;
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

    public void actualizarTreeView() {
        // Crear el árbol y establecerlo en el TreeView
        TreeItem<String> treeRoot = buildTree(VartablaDirectorios);
        arbol.setRoot(treeRoot);
    }

    public void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public Label getLabelRuta() {
        return labelRuta;
    }


    @FXML
    protected void Regresar(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la nueva escena y pasarle los datos
        TablaFATController tablaFATController = loader.getController();
        tablaFATController.setTablaFATData(vartablaFat, VartablaDirectorios);

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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Crear_Archivo.fxml"));
        Parent root = loader.load();


        CrearArchivoController crearArchivoController = loader.getController();
        crearArchivoController.setDatos(VartablaDirectorios, this);

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

    @FXML
    protected void CrearDirectorio(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Crear_Directorio.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la nueva escena y pasarle los datos
        CrearDirectorioController crearDirectorioController = loader.getController();
        crearDirectorioController.setDatos(VartablaDirectorios, this);

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
        ObservableList<Object> selectedItem = obtenerElementoTableView();
        if (selectedItem != null){
            if (selectedItem.get(8) == "Archivo") {
                String nombreArchivo = selectedItem.get(0).toString();
                int puntoIndex = nombreArchivo.indexOf('.');
                if (puntoIndex != -1) {
                    nombreArchivo = nombreArchivo.substring(0, puntoIndex);
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("../Modificar_Archivo.fxml"));
                Parent root = loader.load();

                // Obtener el controlador de la nueva escena y pasarle los datos
                ModificarArchivoCotroller modificarArchivoCotroller = loader.getController();
                modificarArchivoCotroller.setDatos(VartablaDirectorios, this, nombreArchivo, selectedItem.get(1).toString(), selectedItem.get(9).toString());

                // Mostrar la nueva escena
                Stage newStage = new Stage();
                Scene newScene = new Scene(root);
                newStage.setTitle("Simulador FAT16 - Modificar Archivo");
                newStage.setScene(newScene);
                newStage.centerOnScreen();
                newStage.initModality(Modality.WINDOW_MODAL);
                newStage.initOwner(((Node) event.getSource()).getScene().getWindow());
                newStage.show();
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(  "../Modificar_Directorio.fxml"));
                Parent root = loader.load();

                // Obtener el controlador de la nueva escena y pasarle los datos
                ModificarDirectorioController modificarDirectorioController = loader.getController();
                modificarDirectorioController.setDatos(VartablaDirectorios, this, selectedItem.get(0).toString());

                // Mostrar la nueva escena
                Stage newStage = new Stage();
                Scene newScene = new Scene(root);
                newStage.setTitle("Simulador FAT16 - Modificar Directorio");
                newStage.setScene(newScene);
                newStage.centerOnScreen();
                newStage.initModality(Modality.WINDOW_MODAL);
                newStage.initOwner(((Node) event.getSource()).getScene().getWindow());
                newStage.show();
            }

        } else {
            // Muestra una alerta si no hay ninguna fila seleccionada
            mostrarAlerta("Advertencia", "Por favor, selecciona un archivo para modificar.");
        }
    }

    @FXML
    protected void Eliminar(ActionEvent event) throws IOException {
        ObservableList<Object> selectedItem = obtenerElementoTableView();
        if (selectedItem != null){
            if (selectedItem.get(8) == "Archivo") {
                String nombreArchivo = selectedItem.get(0).toString();
                int puntoIndex = nombreArchivo.indexOf('.');
                if (puntoIndex != -1) {
                    nombreArchivo = nombreArchivo.substring(0, puntoIndex);
                }
                String extensionArchivo = selectedItem.get(1).toString();
                String rutaActual = labelRuta.getText() + "\\\\";

                // Intenta eliminar la entrada
                if (VartablaDirectorios.eliminarEntrada(nombreArchivo, extensionArchivo, rutaActual)) {
                    // Muestra la alerta de éxito
                    mostrarAlerta("Archivo borrado", "El archivo " + nombreArchivo + " se ha borrado con éxito.");

                    // Actualiza la tabla después de eliminar el archivo
                    actualizarTabla(rutaActual);
                    actualizarTreeView();
                } else {
                    // Muestra una alerta si la eliminación falla
                    mostrarAlerta("Error", "No se pudo borrar el archivo " + nombreArchivo + ".");
                }

            } else {
                // Espacio para eliminar directorios
            }

        } else {
            // Muestra una alerta si no hay ninguna fila seleccionada
            mostrarAlerta("Advertencia", "Por favor, selecciona un archivo para eliminar.");
        }
    }

    public void Reiniciar(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Configuracion.fxml"));
        Parent root = loader.load();

        // Mostrar la nueva escena
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Simulador FAT16 - Configuración inicial");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void DevolverRuta(MouseEvent mouseEvent) {
        String rutaActual = labelRuta.getText();
        int ultimaEntrada = rutaActual.lastIndexOf("\\\\");
        if (ultimaEntrada != -1) {
            rutaActual = rutaActual.substring(0, ultimaEntrada);
        }
        actualizarTabla(rutaActual);
        labelRuta.setText(rutaActual);
    }

    public void VisualizarBoot(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Vista_Boot_Sector.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la nueva escena y pasarle los datos
        BootSectorController bootSectorController = loader.getController();
        bootSectorController.setDatos(vartablaFat, this, VartablaDirectorios);

        // Mostrar la nueva escena
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Simulador FAT16 - Tabla de Boot Sector");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}
