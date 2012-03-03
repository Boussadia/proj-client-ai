package com.mademoisellegeek.ia.alphabeta;

import com.mademoisellegeek.ia.alphabeta.AlphaBeta;
import com.mademoisellegeek.ia.data.Deplacement;
import com.mademoisellegeek.ia.data.Tour;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO CAMILLE multihread

public abstract class Minimax implements Cloneable
{
    public static final int UNLIMITED_SEARCH_DEPTH = -1;
    public static final int MINI_HAS_WON           = Integer.MAX_VALUE;
    public static final int STALE_MATE             = 0;
    public static final int MAX_HAS_WON            = Integer.MIN_VALUE;
    public static final int MAX_TURN               = 1;
    public static final int MIN_TURN               = -1;
    public static final int COMPTEUR               = 9*1000; //temps maximum pour prendre une décision

    private int player = Minimax.MAX_TURN; // Must always be 1 or -1

    public final int getPlayer()
    {
        return this.player;
    }

    public final void makePerfectMove()
    {
        
        int maxSearchDepth = 1;
        
        long systemTime = System.currentTimeMillis();
        
        if(maxSearchDepth == 0)
        {
            return;
        }
        
        LinkedList<Tour> moves = this.listAllLegalMoves();
        
        if(moves.isEmpty())
        {
            return;
        }
        
        else if(moves.size() == 1)
        {
            this.sendAction(moves.get(0));
            return;
        }
        
        int bestScore = this.player == Minimax.MAX_TURN ? Minimax.MINI_HAS_WON : Minimax.MAX_HAS_WON;
        Tour bestMove  = null;
        
        
        int i = 0;
        int nbMoves = moves.size();
        
       
            
        ///*
        long diff = 0;
        while((diff < 9500) && (i<nbMoves))
        {
            Minimax tempBoard = (Minimax)this.clone();
            tempBoard.doMove(moves.get(i));
            int score = tempBoard.evaluate(maxSearchDepth == Minimax.UNLIMITED_SEARCH_DEPTH ? Minimax.UNLIMITED_SEARCH_DEPTH : maxSearchDepth - 1, new AlphaBeta());
            if(score * player < bestScore || bestMove == null)
            {
                bestScore = score * player;
                bestMove  = moves.get(i);
            }
            diff = System.currentTimeMillis() - systemTime;
            i++;
        }
        ((Deplacement)bestMove).printout();
        this.sendAction(bestMove);
        //doMove(bestMove); //TODO VIRER PUISQUE QUE NOUVEAU SERVEUR LE FAIT
        /**/
        
        
        //NOUVELLE VERSION, IL FAUT VERIFIEr SI ÇA MARCHE!
        
        //*
         MoveThread thread = new MoveThread(this, moves, maxSearchDepth);
        try {
            Thread.sleep(COMPTEUR-System.currentTimeMillis()+systemTime);
        } catch (InterruptedException ex) {
            Logger.getLogger(Minimax.class.getName()).log(Level.SEVERE, null, ex);
        }
        thread.interrupt();
        /**/
    }
    
    public class MoveThread extends Thread{
        Minimax parent;
        LinkedList<Tour> moves;
        int maxSearchDepth;
        public MoveThread(Minimax parent, LinkedList<Tour> moves, int maxSearchDepth){
            this.parent = parent;
            this.moves = moves;
            this.maxSearchDepth = maxSearchDepth;
        }
            
        public void run(){
            int bestScore = parent.player == Minimax.MAX_TURN ? Minimax.MINI_HAS_WON : Minimax.MAX_HAS_WON;
            Tour bestMove  = null;
            int i = 0;
            int nbMoves = this.moves.size();
            while(i<nbMoves) {
                // Traitement
                    Minimax tempBoard = (Minimax)parent.clone();
                    tempBoard.doMove(moves.get(i));
                    int score = tempBoard.evaluate(maxSearchDepth == Minimax.UNLIMITED_SEARCH_DEPTH ? Minimax.UNLIMITED_SEARCH_DEPTH : maxSearchDepth - 1, new AlphaBeta());
                    if(score * player < bestScore || bestMove == null)
                    {
                        bestScore = score * player;
                        bestMove  = moves.get(i);
                    }
                    i++;

                ((Deplacement)bestMove).printout();
                parent.sendAction(bestMove);
                
                try {
                    Thread.sleep(1); // Pause de 100 secondes
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt(); // Très important de réinterrompre
                    break; // Sortie de la boucle infinie
                }
            }
        }
        
    
    }

    public final int evaluate(int maxSearchDepth, AlphaBeta alphaBeta)
    {
        int currentScore = this.getCurrentScore();
        if(currentScore == Minimax.MINI_HAS_WON || currentScore == Minimax.MAX_HAS_WON)
        {
            return currentScore;
        }
        LinkedList<Tour> moves = this.listAllLegalMoves();
        if(moves.isEmpty())
        {
            return Minimax.STALE_MATE;
        }
        int bestScore = 0;
        for(Tour move : moves)
        {
            Minimax tempBoard = (Minimax)this.clone();
            tempBoard.doMove(move);
            int score;
            if(maxSearchDepth == 0)
            {
                score = tempBoard.getCurrentScore();
            }
            else
            {
                score = tempBoard.evaluate(maxSearchDepth == Minimax.UNLIMITED_SEARCH_DEPTH ? Minimax.UNLIMITED_SEARCH_DEPTH : maxSearchDepth - 1, alphaBeta);

                // Alpha-beta pruning
                if(this.player != Minimax.MIN_TURN)
                {
                    if(score < alphaBeta.alpha)
                    {
                        return score;
                    }
                    else if(score < alphaBeta.beta)
                    {
                        alphaBeta.beta = score;
                    }
                }
                else
                {
                    if(score > alphaBeta.beta)
                    {
                        return score;
                    }
                    else if(score > alphaBeta.alpha)
                    {
                        alphaBeta.alpha = score;
                    }
                }
            }
            if(score == Minimax.MINI_HAS_WON && player == -1)
            {
                return Minimax.MINI_HAS_WON;
            }
            else if(score == Minimax.MAX_HAS_WON && player == 1)
            {
                return Minimax.MAX_HAS_WON;
            }
            if(score * player > bestScore)
            {
                bestScore = score * player;
            }
        }
        return bestScore;
    }

    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public abstract int getCurrentScore();
    public abstract LinkedList listAllLegalMoves();
    public abstract void moveAction(Tour tour);
    public abstract void sendAction(Tour tour);
    public final void doMove(Tour tour)
    {
        this.moveAction(tour);
        player *= -1;
    }
}