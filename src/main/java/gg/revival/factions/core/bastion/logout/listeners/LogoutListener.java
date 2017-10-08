package gg.revival.factions.core.bastion.logout.listeners;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.timers.TimerType;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

public class LogoutListener implements Listener {

    @Getter private FC core;

    public LogoutListener(FC core) {
        this.core = core;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDamage(EntityDamageEvent event) {
        if(event.isCancelled()) return;

        if(!(event.getEntity() instanceof Player)) return;

        Player player = (Player)event.getEntity();
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer.isBeingTimed(TimerType.LOGOUT)) {
            facPlayer.removeTimer(TimerType.LOGOUT);
            core.getBastion().getLogoutTask().getSafeloggers().remove(player.getUniqueId());
            core.getBastion().getLogoutTask().getStartingLocations().remove(player.getUniqueId());

            player.sendMessage(ChatColor.RED + "Logout cancelled");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.isCancelled()) return;

        if(event.getDamager() instanceof Player) {
            Player player = (Player)event.getDamager();
            FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

            if(facPlayer.isBeingTimed(TimerType.LOGOUT)) {
                facPlayer.removeTimer(TimerType.LOGOUT);
                core.getBastion().getLogoutTask().getSafeloggers().remove(player.getUniqueId());
                core.getBastion().getLogoutTask().getStartingLocations().remove(player.getUniqueId());

                player.sendMessage(ChatColor.RED + "Logout cancelled");
            }
        }

        if(event.getDamager() instanceof Projectile) {
            ProjectileSource src = ((Projectile) event.getDamager()).getShooter();

            if(src instanceof Player) {
                Player player = (Player)src;
                FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

                if(facPlayer.isBeingTimed(TimerType.LOGOUT)) {
                    facPlayer.removeTimer(TimerType.LOGOUT);
                    core.getBastion().getLogoutTask().getSafeloggers().remove(player.getUniqueId());
                    core.getBastion().getLogoutTask().getStartingLocations().remove(player.getUniqueId());

                    player.sendMessage(ChatColor.RED + "Logout cancelled");
                }
            }
        }
    }

}
