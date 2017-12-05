package listeners;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


/**
 * Created by Quentin on 01/11/2016.
 */
public class SessionListener implements HttpSessionListener
{

    private static int totalActiveSessions;

    public static int getTotalActiveSession(){
        return totalActiveSessions;
    }

    public void sessionCreated(HttpSessionEvent event) {

        System.out.println("A new session is created: "+ event.getSession().getId());
        totalActiveSessions++;
    }

    public void sessionDestroyed(HttpSessionEvent event)
    {
        totalActiveSessions--;
        System.out.println("session is destroyed: " + event.getSession().getId());
    }

}
