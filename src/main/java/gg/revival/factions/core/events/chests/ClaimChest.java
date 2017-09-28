package gg.revival.factions.core.events.chests;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class ClaimChest extends EventChest {

    @Getter ClaimChestType type;

    public ClaimChest(UUID uuid, Location location, String lootTable, ClaimChestType type) {
        super(uuid, location, lootTable);
        this.type = type;
    }

    public ArmorStand getAboveArmorStand() {
        for (Entity nearbyEntities : location.getWorld().getNearbyEntities(location, 1, 2, 1)) {
            if (!(nearbyEntities instanceof ArmorStand)) continue;
            return (ArmorStand) nearbyEntities;
        }

        return null;
    }
}
