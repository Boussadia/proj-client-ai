package com.mademoisellegeek.ia;

public class Grille {

    private int lignes;
    private int colonnes;
    private int ligneDepart;
    private int colonneDepart;
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
        this.ligneDepart = xDepart;
        this.colonneDepart = yDepart;
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
}
