package gg.revival.factions.core.events.builder;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.events.obj.CapZone;
import gg.revival.factions.core.events.obj.DTCEvent;
import gg.revival.factions.core.events.obj.Event;
import gg.revival.factions.core.events.obj.KOTHEvent;
import lombok.Getter;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventBuilder {

    @Getter private FC core;

    public EventBuilder(FC core) {
        this.core = core;
    }

    /**
     * Map containing all players currently building KOTH events
     */
    @Getter Map<UUID, KOTHBuilder> kothBuilders = new HashMap<>();

    /**
     * Map containing all players currently building DTC events
     */
    @Getter Map<UUID, DTCBuilder> dtcBuilders = new HashMap<>();

    /**
     * Returns a KOTHBuilder object based on a players UUID
     * @param uuid
     * @return
     */
    public KOTHBuilder getKOTHBuilder(UUID uuid) {
        if(kothBuilders.containsKey(uuid))
            return kothBuilders.get(uuid);

        return null;
    }

    /**
     * Returns a DTCBuilder object based on a players UUID
     * @param uuid
     * @return
     */
    public DTCBuilder getDTCBuilder(UUID uuid) {
        if(dtcBuilders.containsKey(uuid))
            return dtcBuilders.get(uuid);

        return null;
    }

    /**
     * Retruns true if the given UUID is actively building an event
     * @param uuid
     * @return
     */
    public boolean isBuilding(UUID uuid) {
        return dtcBuilders.containsKey(uuid) || kothBuilders.containsKey(uuid);
    }

    /**
     * Saves the given event to file
     * @param event
     */
    public void saveEvent(Event event) {
        Location lootChest = event.getLootChest();

        core.getFileManager().getEvents().set("events." + event.getEventName() + ".display-name", event.getDisplayName());

        core.getFileManager().getEvents().set("events." + event.getEventName() + ".loot-chest.x", lootChest.getBlockX());
        core.getFileManager().getEvents().set("events." + event.getEventName() + ".loot-chest.y", lootChest.getBlockY());
        core.getFileManager().getEvents().set("events." + event.getEventName() + ".loot-chest.z", lootChest.getBlockZ());
        core.getFileManager().getEvents().set("events." + event.getEventName() + ".loot-chest.world", lootChest.getWorld().getName());

        core.getFileManager().getEvents().set("events." + event.getEventName() + ".hooked-claim", event.getHookedFactionId().toString());
        core.getFileManager().getEvents().set("events." + event.getEventName() + ".palace", event.isPalace());

        for(Integer days : event.getSchedule().keySet()) {
            int hr = event.getSchedule().get(days).keySet().iterator().next();
            int min = event.getSchedule().get(days).get(hr);

            core.getFileManager().getEvents().set("events." + event.getEventName() + ".schedule." + days + ".hr", hr);
            core.getFileManager().getEvents().set("events." + event.getEventName() + ".schedule." + days + ".min", min);
        }

        if(event instanceof KOTHEvent) {
            KOTHEvent koth = (KOTHEvent)event;

            CapZone capzone = koth.getCapZone();
            int duration = koth.getDuration();
            int winCond = koth.getWinCond();

            core.getFileManager().getEvents().set("events." + koth.getEventName() + ".duration", duration);
            core.getFileManager().getEvents().set("events." + koth.getEventName() + ".win-cond", winCond);
            core.getFileManager().getEvents().set("events." + koth.getEventName() + ".type", "KOTH");
            core.getFileManager().getEvents().set("events." + koth.getEventName() + ".capzone.cornerone.x", capzone.getCornerOne().getBlockX());
            core.getFileManager().getEvents().set("events." + koth.getEventName() + ".capzone.cornerone.y", capzone.getCornerOne().getBlockY());
            core.getFileManager().getEvents().set("events." + koth.getEventName() + ".capzone.cornerone.z", capzone.getCornerOne().getBlockZ());
            core.getFileManager().getEvents().set("events." + koth.getEventName() + ".capzone.cornertwo.x", capzone.getCornerTwo().getBlockX());
            core.getFileManager().getEvents().set("events." + koth.getEventName() + ".capzone.cornertwo.y", capzone.getCornerTwo().getBlockY());
            core.getFileManager().getEvents().set("events." + koth.getEventName() + ".capzone.cornertwo.z", capzone.getCornerTwo().getBlockZ());
            core.getFileManager().getEvents().set("events." + koth.getEventName() + ".capzone.world", capzone.getWorldName());
        }

        if(event instanceof DTCEvent) {
            DTCEvent dtc = (DTCEvent)event;

            Location coreLocation = dtc.getCore();
            int winCond = dtc.getWinCond();
            int regenTimer = dtc.getRegenTimer();

            core.getFileManager().getEvents().set("events." + dtc.getEventName() + ".win-cond", winCond);
            core.getFileManager().getEvents().set("events." + dtc.getEventName() + ".regen-timer", regenTimer);
            core.getFileManager().getEvents().set("events." + dtc.getEventName() + ".type", "DTC");
            core.getFileManager().getEvents().set("events." + dtc.getEventName() + ".core.x", coreLocation.getBlockX());
            core.getFileManager().getEvents().set("events." + dtc.getEventName() + ".core.y", coreLocation.getBlockY());
            core.getFileManager().getEvents().set("events." + dtc.getEventName() + ".core.z", coreLocation.getBlockZ());
            core.getFileManager().getEvents().set("events." + dtc.getEventName() + ".core.world", coreLocation.getWorld().getName());
        }

        core.getFileManager().saveEvents();

        core.getLog().log("Event '" + event.getEventName() + "' has been updated and saved");
    }

    /**
     * Deletes a given event from file
     * @param event
     */
    public void deleteEvent(Event event) {
        core.getEvents().getEventManager().getActiveEvents().remove(event);

        core.getFileManager().getEvents().set("events." + event.getEventName(), null);
        core.getFileManager().saveEvents();

        core.getLog().log("Event '" + event.getEventName() + "' has been deleted");
    }

}
