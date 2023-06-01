package org.zeveon.websocketrandomserver.listener;

import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpServletRequest;

import static org.zeveon.websocketrandomserver.util.StringUtil.REMOTE_ADDRESS;

/**
 * @author Stanislav Vafin
 */
@WebListener
public class RequestListener implements ServletRequestListener {

    @Override
    public void requestInitialized(ServletRequestEvent event) {
        var request = (HttpServletRequest) event.getServletRequest();
        var httpSession = request.getSession();
        httpSession.setAttribute(REMOTE_ADDRESS, request.getRemoteAddr());
    }
}
