package ru.enke.sansara;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.enke.sansara.network.NetworkServer;
import ru.enke.sansara.network.session.NetworkSession;
import ru.enke.sansara.network.session.NetworkSessionRegistry;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

public class Server implements Runnable {

    private static final Logger logger = LogManager.getLogger();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Game Thread"));
    private final NetworkSessionRegistry sessionRegistry = new NetworkSessionRegistry();
    private final NetworkServer networkServer = new NetworkServer(sessionRegistry);

    public static void main(final String[] args) {
        new Server().start();
    }

    private void start() {
        final int port = 25565;

        if(networkServer.bind(port)) {
            logger.info("Successfully bind server on port {}", port);
        } else {
            logger.warn("Failed bind server on port {}", port);
            return;
        }

        executor.scheduleAtFixedRate(this, 0, 50, MILLISECONDS);
    }

    @Override
    public void run() {
        for(final NetworkSession session : sessionRegistry.getSessions()) {
            session.handleIncomingPackets();
        }
    }

}
