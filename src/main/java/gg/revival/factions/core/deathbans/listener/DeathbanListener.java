package gg.revival.factions.core.deathbans.listener;

import gg.revival.factions.core.deathbans.DeathMessages;
import gg.revival.factions.core.deathbans.Deathbans;
import gg.revival.factions.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class DeathbanListener implements Listener {

    @EventHandler
    public void onPlayerLoginAttempt(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        Deathbans.getActiveDeathban(uuid, true, death -> {
            if(death == null) return;
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Deathbans.getDeathbanMessage(death));
        });
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(!(event.getEntity() instanceof Player)) return;

        Player player = (Player)event.getEntity();
        final String originalDeathMessage = event.getDeathMessage();

        event.setDeathMessage(DeathMessages.getDeathMessage(player));

        if(player.hasPermission(Permissions.CORE_ADMIN) || player.hasPermission(Permissions.CORE_MOD)) return;

        Deathbans.getDeathbanDurationByLocation(player.getUniqueId(), player.getLocation(), duration -> Deathbans.deathbanPlayer(player.getUniqueId(), originalDeathMessage, duration));
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();
        double damage = event.getFinalDamage();

        if(!(damaged instanceof Player)) return;

        Player player = (Player)damaged;

        if((player.getHealth() - damage) > 0.0 || player.isDead()) return;

        Bukkit.broadcastMessage(DeathMessages.getDeathMessage(player, damager));
    }

}
