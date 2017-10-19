package gg.revival.factions.core.db.listener;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.obj.FPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.github.paperspigot.event.server.ServerShutdownEvent;

public class DatabaseListener implements Listener {

    @Getter private FC core;

    public DatabaseListener(FC core) {
        this.core = core;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        core.getDatabaseManager().loadTimerData(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());
        core.getDatabaseManager().saveTimerData(facPlayer, false);
    }

    @EventHandler
    public void onServerShutdown(ServerShutdownEvent event) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

            if(facPlayer == null) continue;

            core.getDatabaseManager().saveTimerData(facPlayer, false);
        }
    }

}
