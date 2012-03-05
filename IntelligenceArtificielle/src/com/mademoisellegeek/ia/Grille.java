package com.mademoisellegeek.ia;

import com.mademoisellegeek.ia.alphabeta.Minimax;
import com.mademoisellegeek.ia.data.*;
import com.mademoisellegeek.ia.utils.Utils;
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
        for (int i = 0; i < colonnes; i++) {
            for (int j = 0; j < lignes; j++) {
                if (humains[i][j] != 0) {
                    /*
                     * Pour chaque case, calculer le nombre de coups qu'il faut
                     * pour l'attaquer
                     */
                    int nbMonstresNecessaires = humains[i][j];
                    int nbVampiresMovesNeeded = distanceVampires(i, j, nbMonstresNecessaires);
                    int nbLoupsMovesNeeded = distanceLoups(i, j, nbMonstresNecessaires);
                    if (!nousSommesVampires) { // Cas où nous sommes des loups garous
                        System.out.println("Nous sommes des loups garous ");
                        if (nbVampiresMovesNeeded < nbLoupsMovesNeeded) // Il nous faut plus de coups que nos ennemis
                        {
                            score -= humains[i][j] * HUMAIN;
                        } else {
                            score += humains[i][j] * HUMAIN;
                        }
                    } else if (nousSommesVampires) {// Cas où nous sommes des vampires
                        System.out.println("Nous sommes des vampires ");
                        if (nbVampiresMovesNeeded < nbLoupsMovesNeeded) // Il nous faut moins de coups que nos ennemis
                        {
                            /*
                             * Si on peut attraper les humains avant les autres,
                             * le score augmente.
                             */
                            score += humains[i][j] * HUMAIN;
                        } else {
                            score -= humains[i][j] * HUMAIN;
                        }
                    }
                }

                if (vampires[i][j] != 0) // S'il y a des vampires
                {
                    int seuil = 3;
                    double nbLoupsNecessaires = 1.5 * vampires[i][j];// Règle pour gagner
                    int nbLoupsMovesNeeded = distanceLoups(i, j, nbLoupsNecessaires);// Nombre de coups pour manger ces vampires

                    if (!nousSommesVampires) {// Cas où nous sommes des loups garous
                        System.out.println("Nous sommes des loups garous ");
                        if (nbLoupsMovesNeeded < seuil) // Il nous faut moins de coups qu'un seuil
                        {
                            score += vampires[i][j] * MONSTRE;// Score augmente proportionnellement aux vampires qu'on va manger
                        }
                    } else if (nousSommesVampires) {// Cas où nous sommes des vampires
                        System.out.println("Nous sommes des vampires ");
                        if (nbLoupsMovesNeeded < seuil) // Il leur faut moins de coups qu'un seuil
                        {
                            score -= vampires[i][j] * MONSTRE;// Score diminue proportionnellement aux vampires qu'on va se faire manger
                        }
                    }
                }

                if (loups[i][j] != 0) // S'il y a des loups
                {
                    int seuil = 3;
                    double nbVampiresNecessaires = 1.5 * loups[i][j];// Règle pour gagner
                    int nbVampiresMovesNeeded = distanceVampires(i, j, nbVampiresNecessaires);// Nombre de coups pour manger ces vampires

                    if (!nousSommesVampires) {// Cas où nous sommes des loups garous
                        System.out.println("Nous sommes des loups garous ");
                        if (nbVampiresMovesNeeded < seuil) // Il leur faut moins de coups qu'un seuil
                        {
                            score -= loups[i][j] * MONSTRE;// Score diminue proportionnellement aux loups qu'on va se faire manger
                        }
                    } else if (nousSommesVampires) {// Cas où nous sommes des vampires
                        System.out.println("Nous sommes des vampires ");
                        if (nbVampiresMovesNeeded < seuil) // Il nous faut moins de coups qu'un seuil
                        {
                            score += loups[i][j] * MONSTRE;// Score augmente proportionnellement aux loups qu'on va manger
                        }
                    }
                }


            }
        }
        return score;
    }

    @Override
    public LinkedList<Tour> tousLesToursPossibles() {
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
            ArrayList<Case> casesAdjacentes = getCasesAdjacentes(cible, tourDesVampires);
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
    public void executerTour(Tour tour) {
        if (tour.getType() == TypeTour.DEPLACEMENT) {
            Client.sendMov((Deplacement) tour);
        } else {
            Client.sendAtk(((Attaque) tour).getCible());
        }
    }

    public LinkedList<Tour> mouvementsVampiresPossibles() {
        LinkedList<Tour> list = new LinkedList<Tour>();
        for (int i = 0; i < colonnes; i++) {
            for (int j = 0; j < lignes; j++) {
                if (vampires[i][j] != 0) {
                    Case start = new Case(i, j);
                    //mouvements possibles
                    addMouvementsPossibles(i, j, false, list);
                    //attaques possibles
                    ArrayList<Case> casesEnnemisAdjacentes = getCasesAdjacentes(i, j, false, true, true);
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
                    addMouvementsPossibles(i, j, false, list);
                    //attaques possibles
                    ArrayList<Case> casesEnnemisAdjacentes = getCasesAdjacentes(i, j, true, false, true);
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

    public boolean verifierPresence(int i, int j, boolean vampiresPresents, boolean loupsPresents, boolean humainsPresents) {
        boolean result = (!vampiresPresents && vampires[i][j] == 0)
                || (!loupsPresents && loups[i][j] == 0)
                || (!humainsPresents && humains[i][j] == 0);
        return result;
    }

    public ArrayList<Case> getCasesAdjacentes(int x,
            int y,
            boolean vampiresPresents,
            boolean loupsPresents,
            boolean humainsPresents) {

        ArrayList<Case> cases = new ArrayList<Case>();
        //case x-1,y-1
        if ((x > 0) && (y > 0) && (verifierPresence(x - 1, y - 1, vampiresPresents, loupsPresents, humainsPresents))) {
            cases.add(new Case(x - 1, y - 1));
        }
        //case x,y-1
        if ((y > 0) && (verifierPresence(x, y - 1, vampiresPresents, loupsPresents, humainsPresents))) {
            cases.add(new Case(x, y - 1));
        }
        //case x+1,y-1
        if ((x < colonnes - 1) && (y > 0) && (verifierPresence(x + 1, y - 1, vampiresPresents, loupsPresents, humainsPresents))) {
            cases.add(new Case(x + 1, y - 1));
        }
        //case x-1,y
        if ((x > 0) && (verifierPresence(x - 1, y, vampiresPresents, loupsPresents, humainsPresents))) {
            cases.add(new Case(x - 1, y));
        }
        //case x+1,y
        if ((x < colonnes - 1) && (verifierPresence(x + 1, y, vampiresPresents, loupsPresents, humainsPresents))) {
            cases.add(new Case(x + 1, y));
        }
        //case x-1,y+1
        if ((x > 0) && (y < lignes - 1) && (verifierPresence(x - 1, y + 1, vampiresPresents, loupsPresents, humainsPresents))) {
            cases.add(new Case(x - 1, y + 1));
        }
        //case x,y+1
        if ((y < lignes - 1) && (verifierPresence(x, y + 1, vampiresPresents, loupsPresents, humainsPresents))) {
            cases.add(new Case(x, y + 1));
        }
        //case x+1,y+1
        if ((x < colonnes - 1) && (y < lignes - 1) && (verifierPresence(x + 1, y + 1, vampiresPresents, loupsPresents, humainsPresents))) {
            cases.add(new Case(x + 1, y + 1));
        }
        return cases;
    }

    public ArrayList<Case> getCasesAdjacentes(Case cible,
            boolean estVampires) {
        return getCasesAdjacentes(cible.getX(), cible.getY(), estVampires, !estVampires, false);
    }
    
    public void addMouvementsPossibles(int i, 
            int j, 
            boolean estVampires, 
            LinkedList<Tour> list) {
        Case depart = new Case(i, j);
        ArrayList<Case> casesAdjacentes = getCasesAdjacentes(depart, estVampires);
        int[] empty = null;
        ArrayList<int[]> partitions;
        if (estVampires) {
            partitions = Utils.partition(empty, vampires[i][j], casesAdjacentes.size() + 1);
        }
        else {
            partitions = Utils.partition(empty, loups[i][j], casesAdjacentes.size() + 1);
        }
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
    
    public int distanceVampires(int i, int j, double nbMonstresNecessaires) {
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
    
    public int distanceLoups(int i, int j, double nbMonstresNecessaires) {
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