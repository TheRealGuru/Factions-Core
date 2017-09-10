package gg.revival.factions.core.events.builder;

import gg.revival.factions.core.events.engine.EventManager;
import gg.revival.factions.core.events.obj.*;
import gg.revival.factions.core.tools.FileManager;
import gg.revival.factions.core.tools.Logger;
import gg.revival.factions.obj.ServerFaction;
import lombok.Getter;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventBuilder {

    @Getter static Map<UUID, KOTHBuilder> kothBuilders = new HashMap<>();
    @Getter static Map<UUID, DTCBuilder> dtcBuilders = new HashMap<>();

    public static KOTHBuilder getKOTHBuilder(UUID uuid) {
        if(kothBuilders.containsKey(uuid))
            return kothBuilders.get(uuid);

        return null;
    }

    public static DTCBuilder getDTCBuilder(UUID uuid) {
        if(dtcBuilders.containsKey(uuid))
            return dtcBuilders.get(uuid);

        return null;
    }

    public static boolean isBuilding(UUID uuid) {
        return dtcBuilders.containsKey(uuid) || kothBuilders.containsKey(uuid);
    }

    public static void saveEvent(Event event) {
        Location lootChest = event.getLootChest();
        ServerFaction hookedFaction = event.getHookedFaction();

        FileManager.getEvents().set("events." + event.getEventName() + ".display-name", event.getDisplayName());

        FileManager.getEvents().set("events." + event.getEventName() + ".loot-chest.x", lootChest.getBlockX());
        FileManager.getEvents().set("events." + event.getEventName() + ".loot-chest.y", lootChest.getBlockY());
        FileManager.getEvents().set("events." + event.getEventName() + ".loot-chest.z", lootChest.getBlockZ());
        FileManager.getEvents().set("events." + event.getEventName() + ".loot-chest.world", lootChest.getWorld().getName());

        FileManager.getEvents().set("events." + event.getEventName() + ".hooked-claim", hookedFaction.getFactionID().toString());

        for(Integer days : event.getSchedule().keySet()) {
            int hr = event.getSchedule().get(days).keySet().iterator().next();
            int min = event.getSchedule().get(days).get(hr);

            FileManager.getEvents().set("events." + event.getEventName() + ".schedule." + days + ".hr", hr);
            FileManager.getEvents().set("events." + event.getEventName() + ".schedule." + days + ".min", min);
        }

        if(event instanceof KOTHEvent) {
            KOTHEvent koth = (KOTHEvent)event;

            CapZone capzone = koth.getCapZone();
            int duration = koth.getDuration();
            int winCond = koth.getWinCond();

            FileManager.getEvents().set("events." + koth.getEventName() + ".duration", duration);
            FileManager.getEvents().set("events." + koth.getEventName() + ".win-cond", winCond);
            FileManager.getEvents().set("events." + koth.getEventName() + ".type", "KOTH");
            FileManager.getEvents().set("events." + koth.getEventName() + ".capzone.cornerone.x", capzone.getCornerOne().getBlockX());
            FileManager.getEvents().set("events." + koth.getEventName() + ".capzone.cornerone.y", capzone.getCornerOne().getBlockY());
            FileManager.getEvents().set("events." + koth.getEventName() + ".capzone.cornerone.z", capzone.getCornerOne().getBlockZ());
            FileManager.getEvents().set("events." + koth.getEventName() + ".capzone.cornertwo.x", capzone.getCornerTwo().getBlockX());
            FileManager.getEvents().set("events." + koth.getEventName() + ".capzone.cornertwo.y", capzone.getCornerTwo().getBlockY());
            FileManager.getEvents().set("events." + koth.getEventName() + ".capzone.cornertwo.z", capzone.getCornerTwo().getBlockZ());
            FileManager.getEvents().set("events." + koth.getEventName() + ".capzone.world", capzone.getWorldName());
        }

        if(event instanceof DTCEvent) {
            DTCEvent dtc = (DTCEvent)event;

            Location core = dtc.getCore();
            int winCond = dtc.getWinCond();
            int regenTimer = dtc.getRegenTimer();

            FileManager.getEvents().set("events." + dtc.getEventName() + ".win-cond", winCond);
            FileManager.getEvents().set("events." + dtc.getEventName() + ".regen-timer", regenTimer);
            FileManager.getEvents().set("events." + dtc.getEventName() + ".type", "DTC");
            FileManager.getEvents().set("events." + dtc.getEventName() + ".core.x", core.getBlockX());
            FileManager.getEvents().set("events." + dtc.getEventName() + ".core.y", core.getBlockY());
            FileManager.getEvents().set("events." + dtc.getEventName() + ".core.z", core.getBlockZ());
            FileManager.getEvents().set("events." + dtc.getEventName() + ".core.world", core.getWorld().getName());
        }

        FileManager.saveEvents();

        Logger.log("Event '" + event.getEventName() + "' has been updated and saved");
    }

    public static void deleteEvent(Event event) {
        EventManager.getActiveEvents().remove(event);

        FileManager.getEvents().set("events." + event.getEventName(), null);

        FileManager.saveEvents();

        Logger.log("Event '" + event.getEventName() + "' has been deleted");
    }

}
