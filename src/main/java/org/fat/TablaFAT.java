package org.fat;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class TablaFAT {
    private int clusterNumber;
    private int sector;
    private final int SECTOR_SIZE = 512;
    private final int BAD_CLUSTER = 65527; // represented by 0xFFF7
    private final int EOF = 65535; // represented by 0xFFFF
    private int[] table;
    private final ArrayList<Integer> table_first_cluster = new ArrayList<Integer>();

    // Constructor Default
    public TablaFAT() {
        this.clusterNumber = 65524; // para: 2GiB - 1
        this.sector = 64; // estandar de 64 sectores
        this.table = new int[this.clusterNumber];
        Arrays.fill(this.table, 0);
    }

    //Override Constructor params: tamanio disco, cantidad de sectores y porcentaje de clusters dañados
    public TablaFAT(int clusterNumber, int sector, double bad) {
        this.clusterNumber = Math.max(clusterNumber, 4085); // verificar que la cantidad de cluster sea mayor a la permitida
        this.sector = Math.max(sector, 1);
        this.table = new int[this.clusterNumber];
        System.out.println("tamaño: " + this.clusterNumber * sector * 512);
        Arrays.fill(this.table, 0);
        // Asignamos de manera aleatoria clusters dañados
        DaniarClusters(bad);
    }

    public boolean CreateFile(int size) {
        // verificamos cuantos bloques ocupa el archivo
        int clusters = Math.ceilDivExact(size, this.sector * 512);
        int[] tmp = Arrays.copyOf(this.table, this.table.length);
        boolean isFirst = true;
        int pos_first = -1;
        System.out.println("cantidad de cluster a usar: " + clusters);
        // buscamos el primer cluster libre
        for (int i = 0; i < this.table.length; i++) {
            if (this.table[i] == 0 && clusters > 0) {
                // buscamos el siguiente cluster al que vamos a hacer referencia
                for (int j = i + 1; j < this.table.length; j++) {
                    if (this.table[j] == 0) {
                        if (isFirst) {
                            pos_first = i;
                            isFirst = false;
                        }
                        // END OF FILE is represented by -1
                        this.table[i] = (clusters == 1) ? EOF : j;
                        clusters -= 1;
                        break;
                    }
                }
            } else if (clusters == 0) {
                break;
            }
        }
        System.out.println(clusters);
        if (clusters > 1) {
            this.table = tmp;
            return false;
        }
        this.table_first_cluster.add(pos_first);
        return true;
    }

    public boolean DeleteFile(int first_cluster) {
        int tmp = 0;
        tmp = this.table[first_cluster];
        this.table[first_cluster] = 0;
        for (int i = first_cluster; i < this.table.length; i++) {
            if (i == tmp) {
                tmp = this.table[i];
                this.table[i] = 0;
                if (tmp == this.EOF) {
                    break;
                }
            }
        }
        // eliminar de tabla de primer cluster
        this.table_first_cluster.remove((Integer) first_cluster);
        return true;
    }

    private void DaniarClusters(double desuso) {
        if (desuso > 1) {
            desuso = 1;
        } else if (desuso < 0) {
            desuso = 0;
        }
        Random random = new Random();
        int cant_bloques = (int) (this.table.length * desuso * 0.28);
        for (int i = 0; i < cant_bloques; i++) {
            int position = random.nextInt(table.length - 1);
            this.table[position] = BAD_CLUSTER;
        }
    }

    public ArrayList<int[]> getFormattedTable() {
        ArrayList<int[]> tabla = new ArrayList<int[]>();
        int[] tmp = new int[8];
        int[] buffer = new int[8];
        for (int i = 0; i < this.table.length; i++) {
            tmp[i%8] = this.table[i];
            // cada vez que se llena con 8 elementos procede a guardalo como una copia en el arraylist
            if(i%8==7){
                tabla.add(tmp.clone());
                tmp = new int[8];
            }
        }
        // si los ultimos valores no alcanzaron a ser almacenados los guarda
        for (int num: tmp){
            tabla.add(tmp.clone());
            break;
        }
        return tabla;
    }

    // Auto-generated Setters & Getters
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
