package gg.revival.factions.core.bastion.shield;

import gg.revival.factions.core.FC;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class Shield {

    @Getter
    static Set<ShieldPlayer> shieldPlayers = new HashSet<>();

    public static ShieldPlayer getShieldPlayer(UUID uuid)
    {
        List<ShieldPlayer> playerCache = new CopyOnWriteArrayList<>(shieldPlayers);

        for(ShieldPlayer players : playerCache)
        {
            if(players.getUuid().equals(uuid)) return players;
        }

        ShieldPlayer newPlayer = new ShieldPlayer(uuid);
        shieldPlayers.add(newPlayer);

        return newPlayer;
    }

    public static void onEnable()
    {
        loadListeners();
    }

    public static void loadListeners()
    {
        Bukkit.getPluginManager().registerEvents(new ShieldListener(), FC.getFactionsCore());
    }

}
