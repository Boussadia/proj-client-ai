package com.mademoisellegeek.ia;

import com.mademoisellegeek.ia.alphabeta.Minimax;
import com.mademoisellegeek.ia.data.*;
import com.mademoisellegeek.ia.utils.GrilleUtils;
import java.util.ArrayList;
import java.util.LinkedList;

public class Grille extends Minimax implements Cloneable {

    private int lignes;
    private int colonnes;
    private Case caseDepart;
    private int[][] humains;
    private int[][] vampires;
    private int[][] loups;
    private boolean nousSommesVampires;
    private static int HUMAIN = 1000;
    private static int MONSTRE = 1500;

    public Grille(int nbLignes, int nbColonnes) {
        this.lignes = nbLignes;
        this.colonnes = nbColonnes;
        this.humains = new int[nbColonnes][nbLignes];
        this.vampires = new int[nbColonnes][nbLignes];
        this.loups = new int[nbColonnes][nbLignes];
    }

    void setCaseDepart(int xDepart, int yDepart) {
        this.caseDepart = new Case(xDepart, yDepart);
    }

    Case getCaseDepart() {
        return this.caseDepart;
    }

    void ajouterHumain(int xHumain, int yHumain) {
        System.out.println("ajout de x " + xHumain + " ajout de y " + yHumain);
        this.humains[xHumain][yHumain]++;
    }

    void vider() {
        this.humains = new int[colonnes][lignes];
        this.vampires = new int[colonnes][lignes];
        this.loups = new int[colonnes][lignes];
    }

    void update(int xCase, int yCase, int nbHumains, int nbVampires, int nbLoupsGarous) {
        this.humains[xCase][yCase] = nbHumains;
        this.vampires[xCase][yCase] = nbVampires;
        this.loups[xCase][yCase] = nbLoupsGarous;
    }

    @Override
    public int getCurrentScore() {
        int score = 0;
        for (int i=0; i<colonnes; i++) {
            for (int j=0; j<lignes; j++) {
                if (humains[i][j]!=0) {
                    /*
                     * Pour chaque case, calculer le nombre de coups qu'il faut pour l'attaquer
                     */
                    int nbMonstresNecessaires = humains[i][j];
                    int nbVampiresMovesNeeded = GrilleUtils.distanceVampires(i,j,nbMonstresNecessaires, vampires, lignes, colonnes);
                    int nbLoupsMovesNeeded = GrilleUtils.distanceLoups(i,j,nbMonstresNecessaires, loups, lignes, colonnes);
                    switch (nousSommesVampires) 
                    {
                        case 0: // Cas où nous sommes des loups garous
                            System.out.println("Nous sommes des loups garous ");
                            if (nbVampiresMovesNeeded<nbLoupsMovesNeeded) // Il nous faut plus de coups que nos ennemis
                            {
                                score -= humains[i][j]*HUMAIN;
                            }
                            else
                            {
                                score += humains[i][j]*HUMAIN;
                            }
                            
                            break;
                            
                        case 1: // Cas où nous sommes des vampires
                            System.out.println("Nous sommes des vampires ");
                            if (nbVampiresMovesNeeded<nbLoupsMovesNeeded) // Il nous faut moins de coups que nos ennemis
                            {
                                /*
                                 * Si on peut attraper les humains
                                 * avant les autres, le score augmente.
                                 */
                                score += humains[i][j]*HUMAIN;
                            }
                            else
                            {
                                score -= humains[i][j]*HUMAIN;
                            }
                            
                            break;
                        default:
                            break;
                    }
                }
                
                if (vampires[i][j]!=0) // S'il y a des vampires
                {
                        int seuil =3;
                        int nbLoupsNecessaires = 1,5*vampires[i][j];// Règle pour gagner
                        int nbLoupsMovesNeeded = GrilleUtils.distanceLoups(i,j,nbLoupsNecessaires, loups, lignes, colonnes);// Nombre de coups pour manger ces vampires
                        
                        switch (nousSommesVampires) 
                    {
                        case 0: // Cas où nous sommes des loups garous
                            System.out.println("Nous sommes des loups garous ");
                            if (nbLoupsMovesNeeded<seuil) // Il nous faut moins de coups qu'un seuil
                            {
                                score += vampires[i][j]*MONSTRE;// Score augmente proportionnellement aux vampires qu'on va manger
                            }
                            break;
                            
                        case 1: // Cas où nous sommes des vampires
                            System.out.println("Nous sommes des vampires ");
                            if (nbLoupsMovesNeeded<seuil) // Il leur faut moins de coups qu'un seuil
                            {
                                score -= vampires[i][j]*MONSTRE;// Score diminue proportionnellement aux vampires qu'on va se faire manger
                            }
                            break;
                        default:
                            break;
                    }
                }

                if (loups[i][j]!=0) // S'il y a des loups
                {
                        int seuil =3;
                        int nbVampiresNecessaires = 1,5*loups[i][j];// Règle pour gagner
                        int nbVampiresMovesNeeded = GrilleUtils.distanceVampires(i,j,nbVampiresNecessaires, vampires, lignes, colonnes);// Nombre de coups pour manger ces vampires
                        
                        switch (nousSommesVampires) 
                    {
                        case 0: // Cas où nous sommes des loups garous
                            System.out.println("Nous sommes des loups garous ");
                            if (nbVampiresMovesNeeded<seuil) // Il leur faut moins de coups qu'un seuil
                            {
                                score -= loups[i][j]*MONSTRE;// Score diminue proportionnellement aux loups qu'on va se faire manger
                            }
                            break;
                            
                        case 1: // Cas où nous sommes des vampires
                            System.out.println("Nous sommes des vampires ");
                            if (nbVampiresMovesNeeded<seuil) // Il nous faut moins de coups qu'un seuil
                            {
                                score += loups[i][j]*MONSTRE;// Score augmente proportionnellement aux loups qu'on va manger
                            }
                
                            break;
                        default:
                            break;
                    }
                }
                
                
            }
        }
                    
        //TODO JEAN protéger nos vampires, cette fonction est kamikaze
        return score;
    }

