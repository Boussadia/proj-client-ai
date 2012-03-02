/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mademoisellegeek.ia.utils;

import com.mademoisellegeek.ia.data.Case;
import com.mademoisellegeek.ia.data.Deplacement;
import com.mademoisellegeek.ia.data.Mouvement;
import com.mademoisellegeek.ia.data.Tour;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author cbaldock
 */
public class GrilleUtils {

    public static ArrayList<Case> getCasesAdjacentes(int x,
            int y,
            boolean vampiresPresents,
            boolean loupsPresents,
            boolean humainsPresents,
            int[][] vampires,
            int[][] loups,
            int[][] humains,
            int colonnes,
            int lignes) {

        ArrayList<Case> cases = new ArrayList<Case>();
        //case x,y-1
        if ((y > 0) && ((!vampiresPresents && vampires[x][y - 1] == 0)
                || (!loupsPresents && loups[x][y - 1] == 0)
                || (!humainsPresents && humains[x][y - 1] == 0))) {
            cases.add(new Case(x, y - 1));
        }
        //case x,y+1
        if ((y < lignes - 1) && ((!vampiresPresents && vampires[x][y + 1] == 0)
                || (!loupsPresents && loups[x][y + 1] == 0)
                || (!humainsPresents && humains[x][y + 1] == 0))) {
            cases.add(new Case(x, y + 1));
        }
        //case x-1,y
        if ((x > 0) && ((!vampiresPresents && vampires[x - 1][y] == 0)
                || (!loupsPresents && loups[x - 1][y] == 0)
                || (!humainsPresents && humains[x - 1][y] == 0))) {
            cases.add(new Case(x - 1, y));
        }
        //case x+1,y
        if ((x < colonnes - 1) && ((!vampiresPresents && vampires[x + 1][y] == 0)
                || (!loupsPresents && loups[x + 1][y] == 0)
                || (!humainsPresents && humains[x + 1][y] == 0))) {
            cases.add(new Case(x + 1, y));
        }
        return cases;
    }

    public static ArrayList<Case> getCasesAdjacentes(Case cible,
            boolean estVampires,
            int[][] vampires,
            int[][] loups,
            int[][] humains,
            int colonnes,
            int lignes) {
        return getCasesAdjacentes(cible.getX(), cible.getY(), estVampires, !estVampires, false, vampires, loups, humains, colonnes, lignes);
    }

    public static void addMouvementsPossibles(int i, 
            int j, 
            boolean estVampires, 
            LinkedList<Tour> list, 
            int[][] vampires,
            int[][] loups,
            int[][] humains,
            int colonnes,
            int lignes) {
        Case depart = new Case(i, j);
        ArrayList<Case> casesAdjacentes = getCasesAdjacentes(depart, estVampires, vampires, loups, humains, colonnes, lignes);
        int[] empty = null;
        ArrayList<int[]> partitions = Utils.partition(empty, 5, casesAdjacentes.size() + 1);
        for (int[] partition : partitions) {
            ArrayList<Mouvement> mouvements = new ArrayList<Mouvement>();
            for (int k = 0; k < casesAdjacentes.size(); k++) {
                if (partition[k]!=0) {
                    mouvements.add(new Mouvement(depart, casesAdjacentes.get(k), partition[k]));
                }
            }
            Deplacement deplacement = new Deplacement(mouvements, estVampires);
            list.add(deplacement);
        }
    }
    
    public static int distanceVampires(int i, int j, double nbMonstresNecessaires, int[][] vampires, int lignes, int colonnes) {
        int[] dict = new int[lignes+colonnes];
        /* dictionnaire dont la taille correspond 
         * à la distance Manhattan maximale lignes+colonnes */
        
        for (int k=0; k<colonnes; k++) {
            for (int l=0; l<lignes; l++) {
                dict[Utils.distance(i,j,k,l)] += vampires[k][l];
                /*
                 Dictionnaire en fonction de la distance Manhattan donne 
                 le nombre de vampires à portée de coups m.
                 * dict[2] donne le nombre de vampires qui sont à deux coups des
                 * humains de la case (i,j)
                 */
            }
        }
        int m=-1;// Pourquoi -1 et pas 0 ?
        int nbVampires = 0;
        while (nbVampires<nbMonstresNecessaires && m<lignes+colonnes-1) {
            m++;//on incrémente le nombre de coups
            nbVampires += dict[m];// On rajoute les vampires à portée m
        }
        /*
         * Nombre de coups qu'il faut faire pour
         * attaquer les humains avec des vampires depuis la case (i,j)
         */
        return m;/* distance Manhattan*/
    }
    
    public static int distanceLoups(int i, int j, double nbMonstresNecessaires, int[][] loups, int lignes, int colonnes) {
        int[] dict = new int[lignes+colonnes];
        for (int k=0; k<colonnes; k++) {
            for (int l=0; l<lignes; l++) {
                dict[Utils.distance(i,j,k,l)] += loups[k][l];
            }
        }
        int m=-1;
        int nbVampires = 0;
        while (nbVampires<nbMonstresNecessaires && m<(lignes+colonnes-1)) {
            m++;
            nbVampires += dict[m];
        }
        return m;
    }
}
