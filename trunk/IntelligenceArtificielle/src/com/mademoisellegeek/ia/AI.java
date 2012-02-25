package com.mademoisellegeek.ia;

public class AI {

    private static int nbTours;

    public AI() {
        nbTours = 0;
    }

    public static void testJouer(Grille grille) {
        switch (nbTours) {
            case 0:
                Client.sendMov(5, 4, 1, 5, 3);
                break;
            case 1:
                Client.sendMov(5, 3, 1, 5, 2);
                break;
            case 2:
                Client.sendMov(5, 2, 1, 4, 2);
                break;
            case 3:
                Client.sendMov(4, 2, 1, 3, 2);
                break;
            case 4:
                Client.sendMov(3, 2, 1, 2, 2);
                break;
            case 5:
                Client.sendMov(2, 2, 1, 3, 3);
                break;
            case 6:
                Client.sendAtk(2, 3);
                break;
        }
        nbTours++;
    }
    
    public static void jouer(Grille grille) {
        grille.makePerfectMove(3);//pour l'instant 3 
        //TODO ajouter le chrono et ajouter réglage de profondeur
        return;
    }
}
