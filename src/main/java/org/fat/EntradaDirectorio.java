package org.fat;


public class EntradaDirectorio {
    public FileFAT file;
    public int primerCluster;
    public String ruta;

    public EntradaDirectorio(FileFAT file, int primerCluster, String ruta) {
        this.file = file;
        this.primerCluster = primerCluster;
        this.ruta = ruta;
    }

    @Override
    public String toString() {
        return String.format("%-15s %-15s %-10s %-20s %-20s %-20s %-10s",
                ruta + "\\" + file.getName() + "." + file.getExtension(),
                "Primer Cluster: " + primerCluster,
                "Tamaño: " + file.getSize(),
                "Fecha Creación: " + file.getCreationDate(),
                "Fecha Modificación: " + file.getModificationDate(),
                "Último Acceso: " + file.getLastAccessDate(),
                "Atributo: " + file.getAttribute());
    }
}
