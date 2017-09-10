package gg.revival.factions.core.events.obj;

import gg.revival.factions.obj.PlayerFaction;
import gg.revival.factions.obj.ServerFaction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class Event {

    @Getter @Setter String eventName;
    @Getter @Setter String displayName;
    @Getter @Setter ServerFaction hookedFaction;
    @Getter @Setter Location lootChest;
    @Getter Map<Integer, Map<Integer, Integer>> schedule;
    @Getter @Setter boolean palace;
    @Getter @Setter PlayerFaction lootChestFaction;
    @Getter @Setter long lootChestUnlockTime;
    @Getter @Setter boolean active;

    Event(String eventName, String displayName, ServerFaction hookedFaction, Location lootChest, Map<Integer, Map<Integer, Integer>> schedule, boolean palace) {
        this.eventName = eventName;
        this.displayName = displayName;
        this.hookedFaction = hookedFaction;
        this.lootChest = lootChest;
        this.schedule = schedule;
        this.lootChestUnlockTime = -1L;
        this.active = false;
        this.palace = palace;
    }

    public boolean canAccessLootChest(Player player) {
        return lootChestUnlockTime <= System.currentTimeMillis() || lootChestFaction != null && lootChestFaction.getRoster(true).contains(player.getUniqueId());
    }

}
