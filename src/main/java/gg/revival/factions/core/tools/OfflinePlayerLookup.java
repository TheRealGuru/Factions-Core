package gg.revival.factions.core.tools;

import gg.revival.factions.core.FC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class OfflinePlayerLookup
{

    public static void getOfflinePlayerByName(String query, OfflinePlayerCallback callback)
    {
        if(Bukkit.getPlayer(query) != null)
        {
            Player player = Bukkit.getPlayer(query);
            UUID uuid = player.getUniqueId();
            String username = player.getName();

            callback.onQueryDone(uuid, username);

            return;
        }

        new BukkitRunnable() {
            public void run() {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(query);

                if(offlinePlayer != null)
                {
                    UUID uuid = offlinePlayer.getUniqueId();
                    String username = offlinePlayer.getName();

                    new BukkitRunnable() {
                        public void run() {
                            callback.onQueryDone(uuid, username);
                        }
                    }.runTask(FC.getFactionsCore());
                }

                else
                {
                    new BukkitRunnable() {
                        public void run() {
                            callback.onQueryDone(null, null);
                        }
                    }.runTask(FC.getFactionsCore());
                }
            }
        }.runTaskAsynchronously(FC.getFactionsCore());
    }

    public static void getOfflinePlayerByUUID(UUID query, OfflinePlayerCallback callback)
    {
        if(Bukkit.getPlayer(query) != null)
        {
            Player player = Bukkit.getPlayer(query);
            UUID uuid = player.getUniqueId();
            String username = player.getName();

            callback.onQueryDone(uuid, username);

            return;
        }

        new BukkitRunnable() {
            public void run() {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(query);

                if(offlinePlayer != null)
                {
                    UUID uuid = offlinePlayer.getUniqueId();
                    String username = offlinePlayer.getName();

                    new BukkitRunnable() {
                        public void run() {
                            callback.onQueryDone(uuid, username);
                        }
                    }.runTask(FC.getFactionsCore());
                }

                else
                {
                    new BukkitRunnable() {
                        public void run() {
                            callback.onQueryDone(null, null);
                        }
                    }.runTask(FC.getFactionsCore());
                }
            }
        }.runTaskAsynchronously(FC.getFactionsCore());
    }

}
