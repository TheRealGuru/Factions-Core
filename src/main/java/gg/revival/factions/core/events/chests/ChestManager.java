package gg.revival.factions.core.events.chests;

import com.google.common.collect.Sets;
import gg.revival.factions.core.FC;
import lombok.Getter;
import org.bukkit.Location;

import java.util.Set;
import java.util.UUID;

public class ChestManager {

    @Getter private FC core;
    @Getter Set<EventChest> loadedChests = Sets.newHashSet();

    public ChestManager(FC core) {
        this.core = core;
    }

    public EventChest getEventChestById(UUID uuid) {
        if(loadedChests.isEmpty()) return null;

        for(EventChest eventChest : loadedChests)
            if(eventChest.getUuid().equals(uuid)) return eventChest;

        return null;
    }

    public EventChest getEventChestByLocation(Location location) {
        if(loadedChests.isEmpty()) return null;

        for(EventChest eventChest : loadedChests)
            if(eventChest.getLocation().equals(location)) return eventChest;

        return null;
    }

    public ClaimChest getClaimChestByLocation(Location location) {
        EventChest eventChest = getEventChestByLocation(location);

        if(!(eventChest instanceof ClaimChest)) return null;

        return (ClaimChest) eventChest;
    }

    public PalaceChest getPalaceChestByLocation(Location location) {
        EventChest eventChest = getEventChestByLocation(location);

        if(!(eventChest instanceof PalaceChest)) return null;

        return (PalaceChest)eventChest;
    }

    public void createChest(EventChest eventChest) {
        if(eventChest instanceof ClaimChest) {
            ClaimChest claimChest = (ClaimChest)eventChest;
            String uuid = claimChest.getUuid().toString();

            core.getFileManager().getEvents().set("claim-chests." + uuid + ".location.x", claimChest.getLocation().getBlockX());
            core.getFileManager().getEvents().set("claim-chests." + uuid + ".location.y", claimChest.getLocation().getBlockY());
            core.getFileManager().getEvents().set("claim-chests." + uuid + ".location.z", claimChest.getLocation().getBlockZ());
            core.getFileManager().getEvents().set("claim-chests." + uuid + ".location.world", claimChest.getLocation().getWorld().getName());

            core.getFileManager().getEvents().set("claim-chests." + uuid + ".table", claimChest.getLootTable());
            core.getFileManager().getEvents().set("claim-chests." + uuid + ".type", claimChest.getType().toString());

            core.getFileManager().saveEvents();

            loadedChests.add(eventChest);

            return;
        }

        if(eventChest instanceof PalaceChest) {
            PalaceChest palaceChest = (PalaceChest)eventChest;
            String uuid = palaceChest.getUuid().toString();

            core.getFileManager().getEvents().set("palace-chests." + uuid + ".location.x", palaceChest.getLocation().getBlockX());
            core.getFileManager().getEvents().set("palace-chests." + uuid + ".location.y", palaceChest.getLocation().getBlockY());
            core.getFileManager().getEvents().set("palace-chests." + uuid + ".location.z", palaceChest.getLocation().getBlockZ());
            core.getFileManager().getEvents().set("palace-chests." + uuid + ".location.world", palaceChest.getLocation().getWorld().getName());

            core.getFileManager().getEvents().set("palace-chests." + uuid + ".table", palaceChest.getLootTable());
            core.getFileManager().getEvents().set("palace-chests." + uuid + ".type", palaceChest.getTier());

            core.getFileManager().saveEvents();

            loadedChests.add(eventChest);
        }
    }

    public void deleteChest(EventChest eventChest) {
        if(eventChest instanceof ClaimChest) {
            ClaimChest claimChest = (ClaimChest)eventChest;
            String uuid = claimChest.getUuid().toString();

            core.getFileManager().getEvents().set("claim-chests." + uuid, null);

            core.getFileManager().saveEvents();

            loadedChests.remove(eventChest);
        }

        if(eventChest instanceof PalaceChest) {
            PalaceChest palaceChest = (PalaceChest) eventChest;
            String uuid = palaceChest.getUuid().toString();

            core.getFileManager().getEvents().set("palace-chests." + uuid, null);

            core.getFileManager().saveEvents();

            loadedChests.remove(eventChest);
        }
    }

}
