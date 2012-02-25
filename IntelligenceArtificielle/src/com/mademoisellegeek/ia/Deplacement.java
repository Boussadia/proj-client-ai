package com.mademoisellegeek.ia;

/**
 *
 * @author cbaldock
 */
class Deplacement {

    private int nbIndividus;
    private Case caseDepart;
    private Case caseArrivee;
    
    public Deplacement(Case caseDepart, Case caseArrivee, int nbIndividus) {
        this.caseDepart = caseDepart;
        this.caseArrivee = caseArrivee;
    }
    
}
