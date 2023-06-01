package org.zeveon.websocketrandomserver.endpoint;

import com.google.gson.Gson;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.zeveon.websocketrandomserver.config.GetHttpSessionConfigurator;
import org.zeveon.websocketrandomserver.model.RandomResponse;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Optional.ofNullable;
import static org.zeveon.websocketrandomserver.util.StringUtil.REMOTE_ADDRESS;

/**
 * @author Stanislav Vafin
 */
@Slf4j
@ServerEndpoint(
        value = "/random",
        configurator = GetHttpSessionConfigurator.class
)
public class RandomServerEndpoint {

    private static final CloseReason ALREADY_CONNECTED_CLOSE_REASON =
            new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Already connected");
    private static final Map<Session, Set<BigInteger>> SESSION_NUMBERS_MAP = new ConcurrentHashMap<>();
    private static final Random RANDOM = new Random();
    private static final Gson GSON = new Gson();
    private static final int NUM_BITS = 130;

    @OnOpen
    public void onOpen(Session session) {
        var ipAddress = getIpAddress(session);
        if (isSameIp(session, ipAddress)) {
            closeSession(session, ALREADY_CONNECTED_CLOSE_REASON);
            return;
        }
        SESSION_NUMBERS_MAP.put(session, new HashSet<>());
    }

    @OnMessage
    public String onMessage(Session session, String message) {
        var existingNumbers = SESSION_NUMBERS_MAP.get(session);
        var generatedNumber = generateRandomNumber(existingNumbers);
        existingNumbers.add(generatedNumber);
        return GSON.toJson(new RandomResponse(generatedNumber.toString()));
    }

    @OnClose
    public void onClose(Session session) {
        SESSION_NUMBERS_MAP.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("Something went wrong in session %s, session is closed".formatted(session.getId()), throwable);
    }

    private String getIpAddress(Session session) {
        return ofNullable(session.getUserProperties().get(REMOTE_ADDRESS))
                .map(Object::toString)
                .orElseThrow(() -> new RuntimeException("Remote address not found"));
    }

    private boolean isSameIp(Session session, String ipAddress) {
        return session.getOpenSessions().stream()
                .filter(Session::isOpen)
                .anyMatch(other -> ipAddress.equals(getIpAddress(other)));
    }

    @SuppressWarnings("SameParameterValue")
    private void closeSession(Session session, CloseReason closeReason) {
        try {
            session.close(closeReason);
        } catch (Exception exception) {
            log.error("Error during session close", exception);
        }
    }

    private BigInteger generateRandomNumber(Set<BigInteger> bigIntegers) {
        BigInteger value;
        do {
            value = new BigInteger(NUM_BITS, RANDOM);
        } while (bigIntegers.contains(value));
        return value;
    }
}
