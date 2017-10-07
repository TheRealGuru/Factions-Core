package gg.revival.factions.core.tools;

import gg.revival.factions.core.FactionManager;
import gg.revival.factions.obj.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerTools {

    public static void cleanupPlayer(Player player) {
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExhaustion(0);
        player.setFallDistance(0);
        player.setFireTicks(0);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }

    /**
     * Returns true if there is a nearby enemy player
     * @param player The player to check
     * @param distance Distance to check
     * @return Player is nearby
     */
    public static boolean isNearbyEnemy(Player player, int distance) {
        for(Entity nearbyEntities : player.getNearbyEntities(distance, distance, distance)) {
            if(!(nearbyEntities instanceof Player)) continue;

            Player foundPlayer = (Player)nearbyEntities;

            if(FactionManager.isFactionMember(player.getUniqueId(), foundPlayer.getUniqueId()) ||
                    FactionManager.isAllyMember(player.getUniqueId(), foundPlayer.getUniqueId())) continue;

            if(foundPlayer.hasPermission(Permissions.CORE_MOD) || foundPlayer.hasPermission(Permissions.CORE_ADMIN)) continue;

            return true;
        }

        return false;
    }

    /**
     * Returns a Set of nearby faction members
     * @param playerFaction
     * @param location
     * @return
     */
    public static Set<UUID> getNearbyFactionMembers(PlayerFaction playerFaction, Location location) {
        Set<UUID> result = new HashSet<>();

        for(UUID onlineRoster : playerFaction.getRoster(true)) {
            Player player = Bukkit.getPlayer(onlineRoster);

            if(!player.getWorld().equals(location.getWorld())) continue;

            if(player.getLocation().distanceSquared(location) > Configuration.loggerEnemyDistance) continue;

            result.add(player.getUniqueId());
        }

        return result;
    }

    /**
     * Send players with given permission a message
     * @param permission
     * @param message
     */
    public static void sendPermissionMessage(String permission, String message) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!player.hasPermission(permission)) continue;
            player.sendMessage(message);
        }
    }

}
