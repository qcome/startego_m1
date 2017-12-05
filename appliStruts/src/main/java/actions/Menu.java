package actions;

import classes.IGestionStratego;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.SessionAware;

import classes.GestionStratego;
import classes.Partie;
import classes.Joueur;

import classes.exceptions.GameNotFoundException;
import classes.exceptions.GameAlreadyBegunException;
import classes.exceptions.GameOverException;

import java.util.Map;

/**
 * Created by root on 11/14/16.
 */
public class Menu extends ActionSupport implements ApplicationAware, SessionAware
{
    private String NO_SELECTION = "noSelection";

    private String GAME_ALREADY_BEGUN = "gameAlreadyBegun";

    private String login;

    private String loginAdversary;

    private Map<String, Partie> invitations;

    private String position;

    private String gameType;

    private Map <String, Joueur> playersOnline;

    private Map <String, Partie> playersInGame;

    private Map <String, Partie> uniqueGames;

    private Map<String, Object> session;

    private IGestionStratego facade;

    // ??
    private String loginPlayerGame;

    public void setApplication(Map<String, Object> map)
    {
        facade = (IGestionStratego) map.get("facade");
        if(facade == null)
        {
            facade = GestionStratego.getInstance();
            map.put("facade", facade);
        }

    }

    @Override
    public String execute() throws Exception
    {
        if(gameType == null)
            return NO_SELECTION;
        return SUCCESS;
    }

    public String createGame()
    {
        boolean isPrivate;
        if(gameType.equals("Publique"))
            isPrivate = false;
        else
            isPrivate = true;
        facade.createNewGame(login, isPrivate);
        session.put("login", login);
        session.remove("loginAdversary");
        session.put("isObserver", false);
        session.put("colorPlayer", "steelblue");
        session.put("colorAdversary", "indianred");
        session.put("game", facade.getMyGame(login));
        session.put("isPrivate", isPrivate);
        session.put("board", facade.getMyGame(login).getTab(login));
        session.put("stock", facade.getMyStock(login).getStock());
        session.put("playersOnline", facade.getPlayersOnline());

        return SUCCESS;
    }

    public String joinGame()
    {
        if(loginAdversary == null)
            return NO_SELECTION;

        Partie game = facade.getMyGame(loginAdversary);
        try {
            facade.joinGame(game, login);
        } catch(GameNotFoundException e)
        {
            addActionError("La partie est introuvable.");
            return NO_SELECTION;
        } catch(GameOverException e)
        {
            addActionError("La partie vient de se terminer.");
            return NO_SELECTION;
        }

        boolean isObserver = facade.isObserver(login, game);

        if(isObserver)
        {
            session.put("login", login);
            session.put("loginPlayer1", loginAdversary);
            session.put("loginPlayer2", game.getPlayer2().getLogin());
        } else
        {
            session.put("login", login);
            session.put("loginAdversary", loginAdversary);
        }

        session.put("isObserver", isObserver);
        session.put("colorPlayer", facade.getPlayersOnline().get(login).getColor());
        if(!isObserver)
            session.put("colorAdversary", "steelblue");
        session.put("game", facade.getMyGame(login));
        session.put("isPrivate", false);
        session.put("board", facade.getMyGame(login).getTab(login));
        session.put("stock", facade.getMyStock(login).getStock());
        session.put("playersOnline", facade.getPlayersOnline());

        return SUCCESS;
    }


    public String acceptInvitation()
    {
        try {
            facade.acceptInvitation(login, loginAdversary);
        } catch(GameNotFoundException e)
        {
            addActionError("La partie est introuvable.");
            facade.declineInvitation(login, loginAdversary);
            session.put("invitations", facade.getMyInvitations(login));
            return GAME_ALREADY_BEGUN;
        } catch(GameAlreadyBegunException e)
        {
            addActionError("La partie a déjà commencé.");
            facade.declineInvitation(login, loginAdversary);
            session.put("invitations", facade.getMyInvitations(login));
            return GAME_ALREADY_BEGUN;
        }

        session.put("login", login);
        session.put("loginAdversary", loginAdversary);
        session.put("colorPlayer", "indianred");
        session.put("colorAdversary", "steelblue");
        session.put("isPrivate", true);
        session.put("game", facade.getMyGame(login));
        session.put("isObserver", false);
        session.put("board", facade.getMyGame(login).getTab(login));
        session.put("stock", facade.getMyStock(login).getStock());
        session.put("playersOnline", facade.getPlayersOnline());
        return SUCCESS;
    }

    public String declineInvitation()
    {
        facade.declineInvitation(login, loginAdversary);

        return SUCCESS;
    }

    public String refreshMenu()
    {
        uniqueGames = facade.getUniqueGames();
        invitations = facade.getMyInvitations(login);
        playersOnline = facade.getPlayersOnline();
        return SUCCESS;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLoginAdversary()
    {
        return loginAdversary;
    }

    public void setLoginAdversary(String loginAdversary)
    {
        this.loginAdversary = loginAdversary;
    }

    public Map<String, Partie> getInvitations()
    {
        return invitations;
    }

    public void setInvitations(Map<String, Partie> invitations) {
        this.invitations = invitations;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public Map<String, Joueur> getPlayersOnline() {
        return playersOnline;
    }

    public void setPlayersOnline(Map<String, Joueur> playersOnline) {
        this.playersOnline = playersOnline;
    }

    public Map<String, Partie> getPlayersInGame() {
        return playersInGame;
    }

    public void setPlayersInGame(Map<String, Partie> playersInGame) {
        this.playersInGame = playersInGame;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    public void setSession(Map<String, Object> map)
    {
        this.session = map;
    }

    public Map<String, Partie> getUniqueGames() {
        return uniqueGames;
    }

    public void setUniqueGames(Map<String, Partie> uniqueGames) {
        this.uniqueGames = uniqueGames;
    }
}