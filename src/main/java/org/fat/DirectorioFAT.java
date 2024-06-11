package org.fat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class DirectorioFAT {
    public String nombre;
    public ArrayList<EntradaDirectorio> entradas;
    public ArrayList<DirectorioFAT> subdirectorios;
    public final int ATTRIBUTE = 16;
    public Date dirCreatedDate;
    public Date dirModifiedDate;
    public int primerCluster;

    public DirectorioFAT(String nombre) {
        this.nombre = nombre;
        this.entradas = new ArrayList<>();
        this.subdirectorios = new ArrayList<>();
        this.dirCreatedDate = new Date();
        this.dirModifiedDate = new Date();
    }

    public void agregarEntrada(EntradaDirectorio entrada) {
        this.entradas.add(entrada);
    }

    public void agregarSubdirectorio(DirectorioFAT subdirectorio) {
        this.subdirectorios.add(subdirectorio);
    }

    public void eliminarSubdirectorio(int posicion) {
        this.subdirectorios.remove(posicion);
    }

    public DirectorioFAT buscarSubdirectorio(String nombre) {
        for (DirectorioFAT subdir : subdirectorios) {
            if (subdir.nombre.equals(nombre)) {
                return subdir;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Directorio: " + nombre + ", Archivos: " + entradas.size() + ", Subdirectorios: " + subdirectorios.size();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<DirectorioFAT> getSubdirectorios() {
        return subdirectorios;
    }

    public ArrayList<EntradaDirectorio> getEntradas() {
        return entradas;
    }

    public Date getDirCreatedDate() {
        return dirCreatedDate;
    }

    public Date getDirModifiedDate() {
        return dirModifiedDate;
    }

    public void setDirModifiedDate(Date dirModifiedDate) {
        this.dirModifiedDate = dirModifiedDate;
    }

    public int getATTRIBUTE() {
        return ATTRIBUTE;
    }

    public int getPrimerCluster() {
        return primerCluster;
    }

    public void setPrimerCluster(int primerCluster) {
        this.primerCluster = primerCluster;
    }
}
