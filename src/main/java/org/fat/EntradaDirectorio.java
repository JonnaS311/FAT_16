package org.fat;


import java.util.Date;

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

    public String getNombre() {
        return file.getName();
    }

    public String getExtension() {
        return file.getExtension();
    }

    public int getPrimerCluster() {
        return primerCluster;
    }

    public int getTamaño() {
        return file.getSize();
    }

    public Date getFechaCreación() {
        return file.getCreationDate();
    }

    public Date getFechaModificación() {
        return file.getModificationDate();
    }

    public Date getÚltimoAcceso() {
        return file.getLastAccessDate();
    }

    public int getAtributo() {
        return file.getAttribute();
    }

    public String getContent() {
        return file.getContent();
    }
}
