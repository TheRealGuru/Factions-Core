package gg.revival.factions.core.events.task;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.events.chests.ClaimChest;
import gg.revival.factions.core.events.chests.ClaimChestType;
import gg.revival.factions.core.events.chests.EventChest;
import gg.revival.factions.core.events.chests.PalaceChest;
import lombok.Getter;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class ChestTask extends BukkitRunnable implements Runnable {

    @Getter private FC core;

    public ChestTask(FC core) {
        this.core = core;
    }

    @Override
    public void run() {
        if(core.getEvents().getChestManager() == null || core.getEvents().getChestManager().getLoadedChests().isEmpty()) return;

        for(EventChest eventChest : core.getEvents().getChestManager().getLoadedChests()) {
            Location fixedLocation = new Location(
                    eventChest.getLocation().getWorld(), eventChest.getLocation().getBlockX() + 0.5, eventChest.getLocation().getBlockY() + 0.5, eventChest.getLocation().getBlockZ() + 0.5);

            if(eventChest instanceof PalaceChest) {
                PalaceChest palaceChest = (PalaceChest)eventChest;

                if(palaceChest.isRecentlyLooted()) return;

                palaceChest.getLocation().getWorld().spigot().playEffect(fixedLocation, Effect.HAPPY_VILLAGER, 0, 0, 0.5f, 0.5f, 0.5f, 0.001f, 15, 5);
            }

            if(eventChest instanceof ClaimChest) {
                ClaimChest claimChest = (ClaimChest)eventChest;

                if(claimChest.getType().equals(ClaimChestType.BREWING))
                    claimChest.getLocation().getWorld().spigot().playEffect(fixedLocation, Effect.POTION_SWIRL, 0, 0, 0.5f, 0.5f, 0.5f, 0.01f, 30, 10);

                if(claimChest.getType().equals(ClaimChestType.COMBAT)) {
                    claimChest.getLocation().getWorld().spigot().playEffect(fixedLocation, Effect.CRIT, 0, 0, 0.5f, 0.5f, 0.5f, 0.01f, 15, 10);
                    claimChest.getLocation().getWorld().spigot().playEffect(fixedLocation, Effect.MAGIC_CRIT, 0, 0, 0.5f, 0.5f, 0.5f, 0.01f, 15, 10);
                }

                if(claimChest.getType().equals(ClaimChestType.RARE))
                    claimChest.getLocation().getWorld().spigot().playEffect(fixedLocation, Effect.HAPPY_VILLAGER, 0, 0, 0.5f, 0.5f, 0.5f, 0.01f, 15, 10);
            }
        }
    }

}
