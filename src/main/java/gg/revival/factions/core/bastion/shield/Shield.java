package gg.revival.factions.core.bastion.shield;

import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class Shield {

    /**
     * Contains all ShiledPlayer sessions loaded on the server
     */
    @Getter static Set<ShieldPlayer> shieldPlayers = new HashSet<>();

    /**
     * Returns a ShieldPlayer object for the given UUID
     * @param uuid
     * @return
     */
    static ShieldPlayer getShieldPlayer(UUID uuid) {
        List<ShieldPlayer> playerCache = new CopyOnWriteArrayList<>(shieldPlayers);

        for(ShieldPlayer players : playerCache) {
            if(players.getUuid().equals(uuid)) return players;
        }

        ShieldPlayer newPlayer = new ShieldPlayer(uuid);
        shieldPlayers.add(newPlayer);

        return newPlayer;
    }

}
