package ru.enke.sansara.network.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.enke.minecraft.protocol.ProtocolState;
import ru.enke.minecraft.protocol.packet.PacketMessage;
import ru.enke.sansara.network.handler.MessageHandler;
import ru.enke.sansara.network.handler.MessageHandlerRegistry;

import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class NetworkSession extends SimpleChannelInboundHandler<PacketMessage> {

    public static final String LENGTH_CODEC_NAME = "length";
    public static final String PACKET_CODEC_NAME = "packet";
    public static final String SESSION_HANDLER_NAME = "session";

    private static final Logger logger = LogManager.getLogger();

    private final Queue<PacketMessage> messageQueue = new LinkedBlockingQueue<>();
    private final MessageHandlerRegistry messageHandlerRegistry;
    private final NetworkSessionRegistry sessionRegistry;
    private ProtocolState state = ProtocolState.HANDSHAKE;
    private final Channel channel;

    public NetworkSession(final Channel channel, final NetworkSessionRegistry sessionRegistry,
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

    public SocketAddress getAddress() {
        return channel.remoteAddress();
    }

    public void setState(final ProtocolState state) {
        this.state = state;
    }

}
