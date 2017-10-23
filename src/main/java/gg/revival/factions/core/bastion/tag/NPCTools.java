package gg.revival.factions.core.bastion.tag;

import com.google.common.collect.ImmutableList;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.core.tools.Permissions;
import gg.revival.factions.obj.FPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCTools {

    @Getter private FC core;

    public NPCTools(FC core) {
        this.core = core;
    }

    /**
     * Spawns a combat-logger for the given Player. Duration is the time (in seconds) the logger should stay alive until it is despawned
     * @param player
     * @param duration
     */
    void spawnLogger(Player player, int duration) {
        if(player.hasPermission(Permissions.CORE_ADMIN) || player.hasPermission(Permissions.CORE_MOD)) return;

        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        List<ItemStack> contents = new ArrayList<>();

        for(ItemStack inventory : player.getInventory().getContents()) {
            if(inventory == null || inventory.getType().equals(Material.AIR)) continue;
            contents.add(inventory);
        }

        for(ItemStack armor : player.getInventory().getArmorContents()) {
            if(armor == null || armor.getType().equals(Material.AIR)) continue;
            contents.add(armor);
        }

        CombatLogger logger = new CombatLogger(player.getUniqueId(), player.getName(), facPlayer.getLocation().getLastLocation(), contents);

        logger.build();

        core.getBastion().getCombatManager().getCombatLoggers().put(player.getUniqueId(), logger);

        logger.getNpc().setFireTicks(player.getFireTicks());
        logger.getNpc().setFallDistance(player.getFallDistance());
        ((LivingEntity)logger.getNpc()).setRemainingAir(player.getRemainingAir());
        ((LivingEntity)logger.getNpc()).setHealth(player.getHealth());
        ((LivingEntity)logger.getNpc()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 257, false, false));

        Bukkit.broadcastMessage(ChatColor.YELLOW + "Combat-Logger: " + ChatColor.RED + player.getName());

        new BukkitRunnable() {
            public void run() {
                if(logger.getNpc() != null && !logger.getNpc().isDead())
                    despawnLogger(logger);
            }
        }.runTaskLater(core, duration * 20L);
    }

    /**
     * Removes a CombatLogger object's logger NPC from the world
     * @param logger
     */
    private void despawnLogger(CombatLogger logger) {
        if(logger.getLocation().getChunk().isLoaded()) {
            logger.destroy();
            core.getBastion().getCombatManager().getCombatLoggers().remove(logger.getUuid());

            return;
        }

        logger.getLocation().getChunk().load();

        new BukkitRunnable() {
            public void run() {
                logger.destroy();
                core.getBastion().getCombatManager().getCombatLoggers().remove(logger.getUuid());
            }
        }.runTaskLater(core, 1L);
    }

    /**
     * Returns true if the given entity is a combat-logger
     * @param entity
     * @return
     */
    boolean isLogger(Entity entity) {
        ImmutableList<CombatLogger> cache = ImmutableList.copyOf(core.getBastion().getCombatManager().getCombatLoggers().values());

        for(CombatLogger loggers : cache) {
            if(loggers.getNpc() != entity) continue;

            return true;
        }

        return false;
    }

    /**
     * Returns a CombatLogger object based on a given Entity
     * @param entity
     * @return
     */
    CombatLogger getLoggerByEntity(Entity entity) {
        ImmutableList<CombatLogger> cache = ImmutableList.copyOf(core.getBastion().getCombatManager().getCombatLoggers().values());

        for(CombatLogger loggers : cache) {
            if(loggers.getNpc() != entity) continue;

            return loggers;
        }

        return null;
    }

    /**
     * Returns a CombatLogger object based on a players UUID
     * @param uuid
     * @return
     */
    CombatLogger getLoggerByUUID(UUID uuid) {
        ImmutableList<CombatLogger> cache = ImmutableList.copyOf(core.getBastion().getCombatManager().getCombatLoggers().values());

        for(CombatLogger loggers : cache) {
            if(!loggers.getUuid().equals(uuid)) continue;

            return loggers;
        }

        return null;
    }

    /**
     * Returns a CombatLogger object based on a username
     * @param name
     * @return
     */
    public CombatLogger getLoggerByName(String name) {
        ImmutableList<CombatLogger> cache = ImmutableList.copyOf(core.getBastion().getCombatManager().getCombatLoggers().values());

        for(CombatLogger loggers : cache) {
            if(!loggers.getDisplayName().equals(name)) continue;

            return loggers;
        }

        return null;
    }

}
