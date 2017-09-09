package gg.revival.factions.core.events.builder;

import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.events.obj.CapZone;
import gg.revival.factions.core.events.obj.KOTHEvent;
import gg.revival.factions.obj.Faction;
import gg.revival.factions.obj.ServerFaction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class KOTHBuilder {

    @Getter @Setter String eventName;
    @Getter @Setter String hookedFactionName;
    @Getter @Setter Location lootChest;
    @Getter @Setter CapZone capzone;
    @Getter Map<Integer, Map<Integer, Integer>> schedule;
    @Getter @Setter int duration, winCond;
    @Getter @Setter boolean palace;

    public KOTHBuilder(String eventName, String hookedFactionName, int duration, int winCond, boolean palace) {
        this.eventName = eventName;
        this.hookedFactionName = hookedFactionName;
        this.duration = duration;
        this.winCond = winCond;
        this.palace = palace;
        this.lootChest = null;

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
        if(eventName == null)
            return false;

        if(lootChest == null)
            return false;

        if(capzone == null)
            return false;

        if(duration == 0 || winCond == 0)
            return false;

        return true;
    }

    public KOTHEvent convertToKOTH() {
        Faction faction = FactionManager.getFactionByName(hookedFactionName);
        ServerFaction hookedFaction = null;

        if(faction != null && faction instanceof ServerFaction) {
            hookedFaction = (ServerFaction)faction;
        }

        KOTHEvent koth = new KOTHEvent(eventName, hookedFaction, lootChest, schedule, capzone, winCond, duration, palace);

        return koth;
    }

}
