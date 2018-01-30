package ru.enke.sansara;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.enke.sansara.network.NetworkServer;

public class Server {

    private static final Logger logger = LogManager.getLogger();

    public static void main(final String[] args) {
        new Server().start();
    }

    private void start() {
        final NetworkServer networkServer = new NetworkServer(this);
        final int port = 25565;

        if(networkServer.bind(port)) {
            logger.info("Successfully bind server on port {}", port);
        } else {
            logger.warn("Failed bind server on port {}", port);
        }
    }

}
