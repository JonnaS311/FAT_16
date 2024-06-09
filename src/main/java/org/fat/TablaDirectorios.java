package org.fat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Date;

public class TablaDirectorios {
    private static final int LONGITUD_NOMBRE = 8;
    private static final int LONGITUD_EXTENSION = 3;

    private DirectorioFAT root;
    private TablaFAT tablaFAT;

    public TablaDirectorios(TablaFAT tablaFAT) {
        this.root = new DirectorioFAT("root");
        this.tablaFAT = tablaFAT;
    }

    public boolean agregarEntrada(FileFAT file, String ruta) {
        if (file.getName().length() > LONGITUD_NOMBRE || file.getExtension().length() > LONGITUD_EXTENSION) {
            System.out.println("Error: Nombre de archivo o extensión inválidos");
            return false;
        }

        DirectorioFAT directorio = navegarARuta(ruta);
        if (directorio == null) {
            System.out.println("Error: Ruta no encontrada");
            return false;
        }

        for (EntradaDirectorio entrada : directorio.entradas) {
            if (entrada.file.getName().trim().equals(file.getName()) && entrada.file.getExtension().trim().equals(file.getExtension())) {
                System.out.println("Error: El archivo ya existe");
                return false;
            }
        }

        boolean exito = tablaFAT.CreateFile(file.getSize());
        if (!exito) {
            System.out.println("Error: No se pudo crear el archivo en la tabla FAT");
            return false;
        }

        int primerCluster = tablaFAT.getTable_first_cluster().get(tablaFAT.getTable_first_cluster().size() - 1);
        EntradaDirectorio nuevaEntrada = new EntradaDirectorio(file, primerCluster, ruta);

        directorio.agregarEntrada(nuevaEntrada);
        return true;
    }

    public boolean eliminarEntrada(String nombre, String extension, String ruta) {
        DirectorioFAT directorio = navegarARuta(ruta);
        if (directorio == null) {
            System.out.println("Error: Ruta no encontrada");
            return false;
        }

        for (EntradaDirectorio entrada : directorio.entradas) {
            if (entrada.file.getName().trim().equals(nombre) && entrada.file.getExtension().trim().equals(extension)) {
                boolean exito = tablaFAT.DeleteFile(entrada.primerCluster);
                if (!exito) {
                    System.out.println("Error: No se pudo eliminar el archivo de la tabla FAT");
                    return false;
                }

                directorio.entradas.remove(entrada);
                return true;
            }
        }
        System.out.println("Error: Archivo no encontrado");
        return false;
    }

    public EntradaDirectorio obtenerEntrada(String nombre, String extension, String ruta) {
        DirectorioFAT directorio = navegarARuta(ruta);
        if (directorio == null) {
            System.out.println("Error: Ruta no encontrada");
            return null;
        }

        for (EntradaDirectorio entrada : directorio.entradas) {
            if (entrada.file.getName().trim().equals(nombre) && entrada.file.getExtension().trim().equals(extension)) {
                return entrada;
            }
        }
        return null;
    }

    public void listarEntradas(String ruta) {
        DirectorioFAT directorio = navegarARuta(ruta);
        if (directorio == null) {
            System.out.println("Error: Ruta no encontrada");
            return;
        }

        System.out.printf("%-15s %-15s %-10s %-20s %-20s %-20s %-10s%n", 
                "Ruta/Nombre", "Primer Cluster", "Tamaño", "Fecha Creación", "Fecha Modificación", "Último Acceso", "Atributo");
        System.out.println("------------------------------------------------------------------------------------------------------------");

        for (EntradaDirectorio entrada : directorio.entradas) {
            System.out.println(entrada);
        }

        for (DirectorioFAT subdir : directorio.subdirectorios) {
            System.out.println(subdir);
        }
    }

    public boolean crearSubdirectorio(String nombre, String rutaPadre) {
        DirectorioFAT directorioPadre = navegarARuta(rutaPadre);
        if (directorioPadre == null) {
            System.out.println("Error: Ruta del directorio padre no encontrada");
            return false;
        }

        DirectorioFAT nuevoSubdirectorio = new DirectorioFAT(nombre);
        directorioPadre.agregarSubdirectorio(nuevoSubdirectorio);
        return true;
    }

    private DirectorioFAT navegarARuta(String ruta) {
        if (!ruta.startsWith("C:\\")) {
            System.out.println("Error: Ruta no válida. Debe comenzar con C:\\");
            return null;
        }
    
        String[] partesRuta = ruta.substring(3).split("\\\\");
        DirectorioFAT directorioActual = root;
    
        for (String parte : partesRuta) {
            if (parte.isEmpty()) continue;
            directorioActual = directorioActual.buscarSubdirectorio(parte);
            if (directorioActual == null) {
                return null;
            }
        }
    
        return directorioActual;
    }

    public void buscarRuta(String ruta) {
        DirectorioFAT directorio = navegarARuta(ruta);
        if (directorio == null) {
            System.out.println("Error: Ruta no encontrada");
            return;
        }

        listarEntradas(ruta);
    }

    //EJEMPLO DE USO
    public static void main(String[] args) {
        TablaFAT tablaFAT = new TablaFAT();
        TablaDirectorios tablaDirectorios = new TablaDirectorios(tablaFAT);

        // Crear directorios y subdirectorios
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

    }

    public DirectorioFAT getRoot() {
        return  this.root;
    }
}


