package gg.revival.factions.core.deathbans.listener;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.tools.Permissions;
import lombok.Getter;
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

    @Getter private FC core;

    public DeathbanListener(FC core) {
        this.core = core;
    }

    @EventHandler
    public void onPlayerLoginAttempt(AsyncPlayerPreLoginEvent event) {
        if(!core.getConfiguration().deathbansEnabled)
            return;

        UUID uuid = event.getUniqueId();

        core.getDeathbans().getActiveDeathban(uuid, death -> {
            if(death == null) return;
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, core.getDeathbans().getDeathbanMessage(death));

            if(Bukkit.getPlayer(uuid) != null || event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED))
                Bukkit.getPlayer(uuid).kickPlayer(core.getDeathbans().getDeathbanMessage(death));
        });
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        final String originalDeathMessage = event.getDeathMessage();

        event.setDeathMessage(core.getDeathbans().getDeathMessages().getDeathMessage(player));

        if(player.hasPermission(Permissions.CORE_ADMIN) || player.hasPermission(Permissions.CORE_MOD)) return;

        if(core.getConfiguration().deathbansEnabled)
            core.getDeathbans().getDeathbanDurationByLocation(player.getUniqueId(), player.getLocation(), duration -> core.getDeathbans().deathbanPlayer(player.getUniqueId(), originalDeathMessage, duration));
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();
        double damage = event.getFinalDamage();

        if(!(damaged instanceof Player)) return;

        Player player = (Player)damaged;

        if((player.getHealth() - damage) > 0.0 || player.isDead()) return;

        Bukkit.broadcastMessage(core.getDeathbans().getDeathMessages().getDeathMessage(player, damager));
    }

}
