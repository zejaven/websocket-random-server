package org.zeveon.websocketrandomserver.config;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;

import static java.util.Optional.ofNullable;
import static org.zeveon.websocketrandomserver.util.StringUtil.REMOTE_ADDRESS;

/**
 * @author Stanislav Vafin
 */
public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {

    /**
     * Modifies EndpointConfig for WebSocket sessions by putting there remote address property
     *
     * @param config server endpoint config
     * @param request handshake request
     * @param response handshake response
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        ofNullable(request.getHttpSession())
                .filter(e -> e instanceof HttpSession)
                .map(e -> (HttpSession) e)
                .ifPresent(httpSession ->
                        config.getUserProperties().put(REMOTE_ADDRESS, httpSession.getAttribute(REMOTE_ADDRESS)));
    }
}