    @Override
    public LinkedList<Tour> listAllLegalMoves() {
        LinkedList<Tour> touslesTours;
        if (nousSommesVampires) {
            touslesTours = this.mouvementsVampiresPossibles();
        } else {
            touslesTours = this.mouvementsLoupsPossibles();
        }
        return touslesTours;
    }

    @Override
    public void moveAction(Tour tour) {
        if (tour.getType() == TypeTour.DEPLACEMENT) {
            ArrayList<Mouvement> mouvements = ((Deplacement) tour).getMouvements();
            for (Mouvement mouvement : mouvements) {
                int xDepart = mouvement.getXDepart();
                int yDepart = mouvement.getYDepart();
                int xArrivee = mouvement.getXArrivee();
                int yArrivee = mouvement.getYArrivee();
                if (tour.estTourDesVampires()) {
                    this.update(xDepart,
                            yDepart,
                            0,
                            vampires[xDepart][yDepart] - mouvement.getNbIndividus(),
                            0);
                    this.update(xArrivee,
                            yArrivee,
                            0,
                            vampires[xArrivee][yArrivee] + mouvement.getNbIndividus(),
                            0);
                } else {
                    this.update(xDepart,
                            yDepart,
                            0,
                            0,
                            loups[xDepart][yDepart] - mouvement.getNbIndividus());
                    this.update(xArrivee,
                            yArrivee,
                            0,
                            0,
                            loups[xArrivee][yArrivee] + mouvement.getNbIndividus());
                }
            }
        } else {
            Case cible = ((Attaque) tour).getCible();
            //TODO CLB aleatoire
            //TODO CLB fixer tous les bugs
            boolean tourDesVampires = tour.estTourDesVampires();
            ArrayList<Case> casesAdjacentes = GrilleUtils.getCasesAdjacentes(cible, tourDesVampires, vampires, loups, humains, colonnes, lignes);
            if (humains[cible.getX()][cible.getY()] > 0) {
                int nombreAttaquants = nombreAttaquants(casesAdjacentes, tourDesVampires);
                    if (tourDesVampires) {
                        this.update(cible.getX(), cible.getY(), 0, nombreAttaquants, 0);
                    } else {
                        this.update(cible.getX(), cible.getY(), 0, 0, nombreAttaquants);
                    }
            }
        }
    }
    @Override
    public void sendAction(Tour tour) {
        if (tour.getType() == TypeTour.DEPLACEMENT) {
            Client.sendMov((Deplacement)tour);
        } else {
            Client.sendAtk(((Attaque)tour).getCible());
        }
    }

