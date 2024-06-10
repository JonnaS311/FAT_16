package org.fat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TablaDirectorios {
    private static final int LONGITUD_NOMBRE = 8;
    private static final int LONGITUD_EXTENSION = 3;

    private DirectorioFAT root;
    private TablaFAT tablaFAT;

    public TablaDirectorios(TablaFAT tablaFAT) {
        this.root = new DirectorioFAT("Disco local (C:)");
        this.tablaFAT = tablaFAT;
        this.tablaFAT.CreateFile(16384); // Añadir el directorio ROOT a la tabla fat
    }

    public String agregarEntrada(FileFAT file, String ruta) {
        if (file.getName().length() > LONGITUD_NOMBRE || file.getExtension().length() > LONGITUD_EXTENSION) {
            return "Error: Nombre de archivo o extensión inválidos";
        }

        DirectorioFAT directorio = navegarARuta(ruta);
        if (directorio == null) {
            return "Error: Ruta no encontrada";
        }

        for (EntradaDirectorio entrada : directorio.entradas) {
            if (entrada.file.getName().trim().equals(file.getName()) && entrada.file.getExtension().trim().equals(file.getExtension())) {
                return "Error: El archivo ya existe";
            }
        }

        boolean exito = tablaFAT.CreateFile(file.getSize());
        if (!exito) {
            return "Error: No se pudo crear el archivo en la tabla FAT";
        }

        int primerCluster = tablaFAT.getTable_first_cluster().get(tablaFAT.getTable_first_cluster().size() - 1);
        EntradaDirectorio nuevaEntrada = new EntradaDirectorio(file, primerCluster, ruta);

        directorio.agregarEntrada(nuevaEntrada);
        return "true";
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

    public boolean modificarEntradas(String nombre, String extension, String ruta, String nombreAct, String contenido) {
        DirectorioFAT directorio = navegarARuta(ruta);
        if (directorio == null) {
            System.out.println("Error: Ruta no encontrada");
            return false;
        }

        for (EntradaDirectorio entrada : directorio.entradas) {
            if (entrada.file.getName().trim().equals(nombre) && entrada.file.getExtension().trim().equals(extension)) {
                entrada.file.modifiedFile(contenido);
                entrada.file.setName(nombreAct);
                return true;
            }
        }
        System.out.println("Error: Archivo no encontrado");
        return false;
    }

    public boolean modificarSubdirectorios(String ruta, String nombreAnt, String nombreAct) {
        DirectorioFAT directorio = navegarARuta(ruta);
        if (directorio == null) {
            System.out.println("Error: Ruta no encontrada");
            return false;
        }
        for (DirectorioFAT subdir : directorio.subdirectorios) {
            if (subdir.getNombre().trim().equals(nombreAnt)) {
                subdir.setNombre(nombreAct);
                subdir.setDirModifiedDate(new Date()); // Actualizamos la fecha de modificación
                return true;
            }
        }
        System.out.println("Error: Directorio no encontrado");
        return false;
    }

    public boolean abrir(String nombre, String extension, String ruta) {
        DirectorioFAT directorio = navegarARuta(ruta);
        if (directorio == null) {
            System.out.println("Error: Ruta no encontrada");
            return false;
        }

        for (EntradaDirectorio entrada : directorio.entradas) {
            if (entrada.file.getName().trim().equals(nombre) && entrada.file.getExtension().trim().equals(extension)) {
                entrada.file.abrirFile();
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

    public Object[][] listarEntradasComoArray(String ruta) {
        DirectorioFAT directorio = navegarARuta(ruta);
        if (directorio == null) {
            System.out.println("Error: Ruta no encontrada");
            return new Object[0][];
        }

        List<Object[]> listaEntradas = new ArrayList<>();

        for (EntradaDirectorio entrada : directorio.getEntradas()) {
            Object[] datosEntrada = {
                    entrada.getNombre() + "." + entrada.getExtension(),
                    entrada.getExtension(),
                    entrada.getPrimerCluster(),
                    entrada.getTamaño(),
                    entrada.getFechaCreacion(),
                    entrada.getFechaModificacion(),
                    entrada.getUltimoAcceso(),
                    entrada.getAtributo(),
                    "Archivo",
                    entrada.getContent(),
                    entrada
            };
            listaEntradas.add(datosEntrada);
        }

        for (DirectorioFAT subdir : directorio.getSubdirectorios()) {
            Object[] datosSubdir = {
                    subdir.getNombre(),
                    "",
                    "",
                    "",
                    subdir.getDirCreatedDate(),
                    subdir.getDirModifiedDate(),
                    "",
                    subdir.getATTRIBUTE(),
                    "Directorio",
                    ""
            };
            listaEntradas.add(datosSubdir);
        }

        return listaEntradas.toArray(new Object[0][]);
    }

    public String crearSubdirectorio(String nombre, String rutaPadre) {
        DirectorioFAT directorioPadre = navegarARuta(rutaPadre);
        if (directorioPadre == null) {
            return "Error: Ruta del directorio padre no encontrada";
        }
        this.tablaFAT.CreateFile(16384); // Añadimos el subdirectorio a la tabla fat.
        DirectorioFAT nuevoSubdirectorio = new DirectorioFAT(nombre);
        directorioPadre.agregarSubdirectorio(nuevoSubdirectorio);
        return "true";
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
        FileFAT archivo1 = new FileFAT("archivo1", "txtddd", new Date(), 32, 204869, "Contenido del archivo 1");
        FileFAT archivo2 = new FileFAT("archivo2", "pdfhhhhh", new Date(), 32, 404869, "Contenido del archivo 2");
        FileFAT archivo3 = new FileFAT("archivo3", "docddd", new Date(), 32, 404869, "Contenido del archivo 3");

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


