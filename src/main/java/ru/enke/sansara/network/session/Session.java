package ru.enke.sansara.network.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import ru.enke.minecraft.protocol.ProtocolState;
import ru.enke.minecraft.protocol.codec.CompressionCodec;
import ru.enke.minecraft.protocol.packet.PacketMessage;
import ru.enke.minecraft.protocol.packet.server.login.LoginSetCompression;
import ru.enke.minecraft.protocol.packet.server.login.LoginSuccess;
import ru.enke.sansara.login.LoginProfile;
import ru.enke.sansara.network.handler.MessageHandler;
import ru.enke.sansara.network.handler.MessageHandlerRegistry;

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
    private LoginProfile profile;

    public Session(final Channel channel, final SessionRegistry sessionRegistry,
                   final MessageHandlerRegistry messageHandlerRegistry) {
        this.channel = channel;
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
    }

    private void setCompression(final int threshold) {
        sendPacket(new LoginSetCompression(threshold));
        channel.pipeline().addBefore(SESSION_HANDLER_NAME, COMPRESSION_CODEC_NAME, new CompressionCodec(threshold));
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
    public LoginProfile getProfile() {
        return profile;
    }

}
