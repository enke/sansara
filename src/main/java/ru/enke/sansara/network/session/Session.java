package ru.enke.sansara.network.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import ru.enke.minecraft.protocol.codec.CompressionCodec;
import ru.enke.minecraft.protocol.packet.PacketMessage;
import ru.enke.minecraft.protocol.packet.data.game.Difficulty;
import ru.enke.minecraft.protocol.packet.data.game.GameMode;
import ru.enke.minecraft.protocol.packet.data.game.WorldType;
import ru.enke.minecraft.protocol.packet.server.game.JoinGame;
import ru.enke.minecraft.protocol.packet.server.game.SpawnPosition;
import ru.enke.minecraft.protocol.packet.server.game.player.ServerPlayerPositionLook;
import ru.enke.minecraft.protocol.packet.server.login.LoginSetCompression;
import ru.enke.minecraft.protocol.packet.server.login.LoginSuccess;
import ru.enke.sansara.Server;
import ru.enke.sansara.World;
import ru.enke.sansara.login.LoginProfile;
import ru.enke.sansara.network.handler.MessageHandler;
import ru.enke.sansara.network.handler.MessageHandlerRegistry;
import ru.enke.sansara.player.Player;

import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Session extends SimpleChannelInboundHandler<PacketMessage> {

    public static final String LENGTH_CODEC_NAME = "length";
    public static final String COMPRESSION_CODEC_NAME = "compression";
    public static final String PACKET_CODEC_NAME = "packet";
    public static final String SESSION_HANDLER_NAME = "session";

    private static final Logger logger = LogManager.getLogger();

    private final Queue<PacketMessage> messageQueue = new LinkedBlockingQueue<>();
    private final MessageHandlerRegistry messageHandlerRegistry;
    private final SessionRegistry sessionRegistry;
    private final Channel channel;
    private final Server server;
    private Player player;

    public Session(final Channel channel, final Server server, final SessionRegistry sessionRegistry,
                   final MessageHandlerRegistry messageHandlerRegistry) {
        this.channel = channel;
        this.server = server;
        this.sessionRegistry = sessionRegistry;
        this.messageHandlerRegistry = messageHandlerRegistry;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        logger.debug("New network connection from ip {}", getAddress());
        sessionRegistry.addSession(this);
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        logger.debug("Disconnected {}", getAddress());
        sessionRegistry.removeSession(this);

        if(player != null) {
            final World world = player.getWorld();
            world.removePlayer(player);
            server.removePlayer(player);
        }
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final PacketMessage msg) throws Exception {
        if(logger.isTraceEnabled()) {
            logger.trace("Received packet {}", msg);
        }

        messageQueue.add(msg);
    }

    @SuppressWarnings("unchecked")
    public void handleIncomingPackets() {
        PacketMessage msg;

        while((msg = messageQueue.poll()) != null) {
            final MessageHandler handler = messageHandlerRegistry.getMessageHandler(msg);

            if(handler != null) {
                handler.handle(this, msg);
            } else {
                logger.warn("Message {} missing handler", msg);
            }
        }
    }

    public void joinGame(final LoginProfile profile) {
        // Finalize login.
        setCompression(CompressionCodec.DEFAULT_COMPRESSION_THRESHOLD);
        sendPacket(new LoginSuccess(profile.getId().toString(), profile.getName()));

        logger.info("Player {} joined game", profile.getName());

        final World world = server.getWorlds().iterator().next();

        player = new Player(1, this, world, profile);
        server.addPlayer(player);
        world.addPlayer(player);

        sendPacket(new JoinGame(player.getId(), GameMode.SURVIVAL, 1, Difficulty.NORMAL, 100, WorldType.DEFAULT, true));
        sendPacket(new SpawnPosition(world.getSpawnPosition()));

        sendPacket(new ServerPlayerPositionLook(0, 63, 0, 0, 0, 0, 1));
    }

    private void setCompression(final int threshold) {
        sendPacket(new LoginSetCompression(threshold));
        channel.pipeline().addBefore(PACKET_CODEC_NAME, COMPRESSION_CODEC_NAME, new CompressionCodec(threshold));
        logger.trace("Enable compression with {} threshold", threshold);
    }

    public void sendPacket(final PacketMessage msg) {
        if(logger.isTraceEnabled()) {
            logger.trace("Sending packet {}", msg);
        }

        channel.writeAndFlush(msg);
    }

    public SocketAddress getAddress() {
        return channel.remoteAddress();
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

}
