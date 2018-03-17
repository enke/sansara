package ru.enke.sansara;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import ru.enke.sansara.network.NetworkServer;
import ru.enke.sansara.network.session.Session;
import ru.enke.sansara.network.session.SessionRegistry;
import ru.enke.sansara.player.Player;
import ru.enke.sansara.player.PlayerRegistry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Server extends PlayerRegistry implements Runnable {

    public static final String GAME_VERSION = "1.12.2";
    private static final Logger logger = LogManager.getLogger();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Game Thread"));
    private final SessionRegistry sessionRegistry = new SessionRegistry();
    private final NetworkServer networkServer = new NetworkServer(this, sessionRegistry);
    private final Map<String, World> worlds;
    private final boolean onlineMode;
    private final String favicon;

    public static void main(final String[] args) throws IOException {
        final String favicon = readServerIcon();
        final boolean onlineMode = false;

        final Server server = new Server(favicon, onlineMode);
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

    private Server(final String favicon, final boolean onlineMode) {
        this.favicon = favicon;
        this.onlineMode = onlineMode;
        this.worlds = Collections.singletonMap("world", new World("world", 0, 0));
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
        for(final Session session : sessionRegistry.getSessions()) {
            session.handleIncomingPackets();
        }

        for(final World world : getWorlds()) {
            world.run();
        }
    }

    public String getFavicon() {
        return favicon;
    }

    public boolean isOnlineMode() {
        return onlineMode;
    }

    @Nullable
    public World getWorldByName(final String name) {
        return worlds.get(name);
    }

    public Collection<World> getWorlds() {
        return Collections.unmodifiableCollection(worlds.values());
    }

}
