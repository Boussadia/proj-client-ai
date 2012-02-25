package com.mademoisellegeek.ia;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class Grille extends Minimax implements Cloneable {

    private int lignes;
    private int colonnes;
    private Case caseDepart;
    private int[][] humains;
    private int[][] vampires;
    private int[][] loups;

    public Grille(int nbLignes, int nbColonnes) {
        this.lignes = nbLignes;
        this.colonnes = nbColonnes;
        this.humains = new int[nbLignes][nbColonnes];
        this.vampires = new int[nbLignes][nbColonnes];
        this.loups = new int[nbLignes][nbColonnes];
    }

    void setCaseDepart(int xDepart, int yDepart) {
        this.caseDepart = new Case(xDepart, yDepart);
    }

    void ajouterHumain(int xHumain, int yHumain) {
        this.humains[xHumain][yHumain]++;
    }

    void vider() {
        this.humains = new int[lignes][colonnes];
        this.vampires = new int[lignes][colonnes];
        this.loups = new int[lignes][colonnes];
    }

    void update(int xCase, int yCase, int nbHumains, int nbVampires, int nbLoupsGarous) {
        this.humains[xCase][yCase] = nbHumains;
        this.vampires[xCase][yCase] = nbVampires;
        this.loups[xCase][yCase] = nbLoupsGarous;
    }

    @Override
    public int getCurrentScore() {
        //TODO fonction de calcul du "score" de chaque grille
        return 1;
    }

    @Override
    public LinkedList<Tour> listAllLegalMoves() {
        LinkedList<Tour> touslesTours = this.getPlayer() == Minimax.MAX_TURN ? this.mouvementsVampiresPossibles() : this.mouvementsLoupsPossibles();
        return touslesTours;
    }

    @Override
    public void moveAction(Tour tour) {
        //TODO faire l'action et mettre à jour la grille
    }

    public LinkedList<Tour> mouvementsVampiresPossibles() {
        LinkedList<Tour> list = new LinkedList<Tour>();
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                if (vampires[i][j] != 0) {
                    Case caseDepart = new Case(i, j);
                    //mouvements possibles
                    ArrayList<Case> casesVidesAdjacentes = getCasesAdjacentes(i, j, true, false, false);
                    addCombs(casesVidesAdjacentes, new Stack(), vampires[i][j], list, caseDepart);
                    //attaques possibles
                    ArrayList<Case> casesEnnemisAdjacentes = getCasesAdjacentes(i, j, false, true, true);
                    for (Case caseAdjacente : casesEnnemisAdjacentes) {
                        list.add(new Tour(null, caseAdjacente));
                    }
                }
            }
        }
        return list;
    }

    public LinkedList<Tour> mouvementsLoupsPossibles() {
        LinkedList<Tour> list = new LinkedList<Tour>();
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                if (loups[i][j] != 0) {
                    Case caseDepart = new Case(i, j);
                    //mouvements possibles
                    ArrayList<Case> casesVidesAdjacentes = getCasesAdjacentes(i, j, false, true, false);
                    addCombs(casesVidesAdjacentes, new Stack(), vampires[i][j], list, caseDepart);
                    //attaques possibles
                    ArrayList<Case> casesEnnemisAdjacentes = getCasesAdjacentes(i, j, true, false, true);
                    for (Case caseAdjacente : casesEnnemisAdjacentes) {
                        list.add(new Tour(null, caseAdjacente));
                    }
                }
            }
        }
        return list;
    }

    private ArrayList<Case> getCasesAdjacentes(int x, int y, boolean vampires, boolean loups, boolean humains) {
        ArrayList<Case> cases = new ArrayList<Case>();
        //TODO
        return cases;
    }

    public static void addCombs(ArrayList<Case> casesVidesAdjacentes, Stack<Integer> used, int val, LinkedList<Tour> list, Case caseDepart) {
        if (val == 0) {
            ArrayList<Deplacement> deplacements = getDeplacements(used, casesVidesAdjacentes, caseDepart);
            list.add(new Tour(deplacements, null));
            return;
        }
        if (val < 0) {
            return;
        }

        for (int i = val; i >= 0; i--) {
            if (used.size() < casesVidesAdjacentes.size()) {
                used.push(i);
                addCombs(casesVidesAdjacentes, used, val - i, list, caseDepart);
                used.pop();
            }
        }
    }

    private static ArrayList<Deplacement> getDeplacements(Stack<Integer> used, ArrayList<Case> casesVidesAdjacentes, Case caseDepart) {
        ArrayList<Deplacement> deplacements = new ArrayList<Deplacement>();
        for (int i=0; i<used.size(); i++) {
            deplacements.add(new Deplacement(caseDepart, casesVidesAdjacentes.get(i), used.pop()));
        }
        return deplacements;
    }
}