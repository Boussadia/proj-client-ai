import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JFrame;

// Définition de la classe client

public class Client extends JFrame implements KeyListener
{
	private static final long serialVersionUID = 1L;
	private static final String host = "127.0.0.1";
	private static final int port = 5555;
	private Socket socket;
	private byte[] trame;
	private InputStream in;
	private OutputStream out;
	private boolean keyPressed = false;
	private boolean isFin = false;
	private boolean isOK = false;
	Joueurs joueurs;
	
	// Méthode principale
	
	public static void main(String[] args) throws Exception
	{
		int i;
		
		// Création d'une instance d'un client
		
		Client client = new Client();
		
		// Invocation des méthodes qui communiquent avec le serveur
		
		client.sendInitiate();
		client.receiveAck();
		client.sendConnect();
		client.receiveRegistred();
		client.receiveUser(1);
		client.receiveEnd();
		
		for(i=0; i<4; i++)
			client.receiveMessage(); // Les messages de décompte
		
		// On boucle à l'infinie jusqu'à la fin de la partie 
		
		while(true)
		{
			client.setKeyPressed(false);
			
			client.receiveTrame();
			
			if(!client.isOK)
				
				client.addKeyListener();
			
			if(client.isFin())
				break;
			
			if(client.isKeyPressed())
				continue;
			
			client.sendOrder("idle");			
		}
		
		// Fermer le socket client
		
		client.closeAll();
	}
	
	// Constructeur de la classe client
	
	public Client() throws Exception
	{
		System.out.println("\nConnexion à "+host+" sur le port : "+port);
		
		// Création du socket client
		
		socket = new Socket(host, port);
		
		System.out.println("Connexion établie avec le serveur.\n");
		
		// Les données en entrée et en sortie
		
		in = socket.getInputStream();

	    out = socket.getOutputStream();
	    
	    // Création d'une instance de Joueurs
	    
	    joueurs = new Joueurs();
	    this.setTitle("Arène");
        this.setSize(290, 540);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setAlwaysOnTop(true);
	    this.setContentPane(joueurs);
	    	    
	    // Définition de la trame
	    
	    trame = new byte[250];
	    
	    /* La taille de la trame ci-dessus est une taille maximale et n'a 
	     * aucune signification car au moment ou on envoie ou on reçoit des 
	     * données, c'est là ou on définie la taille effective de la trame. */		
	}
	
	// Méthode qui permet d'envoyer la trame (Initiate) au serveur
	
	void sendInitiate() throws Exception
	{
		trame[0] = (byte)0xFF; trame[1] = 73; trame[2] = 2;
	    trame[3] = 0x20; trame[4] = 7; trame[5] = 'P'; trame[6] = 'C';
	    trame[7] = '2'; trame[8] = 'R'; trame[9] = 'O'; trame[10] = 'N';
	    trame[11] = '?'; trame[12] = 0x20; trame[13] = 10; trame[14] = 'P'; 
	    trame[15] = 'C'; trame[16] = '2'; trame[17] = 'R'; 
	    trame[18] = 'O'; trame[19] = 'N'; trame[20] = '2'; trame[21] = '0';
	    trame[22] = '1'; trame[23] = '1';

	    out.write(trame, 0, 24);
	    
	    System.out.println("La trame (Initiate) est envoyée au serveur.");
	}
	
	// Méthode qui permet de recevoir la trame (Ack) du serveur
	
	void receiveAck() throws Exception
	{
		in.read(trame, 0, 19);
	    
	    System.out.println("La trame (Ack) à été reçue du serveur.");	    
	}
	
	// Méthode qui permet d'activer les touches du clavier
	
	void addKeyListener()
	{
		this.addKeyListener(this);
		this.setOK(true);
	}
	
	// Méthode qui permet de recevoir la trame (Registred) du serveur
	
	void receiveRegistred() throws Exception
	{
		in.read(trame, 0, 10);
	    
	    System.out.println("La trame (Registred) à été reçue du serveur.");
	
	    this.joueurs.setId1((short) (trame[8]+trame[9]));
	}
	
	// Méthode qui permet de recevoir la trame (User) du serveur
	
