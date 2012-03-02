package com.mademoisellegeek.ia.data;

import com.mademoisellegeek.ia.data.Tour;

/**
 *
 * @author cbaldock
 */
public class Attaque extends Tour {

    private Case cible;
    private boolean aleatoire;

    public Attaque(Case cible, boolean vampires, boolean aleatoire) {
        this.cible = cible;
        this.aleatoire = aleatoire;
        this.setIsVampires(vampires);
        this.setType(TypeTour.ATTAQUE);
    }

    public Case getCible() {
        return this.cible;
    }
}
