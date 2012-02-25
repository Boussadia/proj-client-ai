package com.mademoisellegeek.ia;

import java.util.ArrayList;

/**
 *
 * @author cbaldock
 */
public class Tour {
    
    ArrayList<Deplacement> deplacements = new ArrayList<Deplacement>();
    Case cible;
    
    public Tour(ArrayList<Deplacement> deplacements, Case cible) {
        this.deplacements = deplacements;
        this.cible = cible;
    }
    
}
