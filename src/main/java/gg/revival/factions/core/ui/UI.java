package gg.revival.factions.core.ui;

import gg.revival.factions.core.FC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UI {

    @Getter private FC core;

    public UI(FC core) {
        this.core = core;

        onEnable();
    }

    public void onEnable() {
        new BukkitRunnable() {
            public void run() {
                for(Player players : Bukkit.getOnlinePlayers())
                    core.getUiManager().update(players);
            }
        }.runTaskTimer(core, 0L, 1L);
    }

}
