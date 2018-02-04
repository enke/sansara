package ru.enke.sansara;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.enke.sansara.network.NetworkServer;
import ru.enke.sansara.network.session.NetworkSession;
import ru.enke.sansara.network.session.NetworkSessionRegistry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Server implements Runnable {

    public static final String GAME_VERSION = "1.12.2";
    private static final Logger logger = LogManager.getLogger();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Game Thread"));
    private final NetworkSessionRegistry sessionRegistry = new NetworkSessionRegistry();
    private final NetworkServer networkServer = new NetworkServer(this, sessionRegistry);
    private final String favicon;

    public static void main(final String[] args) throws IOException {
        final String favicon = readServerIcon();

        final Server server = new Server(favicon);
        server.start();
    }

    private static String readServerIcon() throws IOException {
            final Path path = Paths.get("server-icon.png");

            if(!Files.exists(path)) {
                return null;
            }

            final byte[] bytes = Files.readAllBytes(path);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
    }

    public Server(final String favicon) {
        this.favicon = favicon;
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

    public String getFavicon() {
        return favicon;
    }

}
