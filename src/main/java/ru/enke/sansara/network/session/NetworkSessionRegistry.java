package ru.enke.sansara.network.session;

import io.netty.util.internal.ConcurrentSet;

import java.util.Set;

public class NetworkSessionRegistry {

    private final Set<NetworkSession> sessions = new ConcurrentSet<>();

    void addSession(final NetworkSession session) {
        sessions.add(session);
    }

    void removeSession(final NetworkSession session) {
        sessions.remove(session);
    }

    public Set<NetworkSession> getSessions() {
        return sessions;
    }

}
