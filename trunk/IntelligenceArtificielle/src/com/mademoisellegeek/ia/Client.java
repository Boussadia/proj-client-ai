package com.mademoisellegeek.ia;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    //TODO à mettre dans un fichier config
    private static final String host = "127.0.0.1";
    private static final int port = 5555;
    
    private Socket socket;
    private byte[] trame;
    private InputStream in;
    private OutputStream out;

    public static void main(String[] args) throws Exception {

        // Création d'une instance d'un client
        Client client = new Client();

        // On boucle à l'infini jusqu'à la fin de la partie 
        while (true) {
            client.receiveTrame();
            if (false) {
                //quand la trame de fin de partie est reçue
                break;
            }
        }

        // Fermer le socket client
        client.closeAll();
    }

    // Constructeur de la classe client
    public Client() throws Exception {
        System.out.println("\nConnexion à " + host + " sur le port : " + port);

        // Création du socket client
        socket = new Socket(host, port);

        System.out.println("Connexion établie avec le serveur.\n");

        // Les données en entrée et en sortie
        in = socket.getInputStream();
        out = socket.getOutputStream();

        // Définition de la trame
        //TODO ce 9999 est juste moche
        trame = new byte[9999];
    }
    
        // Méthode qui permet de recevoir une trame (Turn dans le cas nominal)
    void receiveTrame() throws Exception {
        int nbBytesLus = in.read(trame, 0, 3);
        if (nbBytesLus != 3)
            throw new Exception("TODO");
        
        String typeTrame = new String(trame, "Cp1252");
        
        if (typeTrame.equalsIgnoreCase("SET")) {
            nbBytesLus = in.read(trame, 0, 2);
            if (nbBytesLus != 2)
                throw new Exception("TODO");
            receiveSet(trame);
        }
        else if (typeTrame.equalsIgnoreCase("HUM")) {
            nbBytesLus = in.read(trame, 0, 1);
            if (nbBytesLus != 1)
                throw new Exception("TODO");
            int N = (int)trame[0] & 0xff;
            nbBytesLus = in.read(trame, 0, 2*N);
            if (nbBytesLus != 2*N)
                throw new Exception("TODO");
            receiveHum(trame);
        }
        else if (typeTrame.equalsIgnoreCase("HME")) {
            nbBytesLus = in.read(trame, 0, 2);
            if (nbBytesLus != 2)
                throw new Exception("TODO");
            receiveHme(trame);
        }
        else if (typeTrame.equalsIgnoreCase("UPD")) {
            nbBytesLus = in.read(trame, 0, 1);
            if (nbBytesLus != 1)
                throw new Exception("TODO");
            int N = (int)trame[0] & 0xff;
            nbBytesLus = in.read(trame, 0, 5*N);
            if (nbBytesLus != 5*N)
                throw new Exception("TODO");
            receiveUpd(trame);
        }
        else if (typeTrame.equalsIgnoreCase("END")) {
            receiveEnd();
        }
        else if (typeTrame.equalsIgnoreCase("BYE")) {
            receiveBye();
        }
        else {
            throw new Exception ("BIG MOTHER FUCKING PROBLEM");
        }
    }

    //Méthode qui permet au serveur de communiquer la grille
    void receiveSet(byte[] bytes) {
        //TODO
    }
        
    //Méthode qui indique les maisons dans la grille
    void receiveHum(byte[] bytes) {
        //1 octet N
        //N*2 octets x,y,n(avec n le nb d'habitants de la maison)
    }
        
    //Méthode qui indique la case de départ
    void receiveHme(byte[] bytes) {
        //TODO
    }

    //Méthode qui indique les modifications à apporter à la grille
    void receiveUpd(byte[] bytes) {
        //TODO
    }
    
    //Méthode qui indique que la partie est terminée
    void receiveEnd() {
        //TODO
    }

    //Méthode qui indique que le serveur va couper la liaison
    void receiveBye() {
        //TODO
    }
    
    //Méthode qui indique le nom du jouer
    void sendNme() {
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
            System.out.println("TODO");
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
            System.out.println("TODO");
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
            System.out.println("TODO");
        }
        System.out.println("La trame ATK est envoyée au serveur");
        
    }
    
    // Méthode qui permet de fermer le socket client
    void closeAll() throws Exception {
        in.close();
        out.close();
        socket.close();
    }
