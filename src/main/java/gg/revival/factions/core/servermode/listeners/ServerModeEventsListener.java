package gg.revival.factions.core.servermode.listeners;

import gg.revival.factions.core.servermode.ServerMode;
import gg.revival.factions.core.servermode.ServerState;
import gg.revival.factions.core.tools.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class ServerModeEventsListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if(ServerMode.getCurrentState().equals(ServerState.EOTW_CLOSED)) {
            if(player.hasPermission(Permissions.CORE_ADMIN) || player.hasPermission(Permissions.CORE_MOD)) return;
            player.kickPlayer(ChatColor.RED + "The End of the World is currently underway. See you next map!");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(ServerMode.getCurrentState().equals(ServerState.EOTW_CLOSED) && !player.hasPlayedBefore()) {
            if(player.hasPermission(Permissions.CORE_ADMIN) || player.hasPermission(Permissions.CORE_MOD)) return;
            player.kickPlayer(ChatColor.RED + "The End of the World is currently underway. See you next map!");
        }
    }

}
