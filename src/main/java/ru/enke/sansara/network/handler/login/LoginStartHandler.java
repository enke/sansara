package ru.enke.sansara.network.handler.login;

import ru.enke.minecraft.protocol.packet.client.login.LoginStart;
import ru.enke.sansara.network.handler.MessageHandler;
import ru.enke.sansara.network.session.Session;

public class LoginStartHandler implements MessageHandler<LoginStart> {

    @Override
    public void handle(final Session session, final LoginStart loginStart) {
        session.setName(session.getName());
    }

}
