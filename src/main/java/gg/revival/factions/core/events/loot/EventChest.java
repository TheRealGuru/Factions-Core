package gg.revival.factions.core.events.loot;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

public class EventChest {

    @Getter UUID uuid;
    @Getter EventChestType type;
    @Getter @Setter Location location;
    @Getter String lootTable;

    public EventChest(UUID uuid, EventChestType type, Location location, String lootTable) {
        this.uuid = uuid;
        this.type = type;
        this.location = location;
        this.lootTable = lootTable;
    }

}
