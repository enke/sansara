package ru.enke.sansara.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ru.enke.minecraft.protocol.codec.LengthCodec;
import ru.enke.minecraft.protocol.codec.PacketCodec;
import ru.enke.sansara.Server;

import static ru.enke.minecraft.protocol.ProtocolSide.SERVER;

public class NetworkServer {

    public static final String LENGTH_CODEC_NAME = "length";
    public static final String PACKET_CODEC_NAME = "packet";
    public static final String SESSION_HANDLER_NAME = "session";

    private final Server server;

    public NetworkServer(final Server server) {
        this.server = server;
    }

    public boolean bind(final int port) {
        return new ServerBootstrap()
                .group(new NioEventLoopGroup(4))
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(final Channel channel) throws Exception {
                        final ChannelPipeline pipeline = channel.pipeline();

                        pipeline.addLast(LENGTH_CODEC_NAME, new LengthCodec());
                        pipeline.addLast(PACKET_CODEC_NAME, new PacketCodec(SERVER, false, false, null));
                        pipeline.addLast(SESSION_HANDLER_NAME, new NetworkSession(server));
                    }
                })
                .bind(port)
                .awaitUninterruptibly().isSuccess();
    }

}
