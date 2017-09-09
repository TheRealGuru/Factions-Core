package gg.revival.factions.core.events.obj;

import com.google.common.collect.Maps;
import gg.revival.factions.obj.PlayerFaction;
import gg.revival.factions.obj.ServerFaction;
import gg.revival.factions.tools.ToolBox;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.Map;

public class DTCEvent extends Event {

    @Getter @Setter Location core;
    @Getter @Setter long resetTime;
    @Getter @Setter int winCond;
    @Getter @Setter int regenTimer;
    @Getter @Setter PlayerFaction cappingFaction;
    @Getter Map<PlayerFaction, Integer> tickets;

    public DTCEvent(String eventName, ServerFaction hookedFaction, Location lootChest, Map<Integer, Map<Integer, Integer>> schedule, Location core, int winCond, int regenTimer, boolean palace) {
        super(eventName, hookedFaction, lootChest, schedule, palace);
        this.core = core;
        this.resetTime = -1L;
        this.winCond = winCond;
        this.regenTimer = regenTimer;
        this.tickets = Maps.newConcurrentMap();
    }

    public boolean isCore(Location location) {
        return location.equals(core);
    }

    public Map<PlayerFaction, Integer> getTicketsInOrder() {
        return ToolBox.sortByValue(tickets);
    }

}
