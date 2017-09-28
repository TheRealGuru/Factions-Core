package gg.revival.factions.core.events.chests;

import lombok.Getter;
import org.bukkit.Location;

import java.util.UUID;

public class EventChest {

    @Getter UUID uuid;
    @Getter Location location;
    @Getter String lootTable;

    public EventChest(UUID uuid, Location location, String lootTable) {
        this.uuid = uuid;
        this.location = location;
        this.lootTable = lootTable;
    }

}
