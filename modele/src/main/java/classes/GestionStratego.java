package classes;

import classes.exceptions.*;

import java.util.Map;
import java.util.Hashtable;

/**
 * Created by root on 11/11/16.
 */
public class GestionStratego implements IGestionStratego
{

    // Listes des joueurs inscrits
    private Map<String, Joueur> playersRegistered = new Hashtable<>();

    // Liste des joueurs connectés
    private Map <String, Joueur> playersOnline = new Hashtable<>();

    // Liste des joueurs en pleine partie
    private Map <String, Partie> playersInGame = new Hashtable<>();

    // Liste des parties
    private Map <String, Partie> uniqueGames = new Hashtable<>();

    private static IGestionStratego instance = null;

    public static IGestionStratego getInstance() {
        if (instance == null) {
            instance = new GestionStratego();
        }
        return instance;

    }
    /*
     * Méthode pour s'inscrire
     */
    public void registration(String login, String password, String passwordConfirmation) throws LoginTooShortException, PasswordConfirmationException, PasswordTooShortException, LoginAlreadyTakenException
    {
        if(login.length() < 4)
            throw new LoginTooShortException();
        else
        {
            if(password.equals(passwordConfirmation)) 
            {
                if (password.length() < 4)
                    throw new PasswordTooShortException();
                else 
                {
                    if (playersRegistered.containsKey(login))
                        throw new LoginAlreadyTakenException();
                    else 
                    {
                        Joueur player = new Joueur(login, password);
                        playersRegistered.put(login, player);
                    }
                }
            }
            else
                throw new PasswordConfirmationException();
        }
    }

    /*
     * Méthode pour se connecter
     */
    public void connection(String login, String password) throws UnknownPlayerException, PlayerAlreadyConnectedException
    {
        for(Joueur player: playersRegistered.values())
        {
            if(login.equals(player.getLogin()) && password.equals(player.getPassword()))
            {
                if(playersOnline.containsKey(login))
                    throw new PlayerAlreadyConnectedException();
                else
                {
                    playersOnline.put(login, player);
                    return;
                }
            }
        }
        throw new UnknownPlayerException();
    }

    /*
     * Méthode pour se déconnecter
     */
    public void disconnection(String login)
    {
        Joueur player = getPlayersRegistered().get(login);
        if(player != null)
            player.deleteAllInvitations();
        if (getMyGame(login) != null)
            leaveGame(login);
        playersOnline.remove(login);
        playersInGame.remove(login);
    }

    /*
     * Méthode pour créer une partie privée (accessible sur invitation seulement) ou publique (accessible par n'importe quel autre joueur connecté)
     */
    public void createNewGame(String login, boolean isPrivate)
    {
        Joueur player = playersOnline.get(login);
        Partie game = new Partie(player, isPrivate);
        uniqueGames.put(login, game);
        playersInGame.put(login, game);
    }

    /*
     * Méthode pour inviter un joueur à rejoindre sa partie privée
     * Il Faut l'avoir déjà crée pour y inviter quelqu'un
     */
    public void sendInvitation(Partie game, String login, String guestPlayerLogin) throws PlayerAlreadyInGameException
    {
        boolean inGame = playersInGame.containsKey(guestPlayerLogin);
        if (!inGame)
            playersOnline.get(guestPlayerLogin).addInvitation(login, game);
        else
            throw new PlayerAlreadyInGameException();
    }

    /*
     * Méthode pour accepter une invitation et rejoindre la partie privée concernée
     */
    public void acceptInvitation(String login, String guestPlayerLogin) throws GameNotFoundException, GameAlreadyBegunException
    {
        try {
            joinPrivateGame(getMyGame(guestPlayerLogin), login);
        } catch(GameNotFoundException e)
        {
            throw e;
        } catch(GameAlreadyBegunException e)
        {
            throw e;
        }
    }

    /*
     * Méthode pour décliner une invitation
     */
    public void declineInvitation(String login, String guestPlayerLogin)
    {
        Joueur player = playersOnline.get(login);
        player.declineInvitation(guestPlayerLogin);
    }

    /*
     * Méthode pour rejoindre une partie existante
     */
    public void joinGame(Partie game, String login) throws GameNotFoundException, GameOverException
    {
        if(game == null)
            throw new GameNotFoundException();
        else if (game.getState().equals("Terminée"))
            throw new GameOverException();
        else
        {
            Joueur player = playersOnline.get(login);
            // Si la partie possède déjà 2 joueurs, le joueur la rejoint en tant qu'observateur
            // On ne rentrera jamais dans cette boucle si la partie est privée, car les observateurs n'y sont pas toléré
            if (!game.getState().equals("En attente"))
                game.addObserver(player);
            else
                game.addSecondPlayer(player);
            playersInGame.put(login, game);
            playersInGame.put(game.getPlayer1().getLogin(), game);
            player.deleteAllInvitations();
        }
    }

    /*
    * Méthode pour rejoindre une partie privée existante
    */
    public void joinPrivateGame (Partie game, String login) throws GameNotFoundException, GameAlreadyBegunException
    {
        if (!game.getState().equals("En attente"))
            throw new GameAlreadyBegunException();
        else
        {
            try {
                joinGame(game, login);
            } catch(GameNotFoundException e)
            {
                throw e;
            // Cas qui n'arrivera jamais
            } catch(GameOverException e)
            {
            }
        }
    }

