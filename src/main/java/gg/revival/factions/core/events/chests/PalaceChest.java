package gg.revival.factions.core.events.chests;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

public class PalaceChest extends EventChest {

    @Getter int tier;
    @Getter @Setter boolean recentlyLooted;

    public PalaceChest(UUID uuid, Location location, String lootTable, int tier) {
        super(uuid, location, lootTable);
        this.tier = tier;
        this.recentlyLooted = false;
    }
}
