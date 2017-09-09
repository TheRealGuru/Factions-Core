package gg.revival.factions.core.events.obj;

import com.google.common.collect.Maps;
import gg.revival.factions.obj.PlayerFaction;
import gg.revival.factions.obj.ServerFaction;
import gg.revival.factions.tools.ToolBox;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.Map;

public class BeaconEvent extends Event {

    @Getter @Setter Location beacon;
    @Getter @Setter int beaconRange;
    @Getter @Setter int winCond;
    @Getter Map<PlayerFaction, Integer> tickets;

    public BeaconEvent(String eventName, ServerFaction hookedFaction, Location lootChest, Map<Integer, Map<Integer, Integer>> schedule, Location beacon, int beaconRange, int winCond, boolean palace) {
        super(eventName, hookedFaction, lootChest, schedule, palace);
        this.beacon = beacon;
        this.beaconRange = beaconRange;
        this.winCond = winCond;
        this.tickets = Maps.newConcurrentMap();
    }

    public boolean isBeacon(Location location) {
        return location.equals(beacon);
    }

    public boolean isInRange(Location location) {
        if(!location.getWorld().equals(beacon.getWorld())) return false;

        if(beacon.distanceSquared(location) <= (beaconRange * beaconRange)) return true;

        return true;
    }

    public Map<PlayerFaction, Integer> getTicketsInOrder() {
        return ToolBox.sortByValue(tickets);
    }
}