    /*
     * Méthode pour quitter une partie en cours
     */
    public void leaveGame(String login)
    {
        Partie game = getMyGame(login);
        Joueur player = playersOnline.get(login);
        game.leave(player);
        playersInGame.remove(login);
        // Si c'est le joueur 2 qui a quitté la partie
        if(game.getPlayer1() != null)
            uniqueGames.remove(game.getPlayer1().getLogin());
        // Si c'est le joueur 1
        else if(game.getPlayer2() != null)
            uniqueGames.remove(login);
        player.deleteAllInvitations();
    }

    /*
     * Méthode pour savoir si un joueur observe la partie donnée en paramètre
     */
    public boolean isObserver(String login, Partie game)
    {
        Joueur player = playersOnline.get(login);
        return game.isObserver(player);
    }

    /*
     * Méthode pour terminer le tour et lancer le suivant
     */
    public void incrementRound(String login)
    {
        Partie game = getMyGame(login);
        game.incrementRound();
    }

    /*
     * Méthode pour mettre une pièce se trouvant dans sa réserve dans une case lors de la phase d'initialisation
     */
    public boolean putPiece(String login, TypePiece type, int line, int column) throws PieceAlreadyPlacedException, MoveImpossibleException
    {
        Partie game = playersInGame.get(login);
        if(game.isObserver(playersOnline.get(login)) || game.getState().equals("En attente"))
            throw new MoveImpossibleException();
        else
        {
            Reserve reserve = game.getReservePlayer(login);
            try {
                // Vérification que la pièce que le joueur veut déplacer est bien dans la réserve
                Piece piece = reserve.getPiece(type);
                Plateau board = getMyBoard(game);
                boolean moveDone;
                // Si c'est le joueur 1 qui place une de ses pièces on met à jour la ligne donnée en paramètre
                if (login.equals(game.getPlayer1().getLogin()))
                    moveDone = board.putPiece(board.getBox(9 - line, column), piece);
                else
                    moveDone = board.putPiece(board.getBox(line, column), piece);
                // Vérification que le joueur ait le droit de mettre la pièce dans la case en question
                // Si ce n'est pas le cas le mouvement n'est pas effectué et une exception est levée
                if (!moveDone)
                    throw new MoveImpossibleException();
                else
                    reserve.removePiece(type);
            } catch (PieceAlreadyPlacedException e) {
                throw e;
            }
            // Mise à jour de l'état de la partie si la phase d'initialisation est terminée
            game.endInitializationPhase();

            // Retourne le booléen permettant de savoir si le joueur est prêt pour la phase suivante
            return game.readyForNextPhase(login);
        }
    }

    /*
     * Méthode pour déplacer une pièce dans une case
     */
    public boolean movePiece(String login, int line, int column, int lineBis, int columnBis) throws MoveImpossibleException
    {
        Partie game = getMyGame(login);
        Joueur player = playersOnline.get(login);
        if(game.isObserver(player))
            throw new MoveImpossibleException();
        else
        {
            if (game.canMovePiece(login))
            {
                Plateau board = game.getBoard();

                // Vérification que le joueur adverse ait encore des pièces pouvant se déplacer
                boolean endGame = board.victory(game.getOtherPlayer(player.getLogin()));
                if(endGame)
                {
                    game.setState("Terminée");
                    return true;
                }

                if (login.equals(game.getPlayer1().getLogin())) {
                    line = 9 - line;
                    lineBis = 9 - lineBis;
                }

                // Vérification que la case de départ n'est pas vide et n'est pas un lac, donc vérification qu'elle contient bien une pièce
                Case box = board.getBox(line, column);
                Piece piece;
                if (box.isLake() || box.isFree())
                    throw new MoveImpossibleException();
                else
                    piece = box.getPiece();

                // Vérification de la validité du mouvement souhaité
                if (!board.moveIsPossible(player, piece, lineBis, columnBis, game))
                    throw new MoveImpossibleException();
                else
                {
                    box = board.getBox(lineBis, columnBis);

                    // Incrémentation du tour
                    game.incrementRound();

                    // Booléen indiquant si la partie est terminée ou non
                    endGame = board.movePiece(box, piece, player, game);

                    if(endGame)
                        game.setState("Terminée");

                    return endGame;
                }
            } else
                throw new MoveImpossibleException();
        }
    }

    public Partie getMyGame(String login) {return playersInGame.get(login);}

    public Plateau getMyBoard(Partie game) {return game.getBoard();}

    public Reserve getMyStock(String login)
    {
        return getMyGame(login).getReservePlayer(login);
    }

    public Map<String, Partie> getMyInvitations(String login) { Joueur player = playersOnline.get(login);return player.getInvitations();}

    public Map<String, Joueur> getPlayersRegistered() {
        return playersRegistered;
    }

    public Map<String, Joueur> getPlayersOnline() {return playersOnline;}

    public Map<String, Partie> getPlayersInGame()
    {
        return playersInGame;
    }

    public Map<String, Partie> getUniqueGames() {return uniqueGames;}

}
