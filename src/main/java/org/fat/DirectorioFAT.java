package org.fat;

import java.util.ArrayList;

public class DirectorioFAT {
    public String nombre;
    public ArrayList<EntradaDirectorio> entradas;
    public ArrayList<DirectorioFAT> subdirectorios;

    public DirectorioFAT(String nombre) {
        this.nombre = nombre;
        this.entradas = new ArrayList<>();
        this.subdirectorios = new ArrayList<>();
    }

    public void agregarEntrada(EntradaDirectorio entrada) {
        this.entradas.add(entrada);
    }

    public void agregarSubdirectorio(DirectorioFAT subdirectorio) {
        this.subdirectorios.add(subdirectorio);
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

    public ArrayList<DirectorioFAT> getSubdirectorios() {
        return subdirectorios;
    }

    public ArrayList<EntradaDirectorio> getEntradas() {
        return entradas;
    }
}
