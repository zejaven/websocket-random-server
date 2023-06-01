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

    /**
     * Gets IP address from session
     * Checks if there is already exists session from the same IP address
     * Closes session in case of IP address already exists
     * Creates a set for unique numbers related to session
     *
     * @param session current session
     */
    @OnOpen
    public void onOpen(Session session) {
        var ipAddress = getIpAddress(session);
        if (isSameIp(session.getOpenSessions(), ipAddress)) {
            closeSession(session, ALREADY_CONNECTED_CLOSE_REASON);
            return;
        }
        SESSION_NUMBERS_MAP.put(session, new HashSet<>());
    }

    /**
     * Generates random number which is unique relatively existing session numbers
     * Adds generated number to existing session numbers
     * Returns JSON result represented by generated number
     *
     * @param session current session
     * @param message message from client
     * @return generated random number
     */
    @OnMessage
    public String onMessage(Session session, String message) {
        var existingNumbers = SESSION_NUMBERS_MAP.get(session);
        var generatedNumber = generateRandomNumber(existingNumbers);
        existingNumbers.add(generatedNumber);
        return GSON.toJson(new RandomResponse(generatedNumber.toString()));
    }

    /**
     * Removes numbers of closed session from map
     *
     * @param session closed session
     */
    @OnClose
    public void onClose(Session session) {
        SESSION_NUMBERS_MAP.remove(session);
    }

    /**
     * Displays error message
     *
     * @param session session where error happened
     * @param throwable error
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("Something went wrong in session %s, session is closed".formatted(session.getId()), throwable);
    }

    /**
     * Gets current IP address from session properties
     *
     * @param session current session
     * @return current IP address
     */
    private String getIpAddress(Session session) {
        return ofNullable(session.getUserProperties().get(REMOTE_ADDRESS))
                .map(Object::toString)
                .orElseThrow(() -> new RuntimeException("Remote address not found"));
    }

    /**
     * Checks if any opened session contains current session IP address
     *
     * @param sessions  other sessions
     * @param ipAddress current IP address
     * @return true or false - is IP exists
     */
    private boolean isSameIp(Set<Session> sessions, String ipAddress) {
        return sessions.stream()
                .filter(Session::isOpen)
                .anyMatch(other -> ipAddress.equals(getIpAddress(other)));
    }

    /**
     * Closes the session with corresponding reason
     *
     * @param session     session to close
     * @param closeReason close reason
     */
    @SuppressWarnings("SameParameterValue")
    private void closeSession(Session session, CloseReason closeReason) {
        try {
            session.close(closeReason);
        } catch (Exception exception) {
            log.error("Error during session close", exception);
        }
    }

    /**
     * Generates random number which is unique relatively input numbers
     *
     * @param numbers input numbers
     * @return unique number
     */
    private BigInteger generateRandomNumber(Set<BigInteger> numbers) {
        BigInteger value;
        do {
            value = new BigInteger(NUM_BITS, RANDOM);
        } while (numbers.contains(value));
        return value;
    }
}
