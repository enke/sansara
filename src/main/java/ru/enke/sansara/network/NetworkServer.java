package ru.enke.sansara.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ru.enke.minecraft.protocol.codec.LengthCodec;
import ru.enke.minecraft.protocol.codec.PacketCodec;
import ru.enke.sansara.network.session.NetworkSession;
import ru.enke.sansara.network.session.NetworkSessionRegistry;

import static ru.enke.minecraft.protocol.ProtocolSide.SERVER;

public class NetworkServer {

    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private final NetworkSessionRegistry sessionRegistry;

    public NetworkServer(final NetworkSessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
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
                        pipeline.addLast(NetworkSession.SESSION_HANDLER_NAME, new NetworkSession(channel, sessionRegistry));
                    }
                })
                .bind(port)
                .awaitUninterruptibly().isSuccess();
    }

    public void stop() {
        eventLoopGroup.shutdownGracefully();
    }

}
