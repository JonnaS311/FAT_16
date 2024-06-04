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
    public TablaFAT(){
        this.clusterNumber = 65524; // para: 2GiB - 1
        this.sector = 64; // estandar de 64 sectores
        this.table = new int[this.clusterNumber];
        Arrays.fill(this.table,0);
    }

    //Override Constructor params: tamanio disco y cantidad de sectores
    public TablaFAT(int clusterNumber, int sector, double bad){
        this.clusterNumber = clusterNumber;
        this.sector = sector;
        this.table = new int[this.clusterNumber];
        System.out.println("tamaño: " + this.clusterNumber*sector*512);
        Arrays.fill(this.table,0);
        // Asignamos de manera aleatoria clusters dañados
        DaniarClusters(bad);
    }

    public boolean CreateFile(int size){
        // verificamos cuantos bloques ocupa el archivo
        int clusters = Math.ceilDivExact(size,this.sector*512);
        int[] tmp = Arrays.copyOf(this.table, this.table.length);
        boolean isFirst = true;
        int pos_first =-1;
        System.out.println("cantidad de cluster a usar: "+clusters);
        // buscamos el primer cluster libre
        for (int i = 0; i < this.table.length; i++) {
            if(this.table[i] == 0 && clusters>0 ){
                // buscamos el siguiente cluster al que vamos a hacer referencia
                for (int j = i+1; j < this.table.length; j++) {
                    if (this.table[j] == 0){
                        if (isFirst){
                            pos_first = i;
                            isFirst = false;
                        }
                        // END OF FILE is represented by -1
                        this.table[i] = (clusters==1)? EOF: j;
                        clusters -=1;
                        break;
                    }
                }
            }else if (clusters == 0){
                break;
            }
        }
        System.out.println(clusters);
        if (clusters > 1){
            this.table = tmp;
            return false;
        }
        this.table_first_cluster.add(pos_first);
        return true;
    }
    public boolean DeleteFile(int first_cluster){
        // TODO: eliminar un archivo tomando la referencia a la siguiente posicion dentro de la tabla FAT
        return false;
    }

    public void DaniarClusters (double desuso){
        if (desuso > 1){
            desuso = 1;
        }
        Random random = new Random();
        int cant_bloques = (int) (this.table.length*desuso*0.28);
        for (int i = 0; i < cant_bloques; i++) {
            int position = random.nextInt(table.length-1);
            this.table[position] = BAD_CLUSTER;
        }
    }

    // Auto-generated Setters & Getters
    public int getClusterNumber() {
        return clusterNumber;
    }

    public void setClusterNumber(int clusterNumber) {
        this.clusterNumber = clusterNumber;
    }

    public int getSector() {
        return sector;
    }

    public void setSector(int sector) {
        this.sector = sector;
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