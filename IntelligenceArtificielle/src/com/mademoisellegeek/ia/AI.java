package com.mademoisellegeek.ia;

import java.util.ArrayList;

public class AI {

    private static int nbTours;

    public AI() {
        nbTours = 0;
    }

    public static void testJouer(Grille grille) {
        grille.printout();
        ArrayList<Mouvement> mouvements = new ArrayList<Mouvement>();
        switch (nbTours) {
            case 0:
                Mouvement mouvement = new Mouvement(new Case(28,14), new Case(27,14), 5);
                mouvements.add(mouvement);
                Deplacement deplacement = new Deplacement(mouvements, true);
                Client.sendMov(deplacement);
                break;
            case 1:
                Mouvement mouvement2 = new Mouvement(new Case(27,14), new Case(26,14), 3);
                mouvements.add(mouvement2);
                Deplacement deplacement2 = new Deplacement(mouvements, true);
                Client.sendMov(deplacement2);
                break;
        }
        nbTours++;
    }
    
    public static void jouer(Grille grille) {
        grille.makePerfectMove(26);//TODO profondeur ???
        return;
    }
}