	void receiveUser(int joueur) throws Exception
	{
		String nom = ""; int i, j, k = 0;
		
		while(true)
		{
			trame[k] = (byte) in.read();
			
			if(k == 7)
			{
				for(i=0; i<trame[7]; i++)
					trame[8+i] = (byte)in.read();
				
				k = k + trame[7] + 1;
				
				for(i=k; i<(k+16); i++)
					trame[i] = (byte)in.read();
				
				break;
			}
			
			k++;			
		}
						
		for(i=0; i<trame[7]; i++)
	    	nom = nom + (char)(int)trame[8+i];
		
	    j = i+9;
	    
	    if(joueur == 1)
	    {
	    	    
	    this.joueurs.setJoueur1
	    (trame[5], nom, trame[j], trame[j+2], trame[j+4],
	     trame[j+7], trame[j+10], trame[j+12], (byte) trame[j+14]);
	    
	    this.receiveUser(2);
	    
	    }
	    
	    else
	    {
	    	this.joueurs.setJoueur2
		    (trame[5], nom, trame[j], trame[j+2], trame[j+4],
		     trame[j+7], trame[j+10], trame[j+12], (byte) trame[j+14]);	 
	    	
	    	System.out.println("La trame (User) à été reçue du serveur.\n");
	    	
	    	if(this.joueurs.getId1() == 1)
				
				this.setLocation(500, 120);
			else
				
				this.setLocation(200, 120);
			
			this.setVisible(true);	    	 
	    }

	    this.setVisible(true);
	}
	
	// Méthode qui permet de recevoir la trame (End) du serveur
	
	void receiveEnd() throws Exception
	{
		in.read(trame, 0, 1);
	}
	
	// Méthode qui permet de recevoir un message (trame PAUSE) du serveur
	
	void receiveMessage() throws Exception
	{
		String msg = ""; int i, k = 0;
		
		while(true)
		{
			trame[k] = (byte) in.read();
			
			if(k == 4)
			{
				for(i=0; i<trame[4]; i++)
					trame[5+i] = (byte)in.read();
				
				break;
			}
			
			k++;			
		}
				
		for(i=0; i<trame[4]; i++)
			msg = msg + (char)trame[5+i];
		
		this.joueurs.setMessage("                  "+msg);
		this.joueurs.repaint();
		this.repaint();
	}
	
	// Méthode qui permet de recevoir une trame (Turn dans le cas nominal)
	
	void receiveTrame() throws Exception
	{
		int i; String time ="Temps écoulé : ";
		
		in.read(trame, 0, 43);
		
		switch(trame[1])
		{
			case 0x44:
			{
				this.joueurs.setMessage("Vous avez perdu la partie :(");					
				this.joueurs.repaint();
				this.repaint();
				this.setFin(true);
				return;
			}
		
			case 0x57:
			{
				this.joueurs.setMessage("Vous avez gagné la partie !");	
				this.joueurs.repaint();
				this.repaint();
				this.setFin(true);
				return;
			}
			
			default : break;
		}
		
		this.joueurs.addX1(trame[24]);
		this.joueurs.addY1(trame[27]);			
	    this.joueurs.setDir1(trame[29]);  	    
	    	    
	    int x2size = this.joueurs.getX2Size();
	    int y2size = this.joueurs.getY2Size();
	    
	    if( (x2size > 1) && (y2size > 1) )
	    {
	    	this.joueurs.addX2((x2size-1), trame[41]);
	    	this.joueurs.addY2((y2size-1), trame[42]);
	    }
	    
		this.joueurs.addX2(trame[35]);
		this.joueurs.addY2(trame[38]);			
		this.joueurs.setDir2(trame[40]);
	    	    
	    for(i=0; i<trame[4] && (trame[5+i] != '.'); i++)
	   
	    	time += (char)trame[5+i];
	    
	    time += " cs";
	    	    
	    this.joueurs.setMessage(time);
	    joueurs.repaint();
	    this.repaint();
	}
	
	// Méthode qui permet d'envoyer la trame (order) au serveur
	
