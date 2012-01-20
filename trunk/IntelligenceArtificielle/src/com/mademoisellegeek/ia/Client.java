package com.mademoisellegeek.ia;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    //TODO à mettre dans un fichier config
    private static final String host = "10.0.0.6";
    private static final int port = 5555;
    
    private Socket socket;
    private byte[] trame;
    private InputStream in;
    private OutputStream out;
    private Grille grille;

    public static void main(String[] args) throws Exception {

        // Création d'une instance d'un client
        Client client = new Client();
        // On boucle à l'infini jusqu'à la fin de la partie 
        while (true) {
            boolean isByeTrame = client.receiveTrame();
            if (isByeTrame) {
                //quand la trame de fin de partie est reçue
                break;
            }
        }
        // Fermer le socket client
        client.closeAll();
    }

    // Constructeur de la classe client
    public Client() {
        System.out.println("\nConnexion à " + host + " sur le port : " + port);

        try {
            socket = new Socket(host, port);
            in = socket.getInputStream();
            out = socket.getOutputStream();
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Connexion établie avec le serveur.\n");

        // Définition de la trame
        //TODO ce 9999 est juste moche
        trame = new byte[9999];
    }
    
    // Méthode qui permet de recevoir une trame (Turn dans le cas nominal)
    boolean receiveTrame() throws Exception {
        int nbBytesLus = in.read(trame, 0, 3);
        if (nbBytesLus != 3)
            throw new Exception("Erreur de lecture de l'entête de trame");
        
        //todo ASCII
        String typeTrame = new String(trame, "ASCII");
        
        if (typeTrame.equalsIgnoreCase("SET")) {
            nbBytesLus = in.read(trame, 0, 2);
            if (nbBytesLus != 2)
                throw new Exception("Erreur de lecture de la trame SET");
            receiveSet(trame);
        }
        else if (typeTrame.equalsIgnoreCase("HUM")) {
            nbBytesLus = in.read(trame, 0, 1);
            if (nbBytesLus != 1)
                throw new Exception("Erreur de lecture de N de la trame HUM");
            int N = (int)trame[0] & 0xff;
            nbBytesLus = in.read(trame, 0, 2*N);
            if (nbBytesLus != 2*N)
                throw new Exception("Erreur de lecture des données de la trame HUM");
            receiveHum(N, trame);
        }
        else if (typeTrame.equalsIgnoreCase("HME")) {
            nbBytesLus = in.read(trame, 0, 2);
            if (nbBytesLus != 2)
                throw new Exception("Erreur de lecture de la trame HME");
            receiveHme(trame);
        }
        else if (typeTrame.equalsIgnoreCase("UPD")) {
            nbBytesLus = in.read(trame, 0, 1);
            if (nbBytesLus != 1)
                throw new Exception("Erreur de lecture de N de la trame UPD");
            int N = (int)trame[0] & 0xff;
            nbBytesLus = in.read(trame, 0, 5*N);
            if (nbBytesLus != 5*N)
                throw new Exception("Erreur de lecture des données de la trame UPD");
            receiveUpd(trame);
        }
        else if (typeTrame.equalsIgnoreCase("END")) {
            receiveEnd();
        }
        else if (typeTrame.equalsIgnoreCase("BYE")) {
            return true;
        }
        else {
            throw new Exception ("Trame non reconnue");
        }
        return false;
    }

    //Méthode qui permet au serveur de communiquer la grille
    void receiveSet(byte[] bytes) {
        int nbLignes = (int)trame[0] & 0xff;
        int nbColonnes = (int)trame[1] & 0xff;
        grille = new Grille(nbLignes, nbColonnes);
    }
        
    //Méthode qui indique les maisons dans la grille
    void receiveHum(int nbMaisons, byte[] bytes) {
        for (int i=0; i<nbMaisons; i++) {
            int xHumain = (int)bytes[2*i] & 0xff;
            int yHumain = (int)bytes[2*i+1] & 0xff;
            grille.ajouterHumain(xHumain, yHumain);
        }
    }
        
    //Méthode qui indique la case de départ
    void receiveHme(byte[] bytes) {
        int xDepart = (int)trame[0] & 0xff;
        int xArrivee = (int)trame[1] & 0xff;
        grille.setCaseDepart(xDepart, xArrivee);
    }

    //Méthode qui indique les modifications à apporter à la grille
    void receiveUpd(byte[] bytes) {
        //TODO
    }
    
    //Méthode qui indique que la partie est terminée
    void receiveEnd() {
        //TODO
    }
    
    //Méthode qui indique le nom du jouer
    void sendNme() {
        //TODO choisir un meilleur nom d'équipe
        trame[0] = 'N';
        trame[1] = 'M';
        trame[2] = 'E';
        trame[3] = 'T';
        trame[4] = 'A';
        trame[5] = 'N';
        trame[6] = 'O';
        trame[7] = 'N';
        try {
            out.write(trame, 0, 8);
        }
        catch (Exception e) {
            System.out.println("Erreur d'écriture de la trame NME");
        }
        System.out.println("La trame NME est envoyée au serveur.");
    }
    
    //Méthode qui déplace des individus d'une case à l'autre
    void sendMov(int xDepart, int yDepart, int nbIndividus, int xArrivee, int yArrivee) {
        trame[0] = 'M';
        trame[1] = 'O';
        trame[2] = 'V';
        trame[3] = (byte)xDepart;
        trame[4] = (byte)yDepart;
        trame[5] = (byte)nbIndividus;
        trame[6] = (byte)xArrivee;
        trame[7] = (byte)yArrivee;
        try {
            out.write(trame, 0, 8);
        }
        catch (Exception e) {
            System.out.println("Erreur d'écriture de la trame MOV");
        }
        System.out.println("La trame MOV est envoyée au serveur");
    }
    
    //Méthode qui attaque une case
    void sendAtk(int xCible, int yCible) {
        trame[0]='A';
        trame[1]='T';
        trame[2]='K';
        trame[3]=(byte)xCible;
        trame[4]=(byte)yCible;
        try {
            out.write(trame, 0, 5);
        }
        catch (Exception e) {
            System.out.println("Erreur d'écriture de la trame ATK");
        }
        System.out.println("La trame ATK est envoyée au serveur");
        
    }
    
    // Méthode qui permet de fermer le socket client
    void closeAll() throws Exception {
        in.close();
        out.close();
        socket.close();
    }
}
