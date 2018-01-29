package ru.enke.sansara;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.enke.minecraft.protocol.codec.LengthCodec;
import ru.enke.minecraft.protocol.codec.PacketCodec;

import static ru.enke.minecraft.protocol.ProtocolSide.SERVER;

public class Server {

    private final Logger logger = LogManager.getLogger();

    public static void main(final String[] args) {
        new Server().start();
    }

    private void start() {
        final int port = 25565;

        if(bind(port)) {
            logger.info("Successfully bind server on port {}", port);
        } else {
            logger.warn("Failed bind server on port {}", port);
        }
    }

    private boolean bind(final int port) {
        return new ServerBootstrap()
                .group(new NioEventLoopGroup(4))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(final Channel channel) throws Exception {
                        final ChannelPipeline pipeline = channel.pipeline();

                        pipeline.addLast("length", new LengthCodec());
                        pipeline.addLast("packet", new PacketCodec(SERVER, false, false, null));
                    }
                })
                .bind(port)
                .awaitUninterruptibly().isSuccess();
    }

}
