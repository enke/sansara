package ru.enke.sansara.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.enke.minecraft.protocol.packet.PacketMessage;
import ru.enke.sansara.Server;

public class NetworkSession extends SimpleChannelInboundHandler<PacketMessage> {

    private static final Logger logger = LogManager.getLogger();
    private final Server server;

    public NetworkSession(final Server server) {
        this.server = server;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final PacketMessage msg) throws Exception {
        logger.debug("Received packet {}", msg);
    }

}