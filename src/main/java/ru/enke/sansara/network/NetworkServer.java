package ru.enke.sansara.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ru.enke.minecraft.protocol.codec.LengthCodec;
import ru.enke.minecraft.protocol.codec.PacketCodec;
import ru.enke.minecraft.protocol.packet.client.handshake.Handshake;
import ru.enke.minecraft.protocol.packet.client.status.StatusRequest;
import ru.enke.sansara.Server;
import ru.enke.sansara.network.handler.handshake.HandshakeHandler;
import ru.enke.sansara.network.handler.MessageHandlerRegistry;
import ru.enke.sansara.network.handler.status.StatusRequestHandler;
import ru.enke.sansara.network.session.NetworkSession;
import ru.enke.sansara.network.session.NetworkSessionRegistry;

import static ru.enke.minecraft.protocol.ProtocolSide.SERVER;

public class NetworkServer {

    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private final MessageHandlerRegistry messageHandlerRegistry = new MessageHandlerRegistry();
    private final NetworkSessionRegistry sessionRegistry;

    public NetworkServer(final Server server, final NetworkSessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;

        messageHandlerRegistry.registerHandler(Handshake.class, new HandshakeHandler());
        messageHandlerRegistry.registerHandler(StatusRequest.class, new StatusRequestHandler(server));
    }

    public boolean bind(final int port) {
        return new ServerBootstrap()
                .group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(final Channel channel) throws Exception {
                        final ChannelPipeline pipeline = channel.pipeline();

                        pipeline.addLast(NetworkSession.LENGTH_CODEC_NAME, new LengthCodec());
                        pipeline.addLast(NetworkSession.PACKET_CODEC_NAME, new PacketCodec(SERVER, false, false, null));
                        pipeline.addLast(NetworkSession.SESSION_HANDLER_NAME, new NetworkSession(channel, sessionRegistry, messageHandlerRegistry));
                    }
                })
                .bind(port)
                .awaitUninterruptibly().isSuccess();
    }

    public void stop() {
        eventLoopGroup.shutdownGracefully();
    }

}
