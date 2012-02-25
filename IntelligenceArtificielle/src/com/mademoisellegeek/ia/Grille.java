package com.mademoisellegeek.ia;

import java.util.LinkedList;

public class Grille extends Minimax implements Cloneable {

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

    @Override
    public void staleMate() {
        //TODO est-ce possible de ne plus pouvoir bouger ???
    }

    public LinkedList<Tour> mouvementsVampiresPossibles() {
        LinkedList<Tour> list = new LinkedList<Tour>();
        //TODO
        return list;
    }
    
    public LinkedList<Tour> mouvementsLoupsPossibles() {
        LinkedList<Tour> list = new LinkedList<Tour>();
        //TODO
        return list;
    }
}
