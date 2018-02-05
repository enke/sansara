package ru.enke.sansara.network.handler;

import ru.enke.minecraft.protocol.packet.PacketMessage;
import ru.enke.sansara.network.session.Session;

public interface MessageHandler<T extends PacketMessage> {

    void handle(final Session session, final T msg);

}
