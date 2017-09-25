package gg.revival.factions.core.events.task;

import gg.revival.factions.core.events.loot.EventChest;
import gg.revival.factions.core.events.loot.EventChestManager;
import gg.revival.factions.core.events.loot.EventChestType;
import gg.revival.factions.core.events.palace.PalaceChest;
import gg.revival.factions.core.events.palace.PalaceManager;
import gg.revival.factions.core.tools.Configuration;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class CosmeticChestTask extends BukkitRunnable implements Runnable {

    @Override
    public void run() {
        if(!Configuration.playChestEffects) return;

        if(!EventChestManager.getEventChests().isEmpty()) {
            for(EventChest eventChest : EventChestManager.getEventChests()) {
                Location location = new Location(eventChest.getLocation().getWorld(), eventChest.getLocation().getBlockX() + 0.5, eventChest.getLocation().getBlockY() + 0.5, eventChest.getLocation().getBlockZ() + 0.5);

                if(eventChest.getType().equals(EventChestType.RARE)) {
                    eventChest.getLocation().getWorld().spigot().playEffect(location, Effect.HAPPY_VILLAGER, 0, 0, 0.5f, 0.5f, 0.5f, 0.01f, 25, 10);
                }

                if(eventChest.getType().equals(EventChestType.COMBAT)) {
                    eventChest.getLocation().getWorld().spigot().playEffect(location, Effect.CRIT, 0, 0, 0.5f, 0.5f, 0.5f, 0.01f, 15, 10);
                    eventChest.getLocation().getWorld().spigot().playEffect(location, Effect.MAGIC_CRIT, 0, 0, 0.5f, 0.5f, 0.5f, 0.01f, 15, 10);
                }

                if(eventChest.getType().equals(EventChestType.BREWING)) {
                    eventChest.getLocation().getWorld().spigot().playEffect(location, Effect.POTION_SWIRL, 0, 0, 0.5f, 0.5f, 0.5f, 0.01f, 25, 10);
                }
            }
        }

        if(!PalaceManager.getPalaceChests().isEmpty() && PalaceManager.isCaptured()) {
            for(PalaceChest palaceChest : PalaceManager.getPalaceChests()) {
                if(palaceChest.isLooted()) continue;

                Location location = new Location(palaceChest.getLocation().getWorld(), palaceChest.getLocation().getBlockX() + 0.5, palaceChest.getLocation().getBlockY() + 0.5, palaceChest.getLocation().getBlockZ() + 0.5);

                palaceChest.getLocation().getWorld().spigot().playEffect(location, Effect.HAPPY_VILLAGER, 0, 0, 0.5f, 0.5f, 0.5f, 0.001f, 25, 5);
            }
        }
    }

}
