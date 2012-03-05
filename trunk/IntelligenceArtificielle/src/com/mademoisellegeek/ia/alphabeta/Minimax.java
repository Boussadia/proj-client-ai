package com.mademoisellegeek.ia.alphabeta;

import com.mademoisellegeek.ia.data.Deplacement;
import com.mademoisellegeek.ia.data.Tour;
import java.util.LinkedList;

public abstract class Minimax implements Cloneable {

    public static final int UNLIMITED_SEARCH_DEPTH = -1;
    public static final int MINI_GAGNE = Integer.MAX_VALUE;
    public static final int MATCH_NUL = 0;
    public static final int MAX_GAGNE = Integer.MIN_VALUE;
    public static final int TOUR_DE_MAX = 1;
    public static final int TOUR_DE_MIN = -1;
    public static final int COMPTEUR = 9500; //on prévoit une demi-seconde supplémentaire
    public int player = Minimax.TOUR_DE_MAX; //TODO à supprimer, adopter la notation vampire !!

    public final int getPlayer() {
        return this.player;
    }

    public final void makePerfectMove() {
        int maxSearchDepth = Minimax.UNLIMITED_SEARCH_DEPTH;
        long systemTime = System.currentTimeMillis();
        if (maxSearchDepth == 0) {
            return;
        }
        LinkedList<Tour> tours = this.tousLesToursPossibles();
        if (tours.isEmpty()) {
            return;
        } else if (tours.size() == 1) {
            doMove(tours.get(0));
            return;
        }

        int bestScore = this.player == Minimax.TOUR_DE_MAX ? Minimax.MINI_GAGNE : Minimax.MAX_GAGNE;
        Tour bestMove = null;

        long diff = 0;
        int i = 0;
        int nbMoves = tours.size();
        while ((diff < 9500) && (i < nbMoves)) {
            Minimax tempBoard = (Minimax) this.clone();
            tempBoard.doMove(tours.get(i));
            int score = tempBoard.evaluate(maxSearchDepth == Minimax.UNLIMITED_SEARCH_DEPTH ? Minimax.UNLIMITED_SEARCH_DEPTH : maxSearchDepth - 1, new Resultat());
            if (score * player < bestScore || bestMove == null) {
                bestScore = score * player;
                bestMove = tours.get(i);
            }
            diff = System.currentTimeMillis() - systemTime;
            i++;
        }
        ((Deplacement) bestMove).printout();
        this.executerTour(bestMove);
        doMove(bestMove);
    }

    public final int evaluate(int maxSearchDepth, Resultat alphaBeta) {
        int currentScore = this.getCurrentScore();
        if (currentScore == Minimax.MINI_GAGNE || currentScore == Minimax.TOUR_DE_MAX) {
            return currentScore;
        }
        LinkedList<Tour> tours = this.tousLesToursPossibles();
        if (tours.isEmpty()) {
            return Minimax.MATCH_NUL;
        }
        int bestScore = 0;
        for (Tour tour : tours) {
            Minimax tempBoard = (Minimax) this.clone();
            tempBoard.doMove(tour);
            int score;
            if (maxSearchDepth == 0) {
                score = tempBoard.getCurrentScore();
            } else {
                score = tempBoard.evaluate(maxSearchDepth == Minimax.UNLIMITED_SEARCH_DEPTH ? Minimax.UNLIMITED_SEARCH_DEPTH : maxSearchDepth - 1, alphaBeta);

                // Alpha-beta pruning
                if (this.player != Minimax.TOUR_DE_MIN) {
                    if (score < alphaBeta.alpha) {
                        return score;
                    } else if (score < alphaBeta.beta) {
                        alphaBeta.beta = score;
                    }
                } else {
                    if (score > alphaBeta.beta) {
                        return score;
                    } else if (score > alphaBeta.alpha) {
                        alphaBeta.alpha = score;
                    }
                }
            }
            if (score == Minimax.MINI_GAGNE && player == -1) {
                return Minimax.MINI_GAGNE;
            } else if (score == Minimax.TOUR_DE_MAX && player == 1) {
                return Minimax.TOUR_DE_MAX;
            }
            if (score * player > bestScore) {
                bestScore = score * player;
            }
        }
        return bestScore;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (Exception e) {
            return null;
        }
    }

    public abstract int getCurrentScore();

    public abstract LinkedList tousLesToursPossibles();

    public abstract void moveAction(Tour tour);

    public abstract void executerTour(Tour tour);

    public final void doMove(Tour tour) {
        this.moveAction(tour);
        player *= -1;
    }
}