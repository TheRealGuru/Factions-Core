package gg.revival.factions.core.stats.listener;

import gg.revival.factions.core.stats.PlayerStats;
import gg.revival.factions.core.stats.Stats;
import gg.revival.factions.core.tools.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class StatsListener implements Listener {

    @EventHandler
    public void onPlayerLoginAttempt(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        if(!Configuration.statsEnabled) return;
        if(Stats.getActiveStats().contains(uuid)) return;

        Stats.loadStats(uuid, true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(!Configuration.statsEnabled) return;

        PlayerStats stats = Stats.getStats(player.getUniqueId());
        stats.setPlaytime(stats.getNewPlaytime());

        Stats.saveStats(stats, false);
        Stats.getActiveStats().remove(stats);
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
