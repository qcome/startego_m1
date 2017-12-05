package rmiService;

import classes.*;
import classes.exceptions.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Created by Quentin on 10/01/2017.
 */
public interface MonService extends Remote
{
    public final String serviceName = "StrategoDAO";

    void registration(String login, String password, String passwordConfirmation) throws RemoteException, LoginTooShortException, PasswordConfirmationException, PasswordTooShortException, LoginAlreadyTakenException;

    void connection(String login, String password) throws RemoteException, UnknownPlayerException, PlayerAlreadyConnectedException;

    void disconnection(String login) throws RemoteException;

    void incrementRound(String login) throws RemoteException;

    void createNewGame(String login, boolean isPrivate) throws RemoteException;

    void sendInvitation(Partie game, String login, String guestPlayerLogin) throws RemoteException, PlayerAlreadyInGameException;

    void acceptInvitation(String login, String guestPlayerLogin) throws RemoteException, GameAlreadyBegunException, GameNotFoundException;

    void declineInvitation(String login, String guestPlayerLogin) throws RemoteException;

    void joinGame(Partie game, String login) throws RemoteException, GameNotFoundException, GameOverException;

    void joinPrivateGame(Partie game, String login)  throws RemoteException, GameAlreadyBegunException, GameNotFoundException;

    void leaveGame(String login) throws RemoteException;

    boolean isObserver(String login, Partie game) throws RemoteException;

    boolean putPiece(String login, TypePiece type, int line, int column) throws RemoteException, PieceAlreadyPlacedException, MoveImpossibleException;

    boolean movePiece(String login, int line, int column, int lineBis, int columnBis) throws RemoteException, MoveImpossibleException;

    Partie getMyGame(String login) throws RemoteException;

    Plateau getMyBoard(Partie game) throws RemoteException;

    Reserve getMyStock(String login) throws RemoteException;

    Map<String, Partie> getMyInvitations(String login) throws RemoteException;

    Map<String, Joueur> getPlayersOnline() throws RemoteException;

    Map<String, Partie> getPlayersInGame() throws RemoteException;

    Map<String, Partie> getUniqueGames() throws RemoteException;
}
