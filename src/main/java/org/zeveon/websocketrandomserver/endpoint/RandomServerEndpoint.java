package org.zeveon.websocketrandomserver.endpoint;

import com.google.gson.Gson;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.zeveon.websocketrandomserver.config.GetHttpSessionConfigurator;
import org.zeveon.websocketrandomserver.model.RandomResponse;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.zeveon.websocketrandomserver.util.StringUtil.REMOTE_ADDRESS;

/**
 * @author Stanislav Vafin
 */
@ServerEndpoint(
        value = "/random",
        configurator = GetHttpSessionConfigurator.class
)
public class RandomServerEndpoint {

    private static final Random random = new Random();
    private static final Map<Session, Set<BigInteger>> sessionBigIntegerMap = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session) {
        var ipAddress = getIpAddress(session);

        for (Session other : session.getOpenSessions()) {
            if (other.isOpen() && ipAddress.equals(getIpAddress(other))) {
                try {
                    session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Already connected"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        sessionBigIntegerMap.put(session, new HashSet<>());
    }

    @OnMessage
    public String onMessage(Session session, String message) {
        var bigIntegers = sessionBigIntegerMap.get(session);
        BigInteger value;
        do {
            value = new BigInteger(130, random);
        } while (bigIntegers.contains(value));
        bigIntegers.add(value);
        return gson.toJson(new RandomResponse(value.toString()));
    }

    @OnClose
    public void onClose(Session session) {
        sessionBigIntegerMap.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    private String getIpAddress(Session session) {
        return session.getUserProperties().get(REMOTE_ADDRESS).toString();
    }
}
