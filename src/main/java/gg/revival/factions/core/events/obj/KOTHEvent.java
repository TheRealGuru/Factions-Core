package gg.revival.factions.core.events.obj;

import com.google.common.collect.Maps;
import gg.revival.factions.obj.PlayerFaction;
import gg.revival.factions.tools.ToolBox;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;

public class KOTHEvent extends Event {

    @Getter CapZone capZone;
    @Getter @Setter int duration, winCond;
    @Getter Map<PlayerFaction, Integer> tickets;
    @Getter @Setter PlayerFaction cappingFaction;
    @Getter @Setter long nextTicketTime;
    @Getter @Setter boolean isContested;
    @Getter @Setter long pauseDuration;

    public KOTHEvent(String eventName, String displayName, UUID hookedFactionId, Location lootChest, Map<Integer, Map<Integer, Integer>> schedule, CapZone capzone, int duration, int winCond, boolean palace) {
        super(eventName, displayName, hookedFactionId, lootChest, schedule, palace);
        this.capZone = capzone;
        this.duration = duration;
        this.winCond = winCond;
        this.tickets = Maps.newConcurrentMap();
        this.nextTicketTime = duration * 1000L;
        this.isContested = false;
    }

    public boolean insideCapzone(Location location, boolean isPlayer) {
        return capZone.inside(location, isPlayer);
    }

    public Map<PlayerFaction, Integer> getTicketsInOrder() {
        return ToolBox.sortByValue(tickets);
    }

    public int getCapDuration() {
        if(nextTicketTime == -1L) return duration;

        return (int)((nextTicketTime - System.currentTimeMillis()) / 1000L);
    }
}
