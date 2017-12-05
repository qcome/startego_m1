package classes;

import classes.exceptions.*;

import java.util.Map;

/**
 * Created by root on 11/12/16.
 */
public interface IGestionStratego
{

    void registration(String login, String password, String passwordConfirmation) throws LoginTooShortException, PasswordConfirmationException, PasswordTooShortException, LoginAlreadyTakenException;

    void connection(String login, String password) throws UnknownPlayerException, PlayerAlreadyConnectedException;

    void disconnection(String login);

    void createNewGame(String login, boolean isPrivate);

    void sendInvitation(Partie game, String login, String guestPlayerLogin) throws PlayerAlreadyInGameException;

    void acceptInvitation(String login, String guestPlayerLogin) throws GameNotFoundException, GameAlreadyBegunException;

    void declineInvitation(String login, String guestPlayerLogin);

    void joinGame(Partie game, String login) throws GameNotFoundException, GameOverException;

    void joinPrivateGame(Partie game, String login)  throws GameNotFoundException, GameAlreadyBegunException;

    void leaveGame(String login);

    boolean isObserver(String login, Partie game);

    void incrementRound(String login);

    boolean putPiece(String login, TypePiece type, int line, int column) throws PieceAlreadyPlacedException, MoveImpossibleException;

    boolean movePiece(String login, int line, int column, int lineBis, int columnBis) throws MoveImpossibleException;

    Partie getMyGame(String login);

    Plateau getMyBoard(Partie game);

    Reserve getMyStock(String login);

    Map<String, Partie> getMyInvitations(String login);

    Map<String, Joueur> getPlayersOnline();

    Map<String, Partie> getPlayersInGame();

    Map<String, Partie> getUniqueGames();

}