	void sendOrder(String order) throws Exception
	{
		int i;
		trame[0] = (byte)0xFF; trame[1] = 79; trame[2] = 1;
	    trame[3] = 0x20; trame[4] = (byte) order.length(); 
	    
	    for(i=0; i<order.length(); i++)
	    	trame[5+i] = (byte) order.charAt(i);

	    try
		{		
			out.write(trame, 0, 12);
		}
		
		catch(java.net.SocketException e){}		
	}
	
	// Méthode qui permet d'envoyer la trame (Connect) au serveur
	
	void sendConnect() throws Exception
	{
		Scanner sc = new Scanner(System.in);
		String nom; short couleur;
		
		while(true)
		{
		System.out.print("\nDonnez votre nom (20 caractères au maximum) : ");
		nom = sc.next();
		if(nom.length() > 20)
			continue;
		break;
		}
		
		this.joueurs.setNom1(nom);
		
		while(true)
		{
			
		trame[0] = (byte)0xFF; trame[1] = 0x43; trame[2] = 4;
		System.out.println("Donnez votre couleur RVB : ");
		
		System.out.print("R : ");
		trame[3] = 0x11;
		couleur = sc.nextShort();
		if(couleur < 0 || couleur > 255)
		{
			System.out.println("\nLa composante R doit être entre 0 et 255 !");
			continue;
		}		
		trame[4] = (byte) couleur;
		this.joueurs.setR1(trame[4]);
				
		System.out.print("V : ");
		trame[5] = 0x11;
		couleur = sc.nextShort();
		if(couleur < 0 || couleur > 255)
		{
			System.out.println("\nLa composante V doit être entre 0 et 255 !");
			continue;
		}	
		trame[6] = (byte) couleur;
		this.joueurs.setV1(trame[6]);
				
		System.out.print("B : ");
		trame[7] = 0x11;
		couleur = sc.nextShort();
		if(couleur < 0 || couleur > 255)
		{
			System.out.println("\nLa composante B doit être entre 0 et 255 !");
			continue;
		}	
		trame[8] = (byte) couleur;
		this.joueurs.setB1(trame[8]);
				
		break;
		
		}
		
		trame[9] = 0x20; trame[10] = (byte)nom.length();
		for(int i=0; i<trame[10]; i++)
			trame[11+i] = (byte) nom.charAt(i);
		
		out.write(trame, 0, 31);
	    
	    System.out.println("\nLa trame (Connect) est envoyée au serveur.");
	}
	
	// Méthode qui permet de fermer le socket client
	
	void closeAll() throws Exception
	{
		in.close();
        out.close();
        socket.close();
	}
	
	// Méthodes qui permettent de traiter l'appuie sur les touches du clavier
	
	public void keyTyped(KeyEvent e){}
	public void keyReleased(KeyEvent e){}

	public void keyPressed(KeyEvent e) 
	{
		this.setKeyPressed(true);
		
		try
		{
			if(e.getKeyCode() == 38 && this.joueurs.getDir1() != 1)
				this.sendOrder("up");
			else
			if(e.getKeyCode() == 37 && this.joueurs.getDir1() != 3)
				this.sendOrder("left");
			else
			if(e.getKeyCode() == 39 && this.joueurs.getDir1() != 4)
				this.sendOrder("right");
			else
			if(e.getKeyCode() == 40 && this.joueurs.getDir1() != 2)
				this.sendOrder("down");
			else
			if(e.getKeyCode() == 65)
				this.sendOrder("abandon");
			else
				this.setKeyPressed(false);			
		} 
		
		catch (Exception e1) 
		{
			e1.printStackTrace();
		}
	}
	
	// Les getters et les setters
	
	public boolean isKeyPressed()
	{
		return keyPressed;
	}	
	public void setKeyPressed(boolean b)
	{
		this.keyPressed = b;
	}
	public boolean isFin() 
	{
		return isFin;
	}
	public void setFin(boolean isFin) 
	{
		this.isFin = isFin;
	}
	public boolean isOK() 
	{
		return isOK;
	}
	public void setOK(boolean isOK) 
	{
		this.isOK = isOK;
	}
}

//Fin du programme.
