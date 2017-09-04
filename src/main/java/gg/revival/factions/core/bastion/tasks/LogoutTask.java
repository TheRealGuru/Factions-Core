package gg.revival.factions.core.bastion.tasks;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.timers.TimerType;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class LogoutTask
{

    /**
     * Contains every player who is actively safelogging. Players are stored in this 1 extra second after they're disconnected to bypass the combat-logger checks
     */
    @Getter static List<UUID> safeloggers = new ArrayList<>();

    /**
     * Contains every player who is currently performing a /logout's starting location
     */
    @Getter static Map<UUID, Location> startingLocations = new HashMap<>();

    /**
     * Get a players /logout starting location
     * @param uuid The player UUID
     * @return Players starting location
     */
    public static Location getStartingLocation(UUID uuid)
    {
        if(startingLocations.containsKey(uuid))
            return startingLocations.get(uuid);

        return null;
    }

    /**
     * Checks to make sure every player performing a /logout has not moved too far
     */
    public static void checkLocations()
    {
        List<UUID> userCache = new CopyOnWriteArrayList<>(startingLocations.keySet());

        for(UUID uuid : userCache)
        {
            if(Bukkit.getPlayer(uuid) == null)
            {
                startingLocations.remove(uuid);
                continue;
            }

            Player player = Bukkit.getPlayer(uuid);
            FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());
            Location current = player.getLocation();
            Location expected = getStartingLocation(player.getUniqueId());

            if(expected.distance(current) >= 1.0 || expected.getWorld() != current.getWorld())
            {
                if(facPlayer.isBeingTimed(TimerType.LOGOUT))
                {
                    startingLocations.remove(player.getUniqueId());
                    safeloggers.remove(player.getUniqueId());
                    facPlayer.removeTimer(TimerType.LOGOUT);
                    player.sendMessage(ChatColor.RED + "Logout cancelled");
                }
            }
        }
    }

    /**
     * Logs the player out, essentially just a kick with a kind message
     * @param uuid The player UUID
     */
    public static void logoutPlayer(UUID uuid)
    {
        Player player = Bukkit.getPlayer(uuid);

        if(player == null)
        {
            startingLocations.remove(uuid);
            return;
        }

        player.kickPlayer(ChatColor.GREEN + "Successfully logged out");

        startingLocations.remove(player.getUniqueId());

        new BukkitRunnable() {
            public void run()
            {
                safeloggers.remove(uuid);
            }
        }.runTaskLater(FC.getFactionsCore(), 20L);
    }

}
