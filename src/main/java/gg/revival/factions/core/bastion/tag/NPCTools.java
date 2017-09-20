package gg.revival.factions.core.bastion.tag;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCTools {

    /**
     * Spawns a combat-logger for the given Player. Duration is the time (in seconds) the logger should stay alive until it is despawned
     * @param player
     * @param duration
     */
    public static void spawnLogger(Player player, int duration) {
        if(player.hasPermission(Permissions.CORE_ADMIN) || player.hasPermission(Permissions.CORE_MOD)) return;

        List<ItemStack> contents = new ArrayList<>();

        for(ItemStack inventory : player.getInventory().getContents()) {
            if(inventory == null || inventory.getType().equals(Material.AIR)) continue;
            contents.add(inventory);
        }

        for(ItemStack armor : player.getInventory().getArmorContents()) {
            if(armor == null || armor.getType().equals(Material.AIR)) continue;
            contents.add(armor);
        }

        CombatLogger logger = new CombatLogger(player.getUniqueId(), player.getName(), player.getLocation(), contents);

        logger.build();

        CombatManager.getCombatLoggers().put(player.getUniqueId(), logger);

        logger.getNpc().setFireTicks(player.getFireTicks());
        logger.getNpc().setFallDistance(player.getFallDistance());
        ((LivingEntity)logger.getNpc()).setHealth(player.getHealth());

        Bukkit.broadcastMessage(ChatColor.YELLOW + "Combat-Logger: " + ChatColor.RED + player.getName());

        new BukkitRunnable()
        {
            public void run()
            {
                if(logger.getNpc() != null && !logger.getNpc().isDead())
                    despawnLogger(logger);
            }
        }.runTaskLater(FC.getFactionsCore(), duration * 20L);
    }

    /**
     * Removes a CombatLogger object's logger NPC from the world
     * @param logger
     */
    public static void despawnLogger(CombatLogger logger) {
        logger.destroy();
        CombatManager.getCombatLoggers().remove(logger.getUuid());
    }

    /**
     * Returns true if the given entity is a combat-logger
     * @param entity
     * @return
     */
    public static boolean isLogger(Entity entity) {
        for(CombatLogger loggers : CombatManager.getCombatLoggers().values()) {
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
    public static CombatLogger getLoggerByEntity(Entity entity) {
        for(CombatLogger loggers : CombatManager.getCombatLoggers().values()) {
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
    public static CombatLogger getLoggerByUUID(UUID uuid) {
        for(CombatLogger loggers : CombatManager.getCombatLoggers().values()) {
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
    public static CombatLogger getLoggerByName(String name) {
        for(CombatLogger loggers : CombatManager.getCombatLoggers().values()) {
            if(!loggers.getDisplayName().equals(name)) continue;

            return loggers;
        }

        return null;
    }

}
