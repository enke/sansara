package ru.enke.sansara.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ru.enke.minecraft.protocol.codec.LengthCodec;
import ru.enke.minecraft.protocol.codec.PacketCodec;
import ru.enke.minecraft.protocol.packet.client.login.LoginStart;
import ru.enke.minecraft.protocol.packet.client.status.PingRequest;
import ru.enke.minecraft.protocol.packet.client.status.StatusRequest;
import ru.enke.sansara.Server;
import ru.enke.sansara.network.handler.MessageHandlerRegistry;
import ru.enke.sansara.network.handler.login.LoginStartHandler;
import ru.enke.sansara.network.handler.status.PingRequestHandler;
import ru.enke.sansara.network.handler.status.StatusRequestHandler;
import ru.enke.sansara.network.session.Session;
import ru.enke.sansara.network.session.SessionRegistry;

import static ru.enke.minecraft.protocol.ProtocolSide.SERVER;

public class NetworkServer {

    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private final MessageHandlerRegistry messageHandlerRegistry = new MessageHandlerRegistry();
    private final SessionRegistry sessionRegistry;
    private final Server server;

    public NetworkServer(final Server server, final SessionRegistry sessionRegistry) {
        this.server = server;
        this.sessionRegistry = sessionRegistry;

        messageHandlerRegistry.registerHandler(StatusRequest.class, new StatusRequestHandler(server));
        messageHandlerRegistry.registerHandler(PingRequest.class, new PingRequestHandler());
        messageHandlerRegistry.registerHandler(LoginStart.class, new LoginStartHandler(server));
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

                        pipeline.addLast(Session.LENGTH_CODEC_NAME, new LengthCodec());
                        pipeline.addLast(Session.PACKET_CODEC_NAME, new PacketCodec(SERVER, false, false, null));
                        pipeline.addLast(Session.SESSION_HANDLER_NAME, new Session(channel, server, sessionRegistry, messageHandlerRegistry));
                    }
                })
                .bind(port)
                .awaitUninterruptibly().isSuccess();
    }

    public void stop() {
        eventLoopGroup.shutdownGracefully();
    }

}
