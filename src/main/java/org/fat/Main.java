package org.fat;

import java.util.Arrays;

import static java.lang.Integer.toHexString;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        TablaFAT test = new TablaFAT(65524,64, 0.1);

        test.CreateFile(62*65524*512);
        test.CreateFile(160000);
        test.CreateFile(56);
        System.out.println(Arrays.toString(test.getTable()));
        System.out.println(test.getTable_first_cluster());
    }
}