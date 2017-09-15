package gg.revival.factions.core.deathbans.listener;

import gg.revival.factions.core.deathbans.Deathbans;
import gg.revival.factions.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class DeathbanListener implements Listener {

    @EventHandler
    public void onPlayerLoginAttempt(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        Deathbans.getActiveDeathban(uuid, death -> {
            if(death == null) return;

            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Deathbans.getDeathbanMessage(death));

            if(Bukkit.getPlayer(death.getKilled()) != null && Bukkit.getPlayer(death.getKilled()).isOnline())
                Bukkit.getPlayer(death.getKilled()).kickPlayer(Deathbans.getDeathbanMessage(death));
        });
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(!(event.getEntity() instanceof Player)) return;

        Player player = (Player)event.getEntity();

        if(player.hasPermission(Permissions.CORE_ADMIN) || player.hasPermission(Permissions.CORE_MOD)) return;

        // TODO: Get deathban reason here
        Deathbans.deathbanPlayer(player.getUniqueId(), "Insert death reason here hehe", Deathbans.getDeathbanDurationByLocation(player.getUniqueId(), player.getLocation()));
    }

}
