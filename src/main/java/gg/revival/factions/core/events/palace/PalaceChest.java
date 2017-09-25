package gg.revival.factions.core.events.palace;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

public class PalaceChest {

    @Getter UUID uuid;
    @Getter @Setter int level;
    @Getter @Setter Location location;
    @Getter String lootTable;
    @Getter @Setter boolean looted;

    public PalaceChest(UUID uuid, int level, Location location, String lootTable) {
        this.uuid = uuid;
        this.level = level;
        this.location = location;
        this.lootTable = lootTable;
        this.looted = false;
    }

}
