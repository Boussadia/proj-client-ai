/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mademoisellegeek.ia.data;

/**
 *
 * @author cbaldock
 */
public class Mouvement {
    
    private int nbIndividus;
    private Case caseDepart;
    private Case caseArrivee;
    
    public Mouvement(Case caseDepart, Case caseArrivee, int nbIndividus) {
        this.nbIndividus = nbIndividus;
        this.caseDepart = caseDepart;
        this.caseArrivee = caseArrivee;
    }
    
    public int getXDepart() {
        return this.caseDepart.getX();
    }
    
    public int getYDepart() {
        return this.caseDepart.getY();
    }
    
    public int getNbIndividus() {
        return this.nbIndividus;
    }
    
    public int getXArrivee() {
        return this.caseArrivee.getX();
    }
    
    public int getYArrivee() {
        return this.caseArrivee.getY();
    }  
    
}
