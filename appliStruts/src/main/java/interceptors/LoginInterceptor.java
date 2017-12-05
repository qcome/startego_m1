package interceptors;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import java.util.Map;

/**
 * Created by Quentin on 01/12/2016.
 */
public class LoginInterceptor extends AbstractInterceptor implements Cloneable
{
    @Override
    public String intercept(ActionInvocation invocation) throws Exception
    {
        ActionContext ac = invocation.getInvocationContext();
        Map session = ac.getSession();
        String login = (String) session.get("login");

        if (login == null)
            return Action.LOGIN;
        else
        {
            String result = invocation.invoke();
            return result;
        }
    }

}
