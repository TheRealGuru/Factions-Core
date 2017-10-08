package gg.revival.factions.core.stats.listener;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.stats.PlayerStats;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class StatsListener implements Listener {

    @Getter private FC core;

    public StatsListener(FC core) {
        this.core = core;
    }

    @EventHandler
    public void onPlayerLoginAttempt(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        if(!core.getConfiguration().statsEnabled) return;

        core.getStats().loadStats(uuid);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(!core.getConfiguration().statsEnabled) return;

        core.getStats().getStats(player.getUniqueId(), stats -> {
            stats.setPlaytime(stats.getCurrentPlaytime());

            core.getStats().saveStats(stats, false);
            core.getStats().getActiveStats().remove(stats);
        });
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(!core.getConfiguration().trackStats || !core.getConfiguration().statsEnabled) return;

        Player killed = event.getEntity();

        if(killed.getKiller() == null) return;
        if(!(killed.getKiller() instanceof Player)) return;

        Player killer = killed.getKiller();

        core.getStats().getStats(killer.getUniqueId(), PlayerStats::addKill);
        core.getStats().getStats(killed.getUniqueId(), PlayerStats::addDeath);
    }

}
