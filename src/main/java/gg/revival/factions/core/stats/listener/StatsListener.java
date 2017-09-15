package gg.revival.factions.core.stats.listener;

import gg.revival.factions.core.stats.PlayerStats;
import gg.revival.factions.core.stats.Stats;
import gg.revival.factions.core.tools.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StatsListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(!Configuration.statsEnabled) return;

        if(Stats.getActiveStats().contains(player.getUniqueId())) return;
        Stats.loadStats(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(!Configuration.statsEnabled) return;

        PlayerStats stats = Stats.getStats(player.getUniqueId());
        stats.setPlaytime(stats.getNewPlaytime());

        Stats.saveStats(Stats.getStats(player.getUniqueId()), false);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(!Configuration.trackStats || !Configuration.statsEnabled) return;

        if(!(event.getEntity() instanceof Player)) return;

        Player killed = (Player)event.getEntity();

        if(killed.getKiller() == null) return;
        if(!(killed.getKiller() instanceof Player)) return;

        Player killer = (Player)killed.getKiller();

        PlayerStats killerStats = Stats.getStats(killer.getUniqueId());
        PlayerStats killedStats = Stats.getStats(killed.getUniqueId());

        killedStats.addDeath();
        killerStats.addKill();
    }

}
