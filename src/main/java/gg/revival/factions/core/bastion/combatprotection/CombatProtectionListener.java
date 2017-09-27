package gg.revival.factions.core.bastion.combatprotection;

import gg.revival.factions.core.tools.Configuration;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CombatProtectionListener implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if(!Configuration.pvpProtEnabled) return;

        CombatProtection.giveProtection(player, Configuration.pvpProtDuration);
    }

    @EventHandler
    public void onPlayerChangeWorlds(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World from = event.getFrom();

        if(!from.getEnvironment().equals(World.Environment.NORMAL)) return;

        if(!Configuration.pvpSafetyEnabled) return;

        CombatProtection.giveSafety(player, Configuration.pvpSafetyDuration);
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        if(!(event.getPotion().getShooter() instanceof Player)) return;

        for(PotionEffect effects : event.getPotion().getEffects()) {
            if(
                    !effects.getType().equals(PotionEffectType.POISON) &&
                    !effects.getType().equals(PotionEffectType.SLOW) &&
                    !effects.getType().equals(PotionEffectType.WEAKNESS))

                return;
        }

        List<Entity> affectedEntities = new CopyOnWriteArrayList<>(event.getAffectedEntities());

        for(Entity entities : affectedEntities) {
            if(!(entities instanceof Player)) return;

            Player affectedPlayer = (Player)entities;

            if((CombatProtection.hasProt(affectedPlayer) && Configuration.pvpProtEnabled) || (CombatProtection.hasSafety(affectedPlayer) && Configuration.pvpSafetyEnabled))
                event.getAffectedEntities().remove(entities);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.isCancelled())
            return;

        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();

        if(damaged instanceof Player && damager instanceof Player && damaged != damager) {
            Player playerDamaged = (Player)damaged;
            Player playerDamager = (Player)damager;

            if(CombatProtection.hasSafety(playerDamaged) && Configuration.pvpSafetyEnabled) {
                playerDamager.sendMessage(ChatColor.RED + "You can not attack players with PvP safety");
                event.setCancelled(true);
                return;
            }

            if(CombatProtection.hasSafety(playerDamager) && Configuration.pvpSafetyEnabled) {
                CombatProtection.takeSafety(playerDamager);
                event.setCancelled(true);
                return;
            }

            if(CombatProtection.hasProt(playerDamaged) && Configuration.pvpProtEnabled) {
                playerDamager.sendMessage(ChatColor.RED + "You can not attack players with PvP protection");
                event.setCancelled(true);
                return;
            }

            if(CombatProtection.hasProt(playerDamager) && Configuration.pvpProtEnabled) {
                playerDamager.sendMessage(ChatColor.RED + "You have PvP protection. Type '/pvp enable' to enable combat");
                event.setCancelled(true);
                return;
            }
        }

        if(damaged instanceof Player && damager instanceof Projectile) {
            Projectile projectile = (Projectile)damager;

            if(!(projectile.getShooter() instanceof Player)) return;

            Player playerDamager = (Player)projectile.getShooter();
            Player playerDamaged = (Player)damaged;

            if(CombatProtection.hasSafety(playerDamaged) && Configuration.pvpSafetyEnabled) {
                playerDamager.sendMessage(ChatColor.RED + "You can not attack players with PvP safety");
                event.setCancelled(true);
                return;
            }

            if(CombatProtection.hasSafety(playerDamager) && Configuration.pvpSafetyEnabled) {
                CombatProtection.takeSafety(playerDamager);
                event.setCancelled(true);
                return;
            }

            if(CombatProtection.hasProt(playerDamaged) && Configuration.pvpProtEnabled) {
                playerDamager.sendMessage(ChatColor.RED + "You can not attack players with PvP protection");
                event.setCancelled(true);
                return;
            }

            if(CombatProtection.hasProt(playerDamager) && Configuration.pvpProtEnabled) {
                playerDamager.sendMessage(ChatColor.RED + "You have PvP protection. Type '/pvp enable' to enable combat");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerPortalEvent event) {
        Player player = event.getPlayer();

        if(CombatProtection.hasProt(player) && Configuration.pvpProtEnabled) {
            if(!event.getTo().getWorld().getEnvironment().equals(World.Environment.THE_END))
                player.sendMessage(ChatColor.RED + "You can not change worlds while you have PvP protection");

            event.setCancelled(true);
        }
    }

}
