package com.mademoisellegeek.ia;

/**
 *
 * @author cbaldock
 */
public class Tour {
    
    private boolean vampires;
    private TypeTour type;
    
    public boolean estTourDesVampires() {
        return this.vampires;
    }
    
    public TypeTour getType() {
        return this.type;
    }
    
    public void setType(TypeTour type) {
        this.type = type;
    }
    
    public void setIsVampires(boolean vampires) {
        this.vampires = vampires;
    }
}
