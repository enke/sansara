package ru.enke.sansara.network.handler.status;

import ru.enke.minecraft.protocol.packet.client.status.PingRequest;
import ru.enke.minecraft.protocol.packet.server.status.PingResponse;
import ru.enke.sansara.network.handler.MessageHandler;
import ru.enke.sansara.network.session.Session;

public class PingRequestHandler implements MessageHandler<PingRequest> {

    @Override
    public void handle(final Session session, final PingRequest pingRequest) {
        session.sendPacket(new PingResponse(pingRequest.getTime()));
    }

}
