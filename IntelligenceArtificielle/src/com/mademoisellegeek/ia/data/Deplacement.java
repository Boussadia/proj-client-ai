package com.mademoisellegeek.ia.data;



import com.mademoisellegeek.ia.data.Tour;
import java.util.ArrayList;

/**
 *
 * @author cbaldock
 */
public class Deplacement extends Tour {

    private ArrayList<Mouvement> mouvements;

    public Deplacement(ArrayList<Mouvement> mouvements, boolean vampires) {
        this.mouvements = mouvements;
        this.setIsVampires(vampires);
        this.setType(TypeTour.DEPLACEMENT);
    }

    public ArrayList<Mouvement> getMouvements() {
        return this.mouvements;
    }

    public void printout() {
        for (Mouvement mouvement : this.mouvements) {
            System.out.println("Mouvement de "+
                               mouvement.getNbIndividus()+
                               " personnes de "+
                               mouvement.getXDepart()+
                               ","+
                               mouvement.getYDepart()+
                               " à "+
                               mouvement.getXArrivee()+
                               ","+
                               mouvement.getYArrivee());
        }
    }
}