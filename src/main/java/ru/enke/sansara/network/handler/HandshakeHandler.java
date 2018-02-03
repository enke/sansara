package ru.enke.sansara.network.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.enke.minecraft.protocol.ProtocolState;
import ru.enke.minecraft.protocol.packet.client.handshake.Handshake;
import ru.enke.sansara.network.session.NetworkSession;

public class HandshakeHandler implements MessageHandler<Handshake> {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void handle(final NetworkSession session, final Handshake msg) {
        final ProtocolState state = msg.getState();

        logger.debug("Switching {} session to {}", session.getAddress(), state);
        session.setState(state);
    }

}
