package gg.revival.factions.core.events.builder;

import gg.revival.factions.core.events.engine.EventManager;
import gg.revival.factions.core.events.obj.*;
import gg.revival.factions.core.tools.FileManager;
import gg.revival.factions.core.tools.Logger;
import lombok.Getter;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventBuilder {

    @Getter static Map<UUID, KOTHBuilder> kothBuilders = new HashMap<>();
    @Getter static Map<UUID, DTCBuilder> dtcBuilders = new HashMap<>();
    @Getter static Map<UUID, BeaconBuilder> beaconBuilders = new HashMap<>();

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

    public static BeaconBuilder getBeaconBuilder(UUID uuid) {
        if(beaconBuilders.containsKey(uuid))
            return beaconBuilders.get(uuid);

        return null;
    }

    public static void saveEvent(Event event) {
        Location lootChest = event.getLootChest();

        FileManager.getEvents().set("events." + event.getEventName() + ".loot-chest.x", lootChest.getBlockX());
        FileManager.getEvents().set("events." + event.getEventName() + ".loot-chest.y", lootChest.getBlockY());
        FileManager.getEvents().set("events." + event.getEventName() + ".loot-chest.z", lootChest.getBlockZ());
        FileManager.getEvents().set("events." + event.getEventName() + ".loot-chest.world", lootChest.getWorld().getName());

        if(event instanceof KOTHEvent) {
            KOTHEvent koth = (KOTHEvent)event;

            CapZone capzone = koth.getCapZone();
            int duration = koth.getDuration();
            int winCond = koth.getWinCond();

            FileManager.getEvents().set("events." + koth.getEventName() + ".duration", duration);
            FileManager.getEvents().set("events." + koth.getEventName() + ".win-cond", winCond);
            FileManager.getEvents().set("events." + koth.getEventName() + ".type", "KOTH");
            FileManager.getEvents().set("events." + koth.getEventName() + ".capzone.x1", capzone.getXMin());
            FileManager.getEvents().set("events." + koth.getEventName() + ".capzone.x2", capzone.getXMax());
            FileManager.getEvents().set("events." + koth.getEventName() + ".capzone.y1", capzone.getYMin());
            FileManager.getEvents().set("events." + koth.getEventName() + ".capzone.y2", capzone.getYMax());
            FileManager.getEvents().set("events." + koth.getEventName() + ".capzone.z1", capzone.getZMin());
            FileManager.getEvents().set("events." + koth.getEventName() + ".capzone.z2", capzone.getZMax());
            FileManager.getEvents().set("events." + koth.getEventName() + ".world", capzone.getWorldName());
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

        if(event instanceof BeaconEvent) {
            BeaconEvent beacon = (BeaconEvent) event;

            Location beaconLocation = beacon.getBeacon();
            int winCond = beacon.getWinCond();
            int range = beacon.getBeaconRange();

            FileManager.getEvents().set("events." + beacon.getEventName() + ".win-cond", winCond);
            FileManager.getEvents().set("events." + beacon.getEventName() + ".range", range);
            FileManager.getEvents().set("events." + beacon.getEventName() + ".type", "BEACON");
            FileManager.getEvents().set("events." + beacon.getEventName() + ".beacon.x", beaconLocation.getBlockX());
            FileManager.getEvents().set("events." + beacon.getEventName() + ".beacon.y", beaconLocation.getBlockY());
            FileManager.getEvents().set("events." + beacon.getEventName() + ".beacon.z", beaconLocation.getBlockZ());
            FileManager.getEvents().set("events." + beacon.getEventName()+ ".beacon.world", beaconLocation.getWorld().getName());
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
