package org.fat;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        TablaFAT test = new TablaFAT(65524,64, 0.0);
        test.createFile(62*65524*512);
        test.createFile(160000);
        test.createFile(56);
        test.deleteFile(63477);
        System.out.println(Arrays.toString(test.getTable()));
        System.out.println(test.getTable_first_cluster());
        
    }
}


