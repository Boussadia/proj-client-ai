/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mademoisellegeek.ia.utils;

import java.util.ArrayList;

/**
 *
 * @author cbaldock
 */
public class Utils {

    public static ArrayList<int[]> partition(int[] beginning, int sum, int nbCases) {
        ArrayList<int[]> result = new ArrayList<int[]>();
        if (sum == 0) {
            int[] zero = {0};
            int[] temp = beginning;
            for (int i = 1; i <= nbCases - beginning.length; i++) {
                temp = concatenate(temp, zero);
            }
            result.add(temp);
        }
        if (beginning != null && beginning.length == 4) {
            return result;
        }
        if (sum != 0) {
            for (int i = sum; i >= 0; i--) {
                int[] numberArray = {i};
                ArrayList<int[]> partitions = partition(concatenate(beginning, numberArray), sum - i, nbCases);
                for (int[] array : partitions) {
                    result.add(array);
                }
            }
        }
        return result;
    }

    public static int[] concatenate(int[] array1, int[] array2) {
        if (array1 == null) {
            return array2;
        }
        if (array2 == null) {
            return array1;
        }
        int[] array1and2 = new int[array1.length + array2.length];
        System.arraycopy(array1, 0, array1and2, 0, array1.length);
        System.arraycopy(array2, 0, array1and2, array1.length, array2.length);
        return array1and2;

    }

    public static Integer distance(int i, int j, int k, int l) {
        return Math.abs(i - k) + Math.abs(j - l);
    }
}
