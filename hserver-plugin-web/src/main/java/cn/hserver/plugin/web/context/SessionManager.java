package cn.hserver.plugin.web.context;

import io.netty.handler.codec.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);
    private final Map<String, HttpSession> sessionMap = new ConcurrentHashMap<>();

    public SessionManager() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                clearExpiredSessions();
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    public HttpSession createSession(Request request) {
       return createSession(request, UUID.randomUUID().toString().replace("-", ""));
    }

    public HttpSession createSession(Request request,String sessionId) {
        HttpSession session = getSession(request);
        long now = Instant.now().getEpochSecond();
        if (null == session) {
            long expired = now + WebConstConfig.SESSION_TIME_OUT;
            session = new HttpSession();
            session.id(sessionId);
            session.created(now);
            session.expired(expired);
            createSession(session);
            return session;
        } else {
            if (session.expired() < now) {
                destroySession(session);
            } else {
                long expired = now + WebConstConfig.SESSION_TIME_OUT;
                session.expired(expired);
            }
        }
        return session;
    }

    public HttpSession getSession(String id) {
        return sessionMap.get(id);
    }

    private void createSession(HttpSession session) {
        sessionMap.put(session.id(), session);
    }

    public boolean destroySession(HttpSession session) {
        session.attributes().clear();
        log.debug("session销毁: {}", session.id());
        return sessionMap.remove(session.id()) != null;
    }

    private void clearExpiredSessions() {
        Collection<HttpSession> sessions = sessionMap.values();
        sessions.parallelStream().filter(this::expires).forEach(this::destroySession);
    }

    public boolean expires(HttpSession session) {
        long now = Instant.now().getEpochSecond();
        return session.expired() < now;
    }

    public HttpSession getSession(Request request) {
        Set<Cookie> cookies = request.getCookies();
        if (null == cookies) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (WebConstConfig.SESSION_KEY.equals(cookie.name())) {
                String value = cookie.value();
                return getSession(value);
            }
        }
        return null;
    }
}
