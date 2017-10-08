package gg.revival.factions.core.events.engine;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.events.obj.DTCEvent;
import gg.revival.factions.core.events.obj.Event;
import lombok.Getter;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class DTCManager {

    @Getter private FC core;

    public DTCManager(FC core) {
        this.core = core;
    }

    /**
     * Returns a Set containing all active DTC events
     * @return
     */
    public Set<DTCEvent> getActiveDTCEvents() {
        List<Event> cache = new CopyOnWriteArrayList<>(core.getEvents().getEventManager().getActiveEvents());
        Set<DTCEvent> result = new HashSet<>();

        for(Event event : cache) {
            if(!(event instanceof DTCEvent)) continue;

            DTCEvent dtc = (DTCEvent)event;

            result.add(dtc);
        }

        return result;
    }

    /**
     * Returns a DTC event based on given Core location
     * @param coreLocation
     * @return
     */
    public DTCEvent getDTCByCore(Location coreLocation) {
        List<Event> cache = new CopyOnWriteArrayList<>(core.getEvents().getEventManager().getActiveEvents());

        for(Event event : cache) {
            if(!(event instanceof DTCEvent)) continue;

            DTCEvent dtc = (DTCEvent)event;

            if(dtc.getCore().equals(coreLocation))
                return dtc;
        }

        return null;
    }

    /**
     * Updates a DTC event core timer based on its configuration
     * @param event
     */
    public void updateResetTimer(DTCEvent event) {
        event.setResetTime(System.currentTimeMillis() + (event.getRegenTimer() * 1000L));
    }

}