    public LinkedList<Tour> mouvementsVampiresPossibles() {
        LinkedList<Tour> list = new LinkedList<Tour>();
        for (int i = 0; i < colonnes; i++) {
            for (int j = 0; j < lignes; j++) {
                if (vampires[i][j] != 0) {
                    Case start = new Case(i, j);
                    //mouvements possibles
                    GrilleUtils.addMouvementsPossibles(i, j, false, list, vampires, loups, humains, colonnes, lignes);
                    //attaques possibles
                    ArrayList<Case> casesEnnemisAdjacentes = GrilleUtils.getCasesAdjacentes(i, j, false, true, true, vampires, loups, humains, colonnes, lignes);
                    for (Case caseAdjacente : casesEnnemisAdjacentes) {
                        list.add(new Attaque(caseAdjacente, true, estAttaqueAleatoire(caseAdjacente, true)));
                    }
                }
            }
        }
        return list;
    }

    public LinkedList<Tour> mouvementsLoupsPossibles() {
        LinkedList<Tour> list = new LinkedList<Tour>();
        for (int i = 0; i < colonnes; i++) {
            for (int j = 0; j < lignes; j++) {
                if (loups[i][j] != 0) {
                    Case start = new Case(i, j);
                    //mouvements possibles
                    GrilleUtils.addMouvementsPossibles(i, j, false, list, vampires, loups, humains, colonnes, lignes);
                    //attaques possibles
                    ArrayList<Case> casesEnnemisAdjacentes = GrilleUtils.getCasesAdjacentes(i, j, true, false, true, vampires, loups, humains, colonnes, lignes);
                    for (Case caseAdjacente : casesEnnemisAdjacentes) {
                        list.add(new Attaque(caseAdjacente, false, estAttaqueAleatoire(caseAdjacente, false)));
                    }
                }
            }
        }
        return list;
    }

    public void printout() {
        System.out.println("nb colonnes ");
        System.out.println("nb lignes ");
        for (int i = 0; i < colonnes; i++) {
            for (int j = 0; j < lignes; j++) {
                if (vampires[i][j] != 0) {
                    System.out.println(vampires[i][j] + " vampires " + "sur case " + i + " ," + j);
                }
            }
        }
        for (int i = 0; i < colonnes; i++) {
            for (int j = 0; j < lignes; j++) {
                if (loups[i][j] != 0) {
                    System.out.println(loups[i][j] + " loups " + "sur case " + i + " ," + j);
                }
            }
        }
        for (int i = 0; i < colonnes; i++) {
            for (int j = 0; j < lignes; j++) {
                if (humains[i][j] != 0) {
                    System.out.println(humains[i][j] + " humains " + "sur case " + i + " ," + j);
                }
            }
        }
    }

    private int nombreAttaquants(ArrayList<Case> casesAdjacentes, boolean tourDesVampires) {
        int result = 0;
        for (Case caseAdjacente : casesAdjacentes) {
            if (tourDesVampires) {
                result += vampires[caseAdjacente.getX()][caseAdjacente.getY()];
            } else {
                result += loups[caseAdjacente.getX()][caseAdjacente.getY()];
            }
        }
        return result;
    }

    public void setNousSommesVampires() {
        this.nousSommesVampires = (vampires[caseDepart.getX()][caseDepart.getY()] > 0);
    }

    public boolean getNousSommesVampires() {
        return this.nousSommesVampires;
    }

    private boolean estAttaqueAleatoire(Case caseAdjacente, boolean estVampires) {
        int nbVampires = vampires[caseAdjacente.getX()][caseAdjacente.getY()];
        int nbLoups = loups[caseAdjacente.getX()][caseAdjacente.getY()];
        int nbHumains = humains[caseAdjacente.getX()][caseAdjacente.getY()];
        if (estVampires) {
            if (nbHumains > 0) {
                return (nbHumains > nbVampires);
            } else {
                return (1.5 * nbLoups > nbVampires);
            }
        } else {
            if (nbHumains > 0) {
                return (nbHumains > nbLoups);
            } else {
                return (1.5 * nbVampires > nbLoups);
            }
        }
    }   
}