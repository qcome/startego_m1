package actions;


import classes.IGestionStratego;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.SessionAware;

import classes.GestionStratego;

import java.util.Map;

/**
 * Created by root on 12/15/16.
 */
public class Deconnexion extends ActionSupport implements ApplicationAware, SessionAware
{
    private String login;

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
        facade.disconnection(login);
        SessionMap mapSessionListener = (SessionMap) ActionContext.getContext().getSession();
        mapSessionListener.invalidate();
        return SUCCESS;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    public void setSession(Map<String, Object> map)
    {
        this.session = map;
    }
}
