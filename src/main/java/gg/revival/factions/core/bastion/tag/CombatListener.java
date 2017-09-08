package gg.revival.factions.core.bastion.tag;

import gg.revival.factions.claims.Claim;
import gg.revival.factions.claims.ClaimManager;
import gg.revival.factions.claims.ServerClaimType;
import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.core.bastion.logout.tasks.LogoutTask;
import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.core.tools.PlayerTools;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.obj.Faction;
import gg.revival.factions.obj.PlayerFaction;
import gg.revival.factions.obj.ServerFaction;
import gg.revival.factions.timers.TimerType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

public class CombatListener implements Listener
{

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer.isBeingTimed(TimerType.TAG))
        {
            facPlayer.removeTimer(TimerType.TAG);
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event)
    {
        Entity entity = event.getRightClicked();

        if(NPCTools.isLogger(entity))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        if(event.isCancelled())
            return;

        Player player = event.getPlayer();
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(!event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) return;

        Location to = event.getTo();
        Claim claim = ClaimManager.getClaimAt(to, true);

        if(claim != null && claim.getClaimOwner() instanceof ServerFaction)
        {
            ServerFaction serverFaction = (ServerFaction)claim.getClaimOwner();

            if(serverFaction.getType().equals(ServerClaimType.SAFEZONE) && facPlayer.isBeingTimed(TimerType.TAG))
            {
                ItemStack enderpearl = new ItemStack(Material.ENDER_PEARL);
                player.getInventory().addItem(enderpearl);

                facPlayer.removeTimer(TimerType.ENDERPEARL);

                player.sendMessage(ChatColor.RED + "You can not use enderpearls in this claim while combat-tagged");

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        CombatLogger logger = NPCTools.getLoggerByUUID(player.getUniqueId());

        if(logger == null || logger.isDead) return;

        player.teleport(logger.getNpc().getLocation());
        player.setHealth(((LivingEntity)logger.getNpc()).getHealth());
        player.setFireTicks(logger.getNpc().getFireTicks());
        player.setFallDistance(logger.getNpc().getFallDistance());

        logger.destroy();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());
        final Location location = event.getPlayer().getLocation();

        if(LogoutTask.getSafeloggers().contains(player.getUniqueId())) return;
        if(player.isDead() || player.getHealth() <= 0.0) return;

        if(facPlayer.isBeingTimed(TimerType.TAG))
        {
            NPCTools.spawnLogger(player, Configuration.loggerDuration);
            return;
        }

        if(PlayerTools.isNearbyEnemy(player, Configuration.loggerEnemyDistance))
        {
            NPCTools.spawnLogger(player, Configuration.loggerDuration);
            return;
        }

        if(player.getFireTicks() > 0 || player.getFallDistance() > 0)
        {
            NPCTools.spawnLogger(player, Configuration.loggerDuration);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        if(event.isCancelled())
            return;

        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();

        if(damaged instanceof Player && damager instanceof Player)
        {
            Player playerDamaged = (Player)damaged;
            Player playerDamager = (Player)damager;

            if(playerDamaged.getUniqueId().equals(playerDamager.getUniqueId())) return;

            CombatManager.tagPlayer(playerDamaged, TagReason.ATTACKED);
            CombatManager.tagPlayer(playerDamager, TagReason.ATTACKER);
        }

        if(damaged instanceof Player && damager instanceof Projectile)
        {
            Player playerDamaged = (Player)damaged;
            Projectile projectile = (Projectile)damager;

            if(!(projectile.getShooter() instanceof Player)) return;

            Player playerDamager = (Player)projectile.getShooter();

            if(playerDamaged.getUniqueId().equals(playerDamager.getUniqueId())) return;

            CombatManager.tagPlayer(playerDamaged, TagReason.ATTACKED);
            CombatManager.tagPlayer(playerDamager, TagReason.ATTACKER);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event)
    {
        Entity entity = event.getEntity();

        if(!NPCTools.isLogger(entity)) return;

        CombatLogger logger = NPCTools.getLoggerByEntity(entity);

        logger.setDead(true);

        if(!logger.getInventoryContents().isEmpty())
        {
            for(ItemStack contents : logger.getInventoryContents())
            {
                event.getDrops().add(contents);
            }
        }

        // TODO: Deathban player here

        Faction faction = FactionManager.getFactionByPlayer(logger.getUuid());

        if(faction != null && faction instanceof PlayerFaction)
        {
            PlayerFaction playerFaction = (PlayerFaction)faction;

            playerFaction.setDtr(playerFaction.getDtr().subtract(BigDecimal.valueOf(1.0)));
            playerFaction.setUnfreezeTime(gg.revival.factions.tools.Configuration.DTR_FREEZE_TIME);

            // TODO: Send member death message
        }

        if(event.getEntity().getKiller() instanceof Player)
        {
            // TODO: Send combat logger death message
        }

        else
        {
            // TODO: Send combat logger death messages
        }
    }

}
