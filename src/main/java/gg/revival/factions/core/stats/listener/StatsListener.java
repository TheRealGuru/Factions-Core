package gg.revival.factions.core.stats.listener;

import gg.revival.factions.core.stats.StatsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StatsListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(StatsManager.getActiveStats().contains(player.getUniqueId())) return;
        StatsManager.loadStats(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        StatsManager.saveStats(StatsManager.getStats(player.getUniqueId()), false);
    }

}
