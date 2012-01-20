package com.mademoisellegeek.ia;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    //TODO à mettre dans un fichier config
    private static String host = "10.0.0.9";
    private static int port = 5555;
    
    private Socket socket;
    private byte[] trame;
    private InputStream in;
    private OutputStream out;
    private Grille grille;

    public static void main(String[] args) throws Exception {
        
        //Lecture des arguments pour avoir le port et l'adresse du serveur
        if(args.length !=4){
            System.out.println("Pour lancer l'application, il faut donner en paramétres le port (-p) et l'adresse (-h) du serveur.");
            System.out.println("Par exemple : -p 1000 -h 192.168.0.1");
            return;
        }else{
            //On cherche le port : -p
            int tempPort = 0;
            int indexPort = 0;
            
            if(args[0].equals("-p")){
                indexPort = 0;
            }else if(args[0].equals("-p")){
                indexPort = 2;
            }else{
                System.out.println("Il faut renseigner un port!");
                System.out.println("Exemple : -p 1000");
                return;
            }
            
            try{
                tempPort = Integer.parseInt(args[indexPort+1]);
            }catch(Exception e){
                System.out.println("Le port indiqué : "+args[indexPort+1]+" n'est pas valide!");
                return;
            }
            port = tempPort;
            
            //On cherche l'adresse du serveur : -h
            String tempHost = "";
            int indexHost = 0;
            
            if(args[0].equals("-h")){
                indexHost = 0;
            }else if(args[0].equals("-h")){
                indexHost = 2;
            }else{
                System.out.println("Il faut renseigner un host!");
                System.out.println("Exemple : -h 192.168.0.2");
                return;
            }
            
            try{
                tempHost = args[indexHost+1];
            }catch(Exception e){
                System.out.println("Le host indiqué : "+args[indexHost+1]+" n'est pas valide!");
                return;
            }
        }
        

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
        
        trame = new byte[9999];
        sendNme();
        // Définition de la trame
        //TODO ce 9999 est juste moche
    }
    
    // Méthode qui permet de recevoir une trame (Turn dans le cas nominal)
    boolean receiveTrame() throws Exception {
        System.out.println("WOOO");
        trame = new byte[3];
        int nbBytesLus = in.read(trame, 0, 3);
        if (nbBytesLus != 3)
            throw new Exception("Erreur de lecture de l'entête de trame");

        String typeTrame = new String(trame, "ASCII");
        System.out.println("on a reçu une trame de type" + typeTrame);
        if (typeTrame.equalsIgnoreCase("SET")) {
            trame = new byte[2];
            nbBytesLus = in.read(trame, 0, 2);
            if (nbBytesLus != 2)
                throw new Exception("Erreur de lecture de la trame SET");
            receiveSet(trame);
        }
        else if (typeTrame.equalsIgnoreCase("HUM")) {
            trame = new byte[1];
            nbBytesLus = in.read(trame, 0, 1);
            if (nbBytesLus != 1)
                throw new Exception("Erreur de lecture de N de la trame HUM");
            int N = (int)trame[0] & 0xff;
            trame = new byte[2*N];
            nbBytesLus = in.read(trame, 0, 2*N);
            if (nbBytesLus != 2*N)
                throw new Exception("Erreur de lecture des données de la trame HUM");
            receiveHum(N, trame);
        }
        else if (typeTrame.equalsIgnoreCase("HME")) {
            trame = new byte[2];
            nbBytesLus = in.read(trame, 0, 2);
            if (nbBytesLus != 2)
                throw new Exception("Erreur de lecture de la trame HME");
            receiveHme(trame);
        }
        else if (typeTrame.equalsIgnoreCase("UPD")) {
            trame = new byte[1];
            nbBytesLus = in.read(trame, 0, 1);
            if (nbBytesLus != 1)
                throw new Exception("Erreur de lecture de N de la trame UPD");
            int N = (int)trame[0] & 0xff;
            trame = new byte[5*N];
            nbBytesLus = in.read(trame, 0, 5*N);
            if (nbBytesLus != 5*N)
                throw new Exception("Erreur de lecture des données de la trame UPD");
            receiveUpd(N, trame);
            System.out.println("debut mouvement");
            sendMov(4, 5, 2, 4, 4);
            System.out.println("on a envoyé le mouvement");
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
        System.out.println("SET" + nbLignes +"lignes" + nbColonnes + "colonnes");
        grille = new Grille(nbLignes, nbColonnes);
    }
        
    //Méthode qui indique les maisons dans la grille
    void receiveHum(int nbMaisons, byte[] bytes) {
        for (int i=0; i<nbMaisons; i++) {
            int xHumain = (int)bytes[2*i] & 0xff;
            int yHumain = (int)bytes[2*i+1] & 0xff;
            System.out.println("HUM" + xHumain + "   " + yHumain);
            grille.ajouterHumain(xHumain, yHumain);
        }
    }
        
    //Méthode qui indique la case de départ
    void receiveHme(byte[] bytes) {
        int xDepart = (int)trame[0] & 0xff;
        int yDepart = (int)trame[1] & 0xff;
        System.out.println("HOME" + xDepart + "   " + yDepart);
        grille.setCaseDepart(xDepart, yDepart);
    }

    //Méthode qui indique les modifications à apporter à la grille
    void receiveUpd(int nbUpdates, byte[] bytes) {
        for (int i=0; i<nbUpdates; i++) {
            int xCase = (int)trame[5*i] & 0xff;
            int yCase = (int)trame[5*i+1] & 0xff;
            int nbHumains = (int)trame[5*i+2] & 0xff;
            int nbVampires = (int)trame[5*i+3] & 0xff;
            int nbLoupsGarous = (int)trame[5*i+4] & 0xff;
            System.out.println("UPDATE" + xCase + " " + yCase + " " + nbHumains + " " + nbVampires + "   " + nbLoupsGarous);
            grille.update(xCase, yCase, nbHumains, nbVampires, nbLoupsGarous);
        }
    }
    
    //Méthode qui indique que la partie est terminée
    void receiveEnd() {
        //nettoyer la grille
        grille.vider();
    }
    
    //Méthode qui indique le nom du jouer
    void sendNme() {
        //TODO choisir un meilleur nom d'équipe
        trame = new byte[8];
        trame[0] = 'N';
        trame[1] = 'M';
        trame[2] = 'E';
        trame[3] = (byte)4;
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
        trame = new byte[8];
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
        trame = new byte[5];
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
