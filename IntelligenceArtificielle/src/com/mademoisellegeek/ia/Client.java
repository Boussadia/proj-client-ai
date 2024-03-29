package com.mademoisellegeek.ia;

import com.mademoisellegeek.ia.config.ConfigTextParser;
import com.mademoisellegeek.ia.data.Case;
import com.mademoisellegeek.ia.data.Deplacement;
import com.mademoisellegeek.ia.data.Mouvement;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    private static String host;
    private static int port;
    private Socket socket;
    private static InputStream in;
    private static OutputStream out;
    private Grille grille;
    private static String nomEquipe;
    private static ConfigTextParser parser;

    public static void main(String[] args) throws Exception {
        
        getConfigValues(args);

        Client client = new Client();
        while (true) {
            boolean isByeTrame = client.receiveTrame();
            if (isByeTrame) {
                break;
            }
        }
        client.closeAll();
    }

    // Constructeur de la classe client
    public Client() {
        System.out.println("\nConnexion à " + host + " sur le port : " + port);

        try {
            socket = new Socket(host, port);
            in = socket.getInputStream();
            out = socket.getOutputStream();
            sendNme();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Connexion établie avec le serveur.\n");
    }

    // Méthode qui permet de recevoir une trame (Turn dans le cas nominal)
    boolean receiveTrame() throws Exception {;
        byte[] trame = new byte[3];
        int nbBytesLus = in.read(trame, 0, 3);
        if (nbBytesLus != 3) {
            throw new Exception("Erreur de lecture de l'entête de trame");
        }

        String typeTrame = new String(trame, "ASCII");
        System.out.println("on a reçu une trame de type " + typeTrame);
        if (typeTrame.equalsIgnoreCase("SET")) {
            trame = new byte[2];
            nbBytesLus = in.read(trame, 0, 2);
            if (nbBytesLus != 2) {
                throw new Exception("Erreur de lecture de la trame SET");
            }
            receiveSet(trame);
        } else if (typeTrame.equalsIgnoreCase("HUM")) {
            trame = new byte[1];
            nbBytesLus = in.read(trame, 0, 1);
            if (nbBytesLus != 1) {
                throw new Exception("Erreur de lecture de N de la trame HUM");
            }
            int N = (int) trame[0] & 0xff;
            trame = new byte[2 * N];
            nbBytesLus = in.read(trame, 0, 2 * N);
            if (nbBytesLus != 2 * N) {
                throw new Exception("Erreur de lecture des données de la trame HUM");
            }
            receiveHum(N, trame);
        } else if (typeTrame.equalsIgnoreCase("HME")) {
            trame = new byte[2];
            nbBytesLus = in.read(trame, 0, 2);
            if (nbBytesLus != 2) {
                throw new Exception("Erreur de lecture de la trame HME");
            }
            receiveHme(trame);
        } else if (typeTrame.equalsIgnoreCase("MAP")) {
            trame = new byte[1];
            nbBytesLus = in.read(trame, 0, 1);
            if (nbBytesLus != 1) {
                throw new Exception("Erreur de lecture de N de la trame MAP");
            }
            int N = (int) trame[0] & 0xff;
            trame = new byte[5 * N];
            nbBytesLus = in.read(trame, 0, 5 * N);
            if (nbBytesLus != 5 * N) {
                throw new Exception("Erreur de lecture des données de la trame MAP");
            }
            receiveMap(N, trame);
        } else if (typeTrame.equalsIgnoreCase("UPD")) {
            trame = new byte[1];
            nbBytesLus = in.read(trame, 0, 1);
            if (nbBytesLus != 1) {
                throw new Exception("Erreur de lecture de N de la trame UPD");
            }
            int N = (int) trame[0] & 0xff;
            trame = new byte[5 * N];
            nbBytesLus = in.read(trame, 0, 5 * N);
            if (nbBytesLus != 5 * N) {
                throw new Exception("Erreur de lecture des données de la trame UPD");
            }
            receiveUpd(N, trame);
        } else if (typeTrame.equalsIgnoreCase("END")) {
            receiveEnd();
        } else if (typeTrame.equalsIgnoreCase("BYE")) {
            return true;
        } else {
            throw new Exception("Trame non reconnue");
        }
        return false;
    }

    //Méthode qui permet au serveur de communiquer la grille
    void receiveSet(byte[] bytes) {
        int nbLignes = (int) bytes[0] & 0xff;
        int nbColonnes = (int) bytes[1] & 0xff;
        System.out.println("SET" + nbLignes + "lignes" + nbColonnes + "colonnes");
        grille = new Grille(nbLignes, nbColonnes);
    }

    //Méthode qui indique les maisons dans la grille
    void receiveHum(int nbMaisons, byte[] bytes) {
        for (int i = 0; i < nbMaisons; i++) {
            int xHumain = (int) bytes[2 * i] & 0xff;
            int yHumain = (int) bytes[2 * i + 1] & 0xff;
            System.out.println("HUM" + xHumain + "   " + yHumain);
            grille.ajouterHumain(xHumain, yHumain);
        }
    }

    //Méthode qui indique la case de départ
    void receiveHme(byte[] bytes) {
        int xDepart = (int) bytes[0] & 0xff;
        int yDepart = (int) bytes[1] & 0xff;
        System.out.println("HOME" + xDepart + "   " + yDepart);
        grille.setCaseDepart(xDepart, yDepart);
    }

    //Méthode qui indique les modifications à apporter à la grille
    void receiveUpd(int nbUpdates, byte[] bytes) {
        for (int i = 0; i < nbUpdates; i++) {
            int xCase = (int) bytes[5 * i] & 0xff;
            int yCase = (int) bytes[5 * i + 1] & 0xff;
            int nbHumains = (int) bytes[5 * i + 2] & 0xff;
            int nbVampires = (int) bytes[5 * i + 3] & 0xff;
            int nbLoupsGarous = (int) bytes[5 * i + 4] & 0xff;
            System.out.println("UPDATE" + xCase + " " + yCase + " " + nbHumains + " " + nbVampires + "   " + nbLoupsGarous);
            grille.update(xCase, yCase, nbHumains, nbVampires, nbLoupsGarous);
        }
        grille.makePerfectMove();//TODO AHMED PROFONDEUR + CHRONO

    }

    //Méthode qui reçoit la grille pour la première fois
    void receiveMap(int nbUpdates, byte[] bytes) {
        for (int i = 0; i < nbUpdates; i++) {
            int xCase = (int) bytes[5 * i] & 0xff;
            int yCase = (int) bytes[5 * i + 1] & 0xff;
            int nbHumains = (int) bytes[5 * i + 2] & 0xff;
            int nbVampires = (int) bytes[5 * i + 3] & 0xff;
            int nbLoupsGarous = (int) bytes[5 * i + 4] & 0xff;
            System.out.println("MAP" + xCase + " " + yCase + " " + nbHumains + " " + nbVampires + "   " + nbLoupsGarous);
            grille.update(xCase, yCase, nbHumains, nbVampires, nbLoupsGarous);
        }
        grille.setNousSommesVampires();
    }

    //Méthode qui indique que la partie est terminée
    void receiveEnd() {
        grille.vider();
    }

    /**
     * NME: Envoyer le nom de l'équipe
     * (assume que le nom de l'équipe a déjà été fixé)
     */
    static void sendNme() {
        System.out.println("SENT NME : " + nomEquipe.charAt(0) + nomEquipe.charAt(1) + nomEquipe.charAt(2));
        byte[] trame = new byte[7];
        trame[0] = 'N';
        trame[1] = 'M';
        trame[2] = 'E';
        trame[3] = (byte) 3;
        trame[4] = (byte) nomEquipe.charAt(0);
        trame[5] = (byte) nomEquipe.charAt(1);
        trame[6] = (byte) nomEquipe.charAt(2);
        try {
            out.write(trame, 0, 7);
        } catch (Exception e) {
            System.out.println("Erreur d'écriture de la trame NME");
        }
        System.out.println("La trame NME est envoyée au serveur.");
    }

    /**
     * MOV: Effectuer un déplacement de monstres (1 à 3 mouvements)
     *
     * @param Deplacement deplacement Déplacement à effectuer
     */
    static void sendMov(Deplacement deplacement) {
        int nbMouvements = deplacement.getMouvements().size();
        byte[] trame = new byte[4 + 5 * nbMouvements];
        trame[0] = 'M';
        trame[1] = 'O';
        trame[2] = 'V';
        trame[3] = (byte) nbMouvements;
        int index = 4;
        for (Mouvement mouvement : deplacement.getMouvements()) {
            trame[index] = (byte) mouvement.getXDepart();
            trame[index + 1] = (byte) mouvement.getYDepart();
            trame[index + 2] = (byte) mouvement.getNbIndividus();
            trame[index + 3] = (byte) mouvement.getXArrivee();
            trame[index + 4] = (byte) mouvement.getYArrivee();
            index += 5;
        }
        try {
            out.write(trame, 0, 4 + 5 * nbMouvements);
        } catch (Exception e) {
            System.out.println("Erreur d'écriture de la trame MOV");
        }
        System.out.println("La trame MOV est envoyée au serveur");
    }

    /**
     * ATK: Attaquer une case
     *
     * @param Case cible Case cible de l'attaque
     */
    static void sendAtk(Case cible) {
        byte[] trame = new byte[5];
        trame[0] = 'A';
        trame[1] = 'T';
        trame[2] = 'K';
        trame[3] = (byte) cible.getX();
        trame[4] = (byte) cible.getY();
        try {
            out.write(trame, 0, 5);
        } catch (Exception e) {
            System.out.println("Erreur d'écriture de la trame ATK");
        }
        System.out.println("La trame ATK est envoyée au serveur");
    }

    /**
     * Fermer le socket
     */
    void closeAll() throws Exception {
        in.close();
        out.close();
        socket.close();
    }

    /**
     * Obtenir les valeurs de configuration; si aucun argument n'est donné,
     * considérer les valeurs dans le fichier "config.txt"; si des arguments de
     * forme -c text.txt sont donnés, les valeurs dans le fichier "text.txt"
     * sont considérées
     * @param String[] args Arguments ligne de commande
     */
    private static void getConfigValues(String[] args) {

        String pathToConfigFile;

        if (args.length != 2) {
            System.out.println("Pour lancer l'application, il faut donner en paramétres le chemin vers le fichier de congfig (-c).");
            System.out.println("Par exemple : -c \"Path/To/config.txt\"");
            return;
        } else {
            //On cherche le chemin du fichier de configuration : -c
            if (args[0].equals("-c")) {
                pathToConfigFile = args[1];
                System.out.println("Chemin vers le fichier de configuration : " + pathToConfigFile);

                parser = new ConfigTextParser(pathToConfigFile);
                host = parser.getHost();
                port = parser.getPort();
                nomEquipe = parser.getName().toUpperCase();
            } else {
                System.out.println("Pour lancer l'application, il faut donner en paramétres le chemin vers le fichier de congfig (-c).");
                System.out.println("Par exemple : -c \"Path/To/config.txt\"");
                return;
            }
        }
    }
}
