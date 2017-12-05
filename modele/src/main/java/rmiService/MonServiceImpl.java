package rmiService;

import classes.*;
import classes.exceptions.*;

import java.rmi.RemoteException;
import java.util.Map;

/**
 * Created by Quentin on 10/01/2017.
 */
public class MonServiceImpl implements MonService {
    private IGestionStratego maDAO = GestionStratego.getInstance();

    @Override
    public void registration(String login, String password, String passwordConfirmation) throws RemoteException, LoginTooShortException, PasswordConfirmationException, PasswordTooShortException, LoginAlreadyTakenException {
        this.maDAO.registration(login, password, passwordConfirmation);
    }

    @Override
    public void connection(String login, String password) throws RemoteException, UnknownPlayerException, PlayerAlreadyConnectedException {
        this.maDAO.connection(login, password);
    }

    @Override
    public void disconnection(String login) throws RemoteException {
        this.maDAO.disconnection(login);
    }

    @Override
    public void createNewGame(String login, boolean isPrivate) throws RemoteException {
        this.maDAO.createNewGame(login, isPrivate);
    }

    @Override
    public void sendInvitation(Partie game, String login, String guestPlayerLogin) throws RemoteException, PlayerAlreadyInGameException {
        this.maDAO.sendInvitation(game, login, guestPlayerLogin);
    }

    @Override
    public void acceptInvitation(String login, String guestPlayerLogin) throws RemoteException, GameAlreadyBegunException, GameNotFoundException {
        this.maDAO.acceptInvitation(login, guestPlayerLogin);
    }

    @Override
    public void declineInvitation(String login, String guestPlayerLogin) throws RemoteException {
        this.maDAO.declineInvitation(login, guestPlayerLogin);
    }

    @Override
    public void joinGame(Partie game, String login) throws RemoteException, GameNotFoundException, GameOverException {
        this.maDAO.joinGame(game, login);
    }

    @Override
    public void joinPrivateGame(Partie game, String login) throws RemoteException, GameAlreadyBegunException, GameNotFoundException {
        this.maDAO.joinPrivateGame(game, login);
    }

    @Override
    public void leaveGame(String login) throws RemoteException {
        this.maDAO.leaveGame(login);
    }

    @Override
    public boolean isObserver(String login, Partie game) throws RemoteException {
        return maDAO.isObserver(login, game);
    }

    public void incrementRound(String login)throws RemoteException{
        maDAO.incrementRound(login);
    }

    @Override
    public boolean putPiece(String login, TypePiece type, int line, int column) throws RemoteException, PieceAlreadyPlacedException, MoveImpossibleException {
        return maDAO.putPiece(login, type, line, column);
    }

    @Override
    public boolean movePiece(String login, int line, int column, int lineBis, int columnBis) throws RemoteException, MoveImpossibleException {
        return maDAO.movePiece(login, line, column, lineBis, columnBis);
    }

    @Override
    public Partie getMyGame(String login) throws RemoteException {
        return maDAO.getMyGame(login);
    }

    @Override
    public Plateau getMyBoard(Partie game) throws RemoteException {
        return maDAO.getMyBoard(game);
    }

    @Override
    public Reserve getMyStock(String login) throws RemoteException {
        return maDAO.getMyStock(login);
    }

    @Override
    public Map<String, Partie> getMyInvitations(String login) throws RemoteException {
        return maDAO.getMyInvitations(login);
    }

    @Override
    public Map<String, Joueur> getPlayersOnline() throws RemoteException {
        return maDAO.getPlayersOnline();
    }

    @Override
    public Map<String, Partie> getPlayersInGame() throws RemoteException {
        return maDAO.getPlayersInGame();
    }

    @Override
    public Map<String, Partie> getUniqueGames() throws RemoteException{
        return maDAO.getUniqueGames();
    }
}
