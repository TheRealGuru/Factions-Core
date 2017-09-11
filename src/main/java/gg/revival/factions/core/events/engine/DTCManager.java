package gg.revival.factions.core.events.engine;

import gg.revival.factions.core.events.obj.DTCEvent;
import gg.revival.factions.core.events.obj.Event;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class DTCManager {

    public static Set<DTCEvent> getActiveDTCEvents() {
        List<Event> cache = new CopyOnWriteArrayList<>(EventManager.getActiveEvents());
        Set<DTCEvent> result = new HashSet<>();

        for(Event event : cache) {
            if(!(event instanceof DTCEvent)) continue;

            DTCEvent dtc = (DTCEvent)event;

            result.add(dtc);
        }

        return result;
    }

    public static DTCEvent getDTCByCore(Location coreLocation) {
        List<Event> cache = new CopyOnWriteArrayList<>(EventManager.getActiveEvents());

        for(Event event : cache) {
            if(!(event instanceof DTCEvent)) continue;

            DTCEvent dtc = (DTCEvent)event;

            if(dtc.getCore().equals(coreLocation))
                return dtc;
        }

        return null;
    }

    public static void updateResetTimer(DTCEvent event) {
        event.setResetTime(System.currentTimeMillis() + (event.getRegenTimer() * 1000L));
    }

}
