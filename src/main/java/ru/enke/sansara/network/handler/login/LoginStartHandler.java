package ru.enke.sansara.network.handler.login;

import ru.enke.minecraft.protocol.packet.client.login.LoginStart;
import ru.enke.sansara.Server;
import ru.enke.sansara.login.LoginProfile;
import ru.enke.sansara.network.handler.MessageHandler;
import ru.enke.sansara.network.session.Session;

import java.util.UUID;

public class LoginStartHandler implements MessageHandler<LoginStart> {

    private final Server server;

    public LoginStartHandler(final Server server) {
        this.server = server;
    }

    @Override
    public void handle(final Session session, final LoginStart loginStart) {
        if(server.isOnlineMode()) {

        } else {
            session.joinGame(new LoginProfile(UUID.randomUUID(), loginStart.getUsername()));
        }
    }

}
