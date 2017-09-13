package gg.revival.factions.core.ui;

import gg.revival.factions.core.FC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UI {

    public static void onEnable() {
        new BukkitRunnable() {
            public void run() {
                for(Player players : Bukkit.getOnlinePlayers())
                    UIManager.update(players);
            }
        }.runTaskTimer(FC.getFactionsCore(), 0L, 1L);
    }

}
