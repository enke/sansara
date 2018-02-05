package ru.enke.sansara.network.session;

import io.netty.util.internal.ConcurrentSet;

import java.util.Collections;
import java.util.Set;

public class SessionRegistry {

    private final Set<Session> sessions = new ConcurrentSet<>();

    void addSession(final Session session) {
        sessions.add(session);
    }

    void removeSession(final Session session) {
        sessions.remove(session);
    }

    public Set<Session> getSessions() {
        return Collections.unmodifiableSet(sessions);
    }

}
