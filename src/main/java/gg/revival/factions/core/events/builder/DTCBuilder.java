package gg.revival.factions.core.events.builder;

import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.events.obj.DTCEvent;
import gg.revival.factions.obj.Faction;
import gg.revival.factions.obj.ServerFaction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.*;

public class DTCBuilder {

    @Getter @Setter String eventName, displayName, hookedFactionName;
    @Getter @Setter Location lootChest, core;
    @Getter Map<Integer, Map.Entry<Integer, Integer>> schedule;
    @Getter @Setter int winCond, regenTimer;
    @Getter @Setter boolean palace;

    @Getter @Setter int buildPhase;

    public DTCBuilder(int regenTimer, int winCond) {
        this.regenTimer = regenTimer;
        this.winCond = winCond;

        this.buildPhase = 1;

        Map<Integer, Map.Entry<Integer, Integer>> schedule = new HashMap<>();

        schedule.put(Calendar.MONDAY, new AbstractMap.SimpleEntry<>(12, 0));
        schedule.put(Calendar.TUESDAY, new AbstractMap.SimpleEntry<>(12, 0));
        schedule.put(Calendar.WEDNESDAY, new AbstractMap.SimpleEntry<>(12, 0));
        schedule.put(Calendar.THURSDAY, new AbstractMap.SimpleEntry<>(12, 0));
        schedule.put(Calendar.FRIDAY, new AbstractMap.SimpleEntry<>(12, 0));
        schedule.put(Calendar.SATURDAY, new AbstractMap.SimpleEntry<>(12, 0));

        this.schedule = schedule;
    }

    public boolean isReady() {
        return eventName != null && displayName != null && lootChest != null && core != null && regenTimer != 0 && winCond != 0;
    }

    public DTCEvent convertToDTC() {
        if(!isReady()) return null;

        String displayName = ChatColor.translateAlternateColorCodes('&', getDisplayName());
        Faction faction = FactionManager.getFactionByName(hookedFactionName);
        UUID hookedFactionId = null;

        if(faction != null && faction instanceof ServerFaction)
            hookedFactionId = faction.getFactionID();

        DTCEvent dtc = new DTCEvent(eventName, displayName, hookedFactionId, lootChest, schedule, core, winCond, regenTimer, palace);

        return dtc;
    }

    public String getPhaseResponse() {
        if(buildPhase == 1) {
            return ChatColor.GREEN + "Type the name of this event";
        }

        if(buildPhase == 2) {
            return ChatColor.GREEN + "Now enter the display name (w/ color codes!) for this event";
        }

        if(buildPhase == 3) {
            return ChatColor.GREEN + "Type the name of the faction claims this event is connected to (if any)";
        }

        if(buildPhase == 4) {
            return ChatColor.GREEN + "Right-click the Event Loot Chest'";
        }

        if(buildPhase == 5) {
            return ChatColor.GREEN + "Assuming the default event schedule (Mon-Fri), re-configure it in the events.yml file" + "\n" + ChatColor.GREEN + "Right-click the Core";
        }

        if(buildPhase == 6) {
            return ChatColor.GREEN + "Is this a Palace event? (y/N)";
        }

        if(buildPhase == 7) {
            return ChatColor.GREEN + "Event configured successfully";
        }

        return null;
    }

}
