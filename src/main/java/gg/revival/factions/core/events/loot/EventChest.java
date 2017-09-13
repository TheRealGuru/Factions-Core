package gg.revival.factions.core.events.loot;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EventChest {

    @Getter
    EventChestType type;
    @Getter @Setter Location location;
    @Getter List<ItemStack> lootTable;

    public EventChest(EventChestType type, Location location, List<ItemStack> lootTable) {
        this.type = type;
        this.location = location;
        this.lootTable = lootTable;
    }

}
