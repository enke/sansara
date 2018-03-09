package ru.enke.sansara.player;

import ru.enke.minecraft.protocol.packet.PacketMessage;
import ru.enke.sansara.World;
import ru.enke.sansara.login.LoginProfile;
import ru.enke.sansara.network.session.Session;

import java.net.SocketAddress;
import java.util.UUID;

public class Player {

    private final int id;
    private final World world;
    private final LoginProfile profile;
    private final Session session;

    public Player(final int id, final Session session, final World world, final LoginProfile profile) {
        this.id = id;
        this.session = session;
        this.world = world;
        this.profile = profile;
    }

    public int getId() {
        return id;
    }

    public World getWorld() {
        return world;
    }

    public LoginProfile getProfile() {
        return profile;
    }

    public void sendPacket(final PacketMessage msg) {
        session.sendPacket(msg);
    }

    public SocketAddress getAddress() {
        return session.getAddress();
    }

}
