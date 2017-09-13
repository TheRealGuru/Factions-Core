package gg.revival.factions.core.events.loot;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EventChestManager {

    @Getter static Set<EventChest> eventChests = new HashSet<>();

    public static EventChest getEventChestAtLocation(Location location) {
        for(EventChest eventChest : eventChests) {
            if(!eventChest.getLocation().equals(location)) continue;

            return eventChest;
        }

        return null;
    }

    public static List<ItemStack> getRandomItems(EventChest lootChest, int pulls) {
        Random random = new Random();
        List<ItemStack> result = new ArrayList<>();

        for(int i = 0; i < pulls; i++) {
            result.add(lootChest.getLootTable().get(random.nextInt(lootChest.getLootTable().size())));
        }

        return result;
    }
}
