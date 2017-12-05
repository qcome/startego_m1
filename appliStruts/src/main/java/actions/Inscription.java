package actions;

import classes.IGestionStratego;
import classes.exceptions.LoginTooShortException;
import classes.exceptions.PasswordTooShortException;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.SessionAware;

import classes.exceptions.LoginAlreadyTakenException;
import classes.exceptions.PasswordConfirmationException;

import classes.GestionStratego;

import java.util.Map;

/**
 * Created by root on 12/15/16.
 */
public class Inscription extends ActionSupport implements ApplicationAware, SessionAware
{

    private String login;

    private String password;

    private String passwordConfirmation;

    private String error;

    private Map<String, Object> session;

    private IGestionStratego facade;

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
        try {
            facade.registration(login, password, passwordConfirmation);
            error = "";
        } catch (LoginTooShortException e) {
            error = "Votre login doit faire au moins 4 caractères.";
        } catch (PasswordConfirmationException e) {
            error = "Erreur dans la saisie du mot de passe.";
        } catch (PasswordTooShortException e) {
            error = "Votre mot de passe doit faire au moins 4 caractères.";
        } catch (LoginAlreadyTakenException e) {
            error = "Login déjà utilisé.";
        }
        return SUCCESS;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    public void setSession(Map<String, Object> map)
    {
        this.session = map;
    }
}
