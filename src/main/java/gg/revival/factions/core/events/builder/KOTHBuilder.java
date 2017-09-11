package gg.revival.factions.core.events.builder;

import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.events.obj.CapZone;
import gg.revival.factions.core.events.obj.KOTHEvent;
import gg.revival.factions.obj.Faction;
import gg.revival.factions.obj.ServerFaction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class KOTHBuilder {

    @Getter @Setter String eventName;
    @Getter @Setter String displayName;
    @Getter @Setter String hookedFactionName;
    @Getter @Setter Location lootChest;
    @Getter @Setter CapZone capzone;
    @Getter Map<Integer, Map<Integer, Integer>> schedule;
    @Getter @Setter int duration, winCond;
    @Getter @Setter boolean palace;

    @Getter @Setter int buildPhase;

    public KOTHBuilder(int duration, int winCond) {
        this.duration = duration;
        this.winCond = winCond;

        this.buildPhase = 1;

        Map<Integer, Map<Integer, Integer>> schedule = new HashMap<>();
        Map<Integer, Integer> time = new HashMap<>();

        time.put(12, 0);

        schedule.put(Calendar.MONDAY, time);
        schedule.put(Calendar.TUESDAY, time);
        schedule.put(Calendar.WEDNESDAY, time);
        schedule.put(Calendar.THURSDAY, time);
        schedule.put(Calendar.FRIDAY, time);
        schedule.put(Calendar.SATURDAY, time);

        this.schedule = schedule;
    }

    public boolean isReady() {
        return eventName != null && displayName != null && lootChest != null && capzone != null && duration != 0 && winCond != 0;
    }

    public KOTHEvent convertToKOTH() {
        if(!isReady()) return null;

        String displayName = ChatColor.translateAlternateColorCodes('&', getDisplayName());
        Faction faction = FactionManager.getFactionByName(hookedFactionName);
        ServerFaction hookedFaction = null;

        if(faction != null && faction instanceof ServerFaction) {
            hookedFaction = (ServerFaction)faction;
        }

        KOTHEvent koth = new KOTHEvent(eventName, displayName, hookedFaction, lootChest, schedule, capzone, duration, winCond, palace);

        return koth;
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
            return ChatColor.GREEN + "Right-click the Event Loot Chest";
        }

        if(buildPhase == 5) {
            return ChatColor.GREEN + "Assuming the default event schedule (Mon-Fri), re-configure it in the events.yml file" + "\n" + ChatColor.GREEN + "Right-click the first corner of the CapZone";
        }

        if(buildPhase == 6) {
            return ChatColor.GREEN + "Right-click the second corner of the CapZone";
        }

        if(buildPhase == 7) {
            return ChatColor.GREEN + "Is this a Palace event? (y/N)";
        }

        if(buildPhase == 8) {
            return ChatColor.GREEN + "Event configured successfully";
        }

        return null;
    }

}
