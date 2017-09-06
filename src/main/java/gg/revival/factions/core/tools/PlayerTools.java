package gg.revival.factions.core.tools;

import gg.revival.factions.core.FactionManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerTools
{

    /**
     * Returns true if there is a nearby enemy player
     * @param player The player to check
     * @param distance Distance to check
     * @return Player is nearby
     */
    public static boolean isNearbyEnemy(Player player, int distance)
    {

        for(Entity nearbyEntities : player.getNearbyEntities(distance, distance, distance))
        {
            if(!(nearbyEntities instanceof Player)) continue;

            Player foundPlayer = (Player)nearbyEntities;

            if(FactionManager.isFactionMember(player.getUniqueId(), foundPlayer.getUniqueId()) ||
                    FactionManager.isAllyMember(player.getUniqueId(), foundPlayer.getUniqueId())) continue;

            if(foundPlayer.hasPermission(Permissions.CORE_MOD) || foundPlayer.hasPermission(Permissions.CORE_ADMIN)) continue;

            return true;
        }

        return false;
    }

}
