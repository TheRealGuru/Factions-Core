package gg.revival.factions.core.bastion.tag;

import gg.revival.factions.claims.Claim;
import gg.revival.factions.claims.ClaimManager;
import gg.revival.factions.claims.ServerClaimType;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.core.stats.PlayerStats;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.obj.Faction;
import gg.revival.factions.obj.PlayerFaction;
import gg.revival.factions.obj.ServerFaction;
import gg.revival.factions.timers.TimerType;
import gg.revival.factions.tools.Messages;
import lombok.Getter;
import org.bukkit.*;
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
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;

public class CombatListener implements Listener {

    @Getter private FC core;

    public CombatListener(FC core) {
        this.core = core;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer.isBeingTimed(TimerType.TAG)) {
            facPlayer.removeTimer(TimerType.TAG);
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();

        if(core.getBastion().getNpcTools().isLogger(entity))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if(event.isCancelled())
            return;

        Player player = event.getPlayer();
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(!event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) return;

        Location to = event.getTo();
        Claim claim = ClaimManager.getClaimAt(to, true);

        if(claim != null && claim.getClaimOwner() instanceof ServerFaction) {
            ServerFaction serverFaction = (ServerFaction)claim.getClaimOwner();

            if(serverFaction.getType().equals(ServerClaimType.SAFEZONE) && facPlayer.isBeingTimed(TimerType.TAG)) {
                ItemStack enderpearl = new ItemStack(Material.ENDER_PEARL);
                player.getInventory().addItem(enderpearl);

                facPlayer.removeTimer(TimerType.ENDERPEARL);

                player.sendMessage(ChatColor.RED + "You can not use enderpearls in this claim while combat-tagged");

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        core.getBastion().getCombatManager().hasLoggerEntry(player.getUniqueId(), isLogger -> {
            if(isLogger) {
                FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

                if(facPlayer.isBeingTimed(TimerType.TAG))
                    facPlayer.removeTimer(TimerType.TAG);

                player.teleport(core.getLocations().getSpawnLocation());
                core.getPlayerTools().cleanupPlayer(player);

                core.getBastion().getCombatManager().clearLoggerEntry(player.getUniqueId());
            }
        });
    }

    @EventHandler
    public void onPlayerReconnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CombatLogger logger = core.getBastion().getNpcTools().getLoggerByUUID(player.getUniqueId());

        if(logger == null || logger.isDead) return;

        core.getLog().log("Combatlogger was found");

        player.teleport(logger.getNpc().getLocation());
        player.setHealth(((LivingEntity)logger.getNpc()).getHealth());
        player.setFireTicks(logger.getNpc().getFireTicks());
        player.setFallDistance(logger.getNpc().getFallDistance());
        player.setRemainingAir(((LivingEntity) logger.getNpc()).getRemainingAir());

        logger.destroy();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(core.getBastion().getLogoutTask().getSafeloggers().contains(player.getUniqueId())) return;
        if(player.isDead() || player.getHealth() <= 0.0) return;

        if(facPlayer.getLocation() != null &&
                facPlayer.getLocation().getCurrentClaim() != null &&
                facPlayer.getLocation().getCurrentClaim().getClaimOwner() instanceof ServerFaction) {

            ServerFaction serverFaction = (ServerFaction)facPlayer.getLocation().getCurrentClaim().getClaimOwner();

            if(serverFaction.getType().equals(ServerClaimType.SAFEZONE)) return;
        }

        if(facPlayer.isBeingTimed(TimerType.TAG)) {
            core.getBastion().getNpcTools().spawnLogger(player, core.getConfiguration().loggerDuration);
            return;
        }

        if(core.getPlayerTools().isNearbyEnemy(player, core.getConfiguration().loggerEnemyDistance)) {
            core.getBastion().getNpcTools().spawnLogger(player, core.getConfiguration().loggerDuration);
            return;
        }

        if(player.getFireTicks() > 0 || player.getFallDistance() > 0 || player.getRemainingAir() != player.getMaximumAir()) {
            core.getBastion().getNpcTools().spawnLogger(player, core.getConfiguration().loggerDuration);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLoggerDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.isCancelled())
            return;

        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();

        if(!core.getBastion().getNpcTools().isLogger(damaged)) return;

        CombatLogger logger = core.getBastion().getNpcTools().getLoggerByEntity(damaged);

        if(damager instanceof Player) {
            Player playerDamager = (Player)damager;

            if(FactionManager.isFactionMember(playerDamager.getUniqueId(), logger.getUuid())) {
                event.setCancelled(true);
                return;
            }

            core.getBastion().getCombatManager().tagPlayer(playerDamager, TagReason.ATTACKER);
        }

        if(damager instanceof Projectile) {
            Projectile projectile = (Projectile)damager;
            ProjectileSource source = projectile.getShooter();

            if(source instanceof Player) {
                Player playerDamager = (Player)source;

                if(FactionManager.isFactionMember(playerDamager.getUniqueId(), logger.getUuid())) {
                    event.setCancelled(true);
                    return;
                }

                core.getBastion().getCombatManager().tagPlayer(playerDamager, TagReason.ATTACKER);
            }
        }

        new BukkitRunnable() {
            public void run() {
                damaged.teleport(logger.getLocation());
                damaged.setVelocity(damaged.getVelocity().setX(0).setY(0).setZ(0));
            }
        }.runTaskLater(core, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.isCancelled())
            return;

        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();

        if(damaged instanceof Player && damager instanceof Player) {
            Player playerDamaged = (Player)damaged;
            Player playerDamager = (Player)damager;

            if(playerDamaged.getUniqueId().equals(playerDamager.getUniqueId())) return;

            core.getBastion().getCombatManager().tagPlayer(playerDamaged, TagReason.ATTACKED);
            core.getBastion().getCombatManager().tagPlayer(playerDamager, TagReason.ATTACKER);
        }

        if(damaged instanceof Player && damager instanceof Projectile) {
            Player playerDamaged = (Player)damaged;
            Projectile projectile = (Projectile)damager;

            if(!(projectile.getShooter() instanceof Player)) return;

            Player playerDamager = (Player)projectile.getShooter();

            if(playerDamaged.getUniqueId().equals(playerDamager.getUniqueId())) return;

            core.getBastion().getCombatManager().tagPlayer(playerDamaged, TagReason.ATTACKED);
            core.getBastion().getCombatManager().tagPlayer(playerDamager, TagReason.ATTACKER);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if(!core.getBastion().getNpcTools().isLogger(entity)) return;

        CombatLogger logger = core.getBastion().getNpcTools().getLoggerByEntity(entity);

        logger.setDead(true);

        if(!logger.getInventoryContents().isEmpty()) {
            for(ItemStack contents : logger.getInventoryContents())
                event.getDrops().add(contents);
        }

        core.getDeathbans().getDeathbanDurationByLocation(logger.getUuid(), logger.getLocation(), duration -> core.getDeathbans().deathbanPlayer(logger.getUuid(), "Combat Logger Slain", duration));
        core.getBastion().getCombatManager().creatLoggerEntry(logger.getUuid());

        Faction faction = FactionManager.getFactionByPlayer(logger.getUuid());

        if(faction != null && faction instanceof PlayerFaction) {
            PlayerFaction playerFaction = (PlayerFaction)faction;

            playerFaction.setDtr(playerFaction.getDtr().subtract(BigDecimal.valueOf(1.0)));
            playerFaction.setUnfreezeTime(gg.revival.factions.tools.Configuration.DTR_FREEZE_TIME);
            playerFaction.sendMessage(Messages.memberDeath(logger.getDisplayName())); // This is kinda shitty, find a way to replace this?
        }

        if(event.getEntity().getKiller() != null)
            Bukkit.broadcastMessage(core.getDeathbans().getDeathMessages().getPrefix() + ChatColor.GOLD + logger.getDisplayName() + ChatColor.RED + "'s combat-logger has been slain by " + ChatColor.GOLD + event.getEntity().getKiller().getName());
        else
            Bukkit.broadcastMessage(core.getDeathbans().getDeathMessages().getPrefix() + ChatColor.GOLD + logger.getDisplayName() + ChatColor.RED + "'s combat-logger has been slain");

        core.getStats().getStats(logger.getUuid(), PlayerStats::addDeath);

        if(event.getEntity().getKiller() instanceof Player) {
            Player killer = event.getEntity().getKiller();
            core.getStats().getStats(killer.getUniqueId(), PlayerStats::addKill);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();

        if(event.isCancelled()) return;

        if(core.getBastion().getCombatManager().getCombatLoggers().isEmpty()) return;

        for(CombatLogger logger : core.getBastion().getCombatManager().getCombatLoggers().values()) {
            if(!logger.getLocation().getChunk().equals(chunk)) continue;

            event.setCancelled(true);
            break;
        }
    }
}
