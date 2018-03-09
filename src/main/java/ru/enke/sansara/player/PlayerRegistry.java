package ru.enke.sansara.player;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlayerRegistry {

    private final Map<UUID, Player> players = new HashMap<>();

    @Nullable
    public Player getPlayerById(final UUID uuid) {
        return players.get(uuid);
    }

    public Collection<Player> getPlayers() {
        return Collections.unmodifiableCollection(players.values());
    }

    public void addPlayer(final Player player) {
        players.put(player.getProfile().getId(), player);
    }

    public void removePlayer(final Player player) {
        players.remove(player.getProfile().getId(), player);
    }

}
