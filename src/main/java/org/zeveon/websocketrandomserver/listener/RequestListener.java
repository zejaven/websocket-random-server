package org.zeveon.websocketrandomserver.listener;

import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpServletRequest;

import static java.util.Optional.ofNullable;
import static org.zeveon.websocketrandomserver.util.StringUtil.REMOTE_ADDRESS;

/**
 * @author Stanislav Vafin
 */
@WebListener
public class RequestListener implements ServletRequestListener {

    /**
     * Modifies http session by putting there remote address property
     *
     * @param event servlet request event
     */
    @Override
    public void requestInitialized(ServletRequestEvent event) {
        ofNullable(event.getServletRequest())
                .filter(servletRequest -> servletRequest instanceof HttpServletRequest)
                .map(servletRequest -> (HttpServletRequest) servletRequest)
                .ifPresent(httpServletRequest -> ofNullable(httpServletRequest.getSession())
                        .ifPresent(httpSession ->
                                httpSession.setAttribute(REMOTE_ADDRESS, httpServletRequest.getRemoteAddr())));
    }
}
