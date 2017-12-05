package classes;

import java.io.Serializable;
import java.util.Map;
import java.util.Hashtable;


/**
 * Created by root on 11/11/16.
 */
public class Partie implements Serializable
{

    private Joueur player1;

    private Reserve reservePlayer1;

    private Joueur player2;

    private Reserve reservePlayer2;

    private Map<String, Joueur> observers = new Hashtable<>();

    private Plateau board;

    private boolean isPrivate;

    private String state;

    private int round;

    public Partie(Joueur player, boolean isPrivate)
    {
        player.setColor("steelblue");
        player1 = player;
        player2 = null;
        reservePlayer1 = new Reserve("steelblue");
        board = new Plateau();
        this.isPrivate = isPrivate;
        state = "En attente";
        round = 0;
    }

    /*
     * Méthode appelée lorsqu'un second joueur rejoint la partie, qui va donc pouvoir commencer
     */
    public void addSecondPlayer(Joueur player)
    {
        player.setColor("indianred");
        player2 = player;
        reservePlayer2 = new Reserve("indianred");
        state = "Phase 1";
    }

    /*
     * Méthode pour ajouter un observateur à la partie
     */
    public void addObserver(Joueur player)
    {
        observers.put(player.getLogin(), player);
    }

    /*
     * Méthode pour supprimer un observateur de la partie
     */
    public void removeObserver(Joueur player)
    {
        observers.remove(player.getLogin());
    }

    /*
     * Méthode pour savoir si un joueur observe la partie ou la joue
     */
    public boolean isObserver(Joueur player)
    {
       return observers.containsKey(player.getLogin());
    }

    /*
     * Méthode permettant à un joueur de quitter la partie
     */
    public void leave(Joueur player)
    {
        if (!state.equals("En attente"))
        {
            // Si le joueur est un observateur la partie continue normalement
            if(isObserver(player))
                removeObserver(player);
            else
            {
                // Si c'est le joueur 1 qui quitte la partie alors qu'elle n'est pas terminée, c'est le joueur 2 qui l'emporte
                if (player == player1)
                    player1 = null;
                // Si en revanche c'est le joueur 2 qui la quitte, le joueur 1 la gagne
                else if (player == player2)
                    player2 = null;

                // Dans le cas ou il reste encore un joueur après que le second soit partie, la partie est déclarée comme étant terminée
                // Pas besoin de la déclarer terminée s'il ne restait qu'un joueur avant son départ, car elle l'est déjà
                if(!state.equals("Terminée"))
                    state = "Terminée";
                player.setColor("");
            }
        }
    }

    /*
     * Méthode pour savoir si un joueurs a placé toutes les pièces de sa réserve sur le plateau
     */
    public boolean readyForNextPhase(String login)
    {
        if(login.equals(player1.getLogin()))
            return reservePlayer1.isEmpty();
        else
            return reservePlayer2.isEmpty();
    }

    /*
     * Méthode pour mettre fin à la phase d'initialisation si elle est terminée, c'est à dire si les 2 joueurs ont placé toutes les pièces de leur réserve sur le plateau
     */
    public void endInitializationPhase()
    {
        if(reservePlayer2 != null) {
            if (reservePlayer1.isEmpty() && reservePlayer2.isEmpty()) {
                if (round == 0)
                    round++;
                state = "Phase 2";
            }
        }
    }

    /*
     * Méthode pour savoir si c'est le tour du joueur donné en paramètre
     */
    public boolean canMovePiece(String login)
    {
        if(login.equals(player1.getLogin()))
            return round % 2 == 0;
        else
            return round % 2 != 0;
    }

    /*
     * Méthode pour incrémenter le tour
     */
    public void incrementRound()
    {
        round += 1;
    }

    /*
     * Méthode pour récupérer le plateau du joueur pour qu'il soit affiché face à lui
     */
    public Case[][] getTab(String login)
    {
        if (player1 != null && login.equals(player1.getLogin()))
        {
            int LENGTH = 10;//board.getLENGTH();
            Case[][] aux = new Case[LENGTH][LENGTH];
            for(int i = 0; i < LENGTH; i++)
            {
                for(int j = 0; j < LENGTH; j++)
                {
                    aux[i][j] = new Case(LENGTH -1 - i, j);
                    if(!board.getBox(LENGTH -1 - i, j).isFree() && !board.getBox(LENGTH -1 - i, j).isLake())
                    {
                        aux[i][j].setPiece(board.getBox(LENGTH -1 - i, j).getPiece());
                        aux[i][j].setFree(false);
                    }
                }
            }
            return aux;
        }
        else
            return board.getTab();
    }

    public Joueur getPlayer1()
    {
        return player1;
    }

    public Joueur getPlayer2()
    {
        return player2;
    }

    public Joueur getOtherPlayer(String login)
    {
        if(player1 == null || player2 == null)
            return null;
        else if (login.equals(player1.getLogin()))
            return player2;
        else
            return player1;
    }

    public Reserve getReservePlayer(String login)
    {
        if (login.equals(player1.getLogin()))
            return reservePlayer1;
        else
            return reservePlayer2;
    }

    public Map<String, Joueur> getObservers()
    {
        return observers;
    }

    public Plateau getBoard()
    {
        return board;
    }

    public boolean getIsPrivate()
    {
        return isPrivate;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public int getRound()
    {
        return round;
    }

}
