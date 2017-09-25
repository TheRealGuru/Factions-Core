package gg.revival.factions.core.bastion.shield;

import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
        ImmutableList<ShieldPlayer> cache = ImmutableList.copyOf(shieldPlayers);

        for(ShieldPlayer players : cache)
            if(players.getUuid().equals(uuid)) return players;

        ShieldPlayer newPlayer = new ShieldPlayer(uuid);
        shieldPlayers.add(newPlayer);

        return newPlayer;
    }

}
