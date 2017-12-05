package actions;

import classes.IGestionStratego;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.SessionAware;

import classes.exceptions.UnknownPlayerException;
import classes.exceptions.PlayerAlreadyConnectedException;

import classes.GestionStratego;

import java.util.Map;

/**
 * Created by root on 11/13/16.
 */
public class Connexion extends ActionSupport implements ApplicationAware, SessionAware
{
    private String login;

    private String password;

    private Map<String, Object> session;

    private IGestionStratego facade;

    public void setApplication(Map<String, Object> map)
    {
        facade = (IGestionStratego) map.get("facade");
        if(facade == null)
        {
            System.out.println(GestionStratego.getInstance());
            facade = GestionStratego.getInstance();
            map.put("facade", facade);
        }
    }

    @Override
    public String execute() throws Exception
    {
        try {
            facade.connection(login, password);
        } catch(UnknownPlayerException e)
        {
            addFieldError("connexion", "Login ou mot de passe incorrect.");
            return ERROR;
        } catch(PlayerAlreadyConnectedException e)
        {
            addFieldError("connexion", "Utilisateur déjà connecté.");
            return ERROR;
        }
        session.put("login", login);
        session.put("invitations", facade.getMyInvitations(login));
        session.put("playersOnline", facade.getPlayersOnline());
        session.put("games", facade.getPlayersInGame());
        session.put("uniqueGames", facade.getUniqueGames());
        return SUCCESS;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    public void setSession(Map<String, Object> map)
    {
        this.session = map;
    }
}