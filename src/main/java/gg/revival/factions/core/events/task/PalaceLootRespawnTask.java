package gg.revival.factions.core.events.task;

import gg.revival.factions.core.events.palace.PalaceManager;
import org.bukkit.scheduler.BukkitRunnable;

public class PalaceLootRespawnTask extends BukkitRunnable implements Runnable {

    @Override
    public void run() {
        PalaceManager.spawnLoot();
    }

}