/*
    // Méthode qui permet d'envoyer la trame (Initiate) au serveur
    void sendInitiate() throws Exception {
        trame[0] = (byte) 0xFF;
        trame[1] = 73;
        trame[2] = 2;
        trame[3] = 0x20;
        trame[4] = 7;
        trame[5] = 'P';
        trame[6] = 'C';
        trame[7] = '2';
        trame[8] = 'R';
        trame[9] = 'O';
        trame[10] = 'N';
        trame[11] = '?';
        trame[12] = 0x20;
        trame[13] = 10;
        trame[14] = 'P';
        trame[15] = 'C';
        trame[16] = '2';
        trame[17] = 'R';
        trame[18] = 'O';
        trame[19] = 'N';
        trame[20] = '2';
        trame[21] = '0';
        trame[22] = '1';
        trame[23] = '1';

        out.write(trame, 0, 24);

        System.out.println("La trame (Initiate) est envoyée au serveur.");
    }

    // Méthode qui permet de recevoir la trame (Ack) du serveur
    void receiveAck() throws Exception {
        in.read(trame, 0, 19);

        System.out.println("La trame (Ack) à été reçue du serveur.");
    }

    // Méthode qui permet de recevoir la trame (Registred) du serveur
    void receiveRegistred() throws Exception {
        in.read(trame, 0, 10);

        System.out.println("La trame (Registred) à été reçue du serveur.");

        this.joueurs.setId1((short) (trame[8] + trame[9]));
    }

    // Méthode qui permet de recevoir la trame (User) du serveur
    void receiveUser(int joueur) throws Exception {
        String nom = "";
        int i, j, k = 0;

        while (true) {
            trame[k] = (byte) in.read();

            if (k == 7) {
                for (i = 0; i < trame[7]; i++) {
                    trame[8 + i] = (byte) in.read();
                }

                k = k + trame[7] + 1;

                for (i = k; i < (k + 16); i++) {
                    trame[i] = (byte) in.read();
                }

                break;
            }

            k++;
        }

        for (i = 0; i < trame[7]; i++) {
            nom = nom + (char) (int) trame[8 + i];
        }

        j = i + 9;

        if (joueur == 1) {

            this.joueurs.setJoueur1(trame[5], nom, trame[j], trame[j + 2], trame[j + 4],
                    trame[j + 7], trame[j + 10], trame[j + 12], (byte) trame[j + 14]);

            this.receiveUser(2);

        } else {
            this.joueurs.setJoueur2(trame[5], nom, trame[j], trame[j + 2], trame[j + 4],
                    trame[j + 7], trame[j + 10], trame[j + 12], (byte) trame[j + 14]);

            System.out.println("La trame (User) à été reçue du serveur.\n");

            if (this.joueurs.getId1() == 1) {
                this.setLocation(500, 120);
            } else {
                this.setLocation(200, 120);
            }

            this.setVisible(true);
        }

        this.setVisible(true);
    }

    // Méthode qui permet de recevoir la trame (End) du serveur
    void receiveEnd() throws Exception {
        in.read(trame, 0, 1);
    }

    // Méthode qui permet de recevoir un message (trame PAUSE) du serveur
    void receiveMessage() throws Exception {
        String msg = "";
        int i, k = 0;

        while (true) {
            trame[k] = (byte) in.read();

            if (k == 4) {
                for (i = 0; i < trame[4]; i++) {
                    trame[5 + i] = (byte) in.read();
                }

                break;
            }

            k++;
        }

        for (i = 0; i < trame[4]; i++) {
            msg = msg + (char) trame[5 + i];
        }

        this.joueurs.setMessage("                  " + msg);
        this.joueurs.repaint();
        this.repaint();
    }
*/

/*
    // Méthode qui permet d'envoyer la trame (order) au serveur
    void sendOrder(String order) throws Exception {
        int i;
        trame[0] = (byte) 0xFF;
        trame[1] = 79;
        trame[2] = 1;
        trame[3] = 0x20;
        trame[4] = (byte) order.length();

        for (i = 0; i < order.length(); i++) {
            trame[5 + i] = (byte) order.charAt(i);
        }

        try {
            out.write(trame, 0, 12);
        } catch (java.net.SocketException e) {
        }
    }

    // Méthode qui permet d'envoyer la trame (Connect) au serveur
    void sendConnect() throws Exception {
        Scanner sc = new Scanner(System.in);
        String nom;
        short couleur;

        while (true) {
            System.out.print("\nDonnez votre nom (20 caractères au maximum) : ");
            nom = sc.next();
            if (nom.length() > 20) {
                continue;
            }
            break;
        }

        this.joueurs.setNom1(nom);

        while (true) {

            trame[0] = (byte) 0xFF;
            trame[1] = 0x43;
            trame[2] = 4;
            System.out.println("Donnez votre couleur RVB : ");

            System.out.print("R : ");
            trame[3] = 0x11;
            couleur = sc.nextShort();
            if (couleur < 0 || couleur > 255) {
                System.out.println("\nLa composante R doit être entre 0 et 255 !");
                continue;
            }
            trame[4] = (byte) couleur;
            this.joueurs.setR1(trame[4]);

            System.out.print("V : ");
            trame[5] = 0x11;
            couleur = sc.nextShort();
            if (couleur < 0 || couleur > 255) {
                System.out.println("\nLa composante V doit être entre 0 et 255 !");
                continue;
            }
            trame[6] = (byte) couleur;
            this.joueurs.setV1(trame[6]);

            System.out.print("B : ");
            trame[7] = 0x11;
            couleur = sc.nextShort();
            if (couleur < 0 || couleur > 255) {
                System.out.println("\nLa composante B doit être entre 0 et 255 !");
                continue;
            }
            trame[8] = (byte) couleur;
            this.joueurs.setB1(trame[8]);

            break;

        }

        trame[9] = 0x20;
        trame[10] = (byte) nom.length();
        for (int i = 0; i < trame[10]; i++) {
            trame[11 + i] = (byte) nom.charAt(i);
        }

        out.write(trame, 0, 31);

        System.out.println("\nLa trame (Connect) est envoyée au serveur.");
    }

    // Méthodes qui permettent de traiter l'appuie sur les touches du clavier
    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        this.setKeyPressed(true);

        try {
            if (e.getKeyCode() == 38 && this.joueurs.getDir1() != 1) {
                this.sendOrder("up");
            } else if (e.getKeyCode() == 37 && this.joueurs.getDir1() != 3) {
                this.sendOrder("left");
            } else if (e.getKeyCode() == 39 && this.joueurs.getDir1() != 4) {
                this.sendOrder("right");
            } else if (e.getKeyCode() == 40 && this.joueurs.getDir1() != 2) {
                this.sendOrder("down");
            } else if (e.getKeyCode() == 65) {
                this.sendOrder("abandon");
            } else {
                this.setKeyPressed(false);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    */
}
