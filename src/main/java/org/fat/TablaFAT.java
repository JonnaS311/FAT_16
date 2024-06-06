package org.fat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class TablaFAT {
    private int clusterNumber;
    private int sector;
    private final int SECTOR_SIZE = 512;
    private final int BAD_CLUSTER = 65527; // representado por 0xFFF7
    private final int EOF = 65535; // representado por 0xFFFF
    private int[] table;
    private final ArrayList<Integer> table_first_cluster = new ArrayList<>();

    // Constructor por defecto
    public TablaFAT() {
        this.clusterNumber = 65524; // para: 2GiB - 1
        this.sector = 64; // estándar de 64 sectores = 32Kb que es lo máximo permitido por FAT16
        this.table = new int[this.clusterNumber];
        Arrays.fill(this.table, 0);
    }

    // Constructor con parámetros: tamaño del disco, cantidad de sectores y porcentaje de clusters dañados
    public TablaFAT(int clusterNumber, int sector, double bad) {
        this.clusterNumber = Math.max(clusterNumber, 4085); 
        this.sector = Math.max(sector, 1);
        this.table = new int[this.clusterNumber];
        System.out.println("tamaño: " + this.clusterNumber * sector * 512);
        Arrays.fill(this.table, 0);
        daniarClusters(bad);
    }

    // Método para crear un archivo en la tabla FAT
    public boolean createFile(int size) {
        // Verificamos cuantos bloques ocupa el archivo
        int clusters = (int) Math.ceil((double) size / (this.sector * SECTOR_SIZE));
        int[] tmp = Arrays.copyOf(this.table, this.table.length);
        boolean isFirst = true;
        int pos_first = -1;
        int last_pos = -1;
        System.out.println("Cantidad de clusters a usar: " + clusters);

        // Buscamos el primer cluster libre
        for (int i = 0; i < this.table.length && clusters > 0; i++) {
            if (this.table[i] == 0) {
                if (isFirst) {
                    pos_first = i;
                    isFirst = false;
                } else {
                    this.table[last_pos] = i;
                }
                last_pos = i;
                clusters--;
            }
        }

        // Si no se encontraron suficientes clusters libres, revertimos los cambios
        if (clusters > 0) {
            this.table = tmp;
            return false;
        }

        this.table[last_pos] = EOF;
        this.table_first_cluster.add(pos_first);
        return true;
    }

    // Método para eliminar un archivo de la tabla FAT
    public boolean deleteFile(int first_cluster) {
        int next_cluster;
        int current_cluster = first_cluster;

        // Liberamos los clusters usados por el archivo
        while (current_cluster != EOF) {
            next_cluster = this.table[current_cluster];
            this.table[current_cluster] = 0;
            current_cluster = next_cluster;
        }

        // Eliminamos el primer cluster de la tabla de primeros clusters
        this.table_first_cluster.remove((Integer) first_cluster);
        return true;
    }

    // Método para dañar clusters de manera aleatoria
    private void daniarClusters(double desuso) {
        if (desuso > 1) {
            desuso = 1;
        } else if (desuso < 0) {
            desuso = 0;
        }
        Random random = new Random();
        int cant_bloques = (int) (this.table.length * desuso * 0.28);
        for (int i = 0; i < cant_bloques; i++) {
            int position = random.nextInt(this.table.length);
            this.table[position] = BAD_CLUSTER;
        }
    }
    public void imprimirTablaFAT() {
        System.out.println("Estado de la Tabla FAT:");
        for (int i = 0; i < this.table.length; i++) {
            if (this.table[i] != 0 && this.table[i] != BAD_CLUSTER) {
                System.out.printf("Clúster %d: %s%n", i, (this.table[i] == EOF) ? "EOF" : String.valueOf(this.table[i]));
            }
        }
    }
    // Getters & Setters generados automáticamente
    public int getClusterNumber() {
        return clusterNumber;
    }

    public void setClusterNumber(int clusterNumber) {
        this.clusterNumber = Math.max(clusterNumber, 4085);
    }

    public int getSector() {
        return sector;
    }

    public void setSector(int sector) {
        this.sector = Math.max(sector, 1);
    }

    public int[] getTable() {
        return table;
    }

    public int getSECTOR_SIZE() {
        return SECTOR_SIZE;
    }

    public int getBAD_CLUSTER() {
        return BAD_CLUSTER;
    }

    public int getEOF() {
        return EOF;
    }

    public ArrayList<Integer> getTable_first_cluster() {
        return table_first_cluster;
    }
}
