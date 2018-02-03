package ru.enke.sansara.network.handler;

import ru.enke.minecraft.protocol.packet.PacketMessage;
import ru.enke.sansara.network.session.NetworkSession;

public interface MessageHandler<T extends PacketMessage> {

    void handle(final NetworkSession session, final T msg);

}
