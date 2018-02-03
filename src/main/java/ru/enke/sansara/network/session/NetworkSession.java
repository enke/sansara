package ru.enke.sansara.network.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.enke.minecraft.protocol.packet.PacketMessage;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class NetworkSession extends SimpleChannelInboundHandler<PacketMessage> {

    public static final String LENGTH_CODEC_NAME = "length";
    public static final String PACKET_CODEC_NAME = "packet";
    public static final String SESSION_HANDLER_NAME = "session";

    private static final Logger logger = LogManager.getLogger();

    private final Queue<PacketMessage> messageQueue = new LinkedBlockingQueue<>();
    private final NetworkSessionRegistry sessionRegistry;
    private final Channel channel;

    public NetworkSession(final Channel channel, final NetworkSessionRegistry sessionRegistry) {
        this.channel = channel;
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        logger.debug("New network connection from ip {}", ctx.channel().remoteAddress());
        sessionRegistry.addSession(this);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("Disconnected {}", ctx.channel().remoteAddress());
        sessionRegistry.removeSession(this);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final PacketMessage msg) throws Exception {
        if(logger.isTraceEnabled()) {
            logger.trace("Received packet {}", msg);
        }

        messageQueue.add(msg);
    }

    public void handleIncomingPackets() {
        PacketMessage msg;

        while((msg = messageQueue.poll()) != null) {
            if(logger.isTraceEnabled()) {
                logger.trace("Handle packet {}", msg);
            }
        }
    }

}
