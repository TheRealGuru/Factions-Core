package gg.revival.factions.core.events.chests;

import com.google.common.collect.Sets;
import gg.revival.factions.core.tools.FileManager;
import lombok.Getter;
import org.bukkit.Location;

import java.util.Set;
import java.util.UUID;

public class ChestManager {

    @Getter static Set<EventChest> loadedChests = Sets.newHashSet();

    public static EventChest getEventChestById(UUID uuid) {
        if(loadedChests.isEmpty()) return null;

        for(EventChest eventChest : loadedChests)
            if(eventChest.getUuid().equals(uuid)) return eventChest;

        return null;
    }

    public static EventChest getEventChestByLocation(Location location) {
        if(loadedChests.isEmpty()) return null;

        for(EventChest eventChest : loadedChests)
            if(eventChest.getLocation().equals(location)) return eventChest;

        return null;
    }

    public static ClaimChest getClaimChestByLocation(Location location) {
        EventChest eventChest = getEventChestByLocation(location);

        if(!(eventChest instanceof ClaimChest)) return null;

        return (ClaimChest) eventChest;
    }

    public static PalaceChest getPalaceChestByLocation(Location location) {
        EventChest eventChest = getEventChestByLocation(location);

        if(!(eventChest instanceof PalaceChest)) return null;

        return (PalaceChest)eventChest;
    }

    public static void createChest(EventChest eventChest) {
        if(eventChest instanceof ClaimChest) {
            ClaimChest claimChest = (ClaimChest)eventChest;
            String uuid = claimChest.getUuid().toString();

            FileManager.getEvents().set("claim-chests." + uuid + ".location.x", claimChest.getLocation().getBlockX());
            FileManager.getEvents().set("claim-chests." + uuid + ".location.y", claimChest.getLocation().getBlockY());
            FileManager.getEvents().set("claim-chests." + uuid + ".location.z", claimChest.getLocation().getBlockZ());
            FileManager.getEvents().set("claim-chests." + uuid + ".location.world", claimChest.getLocation().getWorld().getName());

            FileManager.getEvents().set("claim-chests." + uuid + ".table", claimChest.getLootTable());
            FileManager.getEvents().set("claim-chests." + uuid + ".type", claimChest.getType().toString());

            FileManager.saveEvents();

            loadedChests.add(eventChest);

            return;
        }

        if(eventChest instanceof PalaceChest) {
            PalaceChest palaceChest = (PalaceChest)eventChest;
            String uuid = palaceChest.getUuid().toString();

            FileManager.getEvents().set("palace-chests." + uuid + ".location.x", palaceChest.getLocation().getBlockX());
            FileManager.getEvents().set("palace-chests." + uuid + ".location.y", palaceChest.getLocation().getBlockY());
            FileManager.getEvents().set("palace-chests." + uuid + ".location.z", palaceChest.getLocation().getBlockZ());
            FileManager.getEvents().set("palace-chests." + uuid + ".location.world", palaceChest.getLocation().getWorld().getName());

            FileManager.getEvents().set("palace-chests." + uuid + ".table", palaceChest.getLootTable());
            FileManager.getEvents().set("palace-chests." + uuid + ".type", palaceChest.getTier());

            FileManager.saveEvents();

            loadedChests.add(eventChest);
        }
    }

    public static void deleteChest(EventChest eventChest) {
        if(eventChest instanceof ClaimChest) {
            ClaimChest claimChest = (ClaimChest)eventChest;
            String uuid = claimChest.getUuid().toString();

            FileManager.getEvents().set("claim-chests." + uuid, null);

            FileManager.saveEvents();

            loadedChests.remove(eventChest);
        }

        if(eventChest instanceof PalaceChest) {
            PalaceChest palaceChest = (PalaceChest) eventChest;
            String uuid = palaceChest.getUuid().toString();

            FileManager.getEvents().set("palace-chests." + uuid, null);

            FileManager.saveEvents();

            loadedChests.remove(eventChest);
        }
    }

}
