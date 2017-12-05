package classes;

import java.io.Serializable;
import java.util.Map;
import java.util.Hashtable;

/**
 * Created by root on 11/11/16.
 */
public class Joueur implements Serializable
{

    private String login;

    private String password;

    private String color;

    private Map invitations = new Hashtable<String, Partie>();

    public Joueur(String login, String password)
    {
        this.login = login;
        this.password = password;
        color = "";
    }

    public void addInvitation(String login, Partie game)
    {
        invitations.put(login, game);
    }

    public void declineInvitation(String login)
    {
        invitations.remove(login);
    }

    public void deleteAllInvitations()
    {
        invitations.clear();
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Map getInvitations() {
        return invitations;
    }

}
