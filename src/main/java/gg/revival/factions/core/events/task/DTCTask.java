package gg.revival.factions.core.events.task;

import gg.revival.factions.core.events.engine.DTCManager;
import gg.revival.factions.core.events.engine.EventManager;
import gg.revival.factions.core.events.messages.EventsMessages;
import gg.revival.factions.core.events.obj.DTCEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class DTCTask extends BukkitRunnable {

    @Override
    public void run() {
        for(DTCEvent dtc : DTCManager.getActiveDTCEvents()) {
            if(dtc.getRecentBreaker() != null) {
                if(dtc.getCappingFaction() != null && !dtc.getCappingFaction().equals(dtc.getRecentBreaker())) {
                    if(dtc.isPalace())
                        Bukkit.broadcastMessage(EventsMessages.asPalace(EventsMessages.controlLost(dtc.getCappingFaction(), dtc)));
                    else
                        Bukkit.broadcastMessage(EventsMessages.asDTC(EventsMessages.controlLost(dtc.getCappingFaction(), dtc)));

                    dtc.getTickets().clear();
                }

                dtc.setCappingFaction(dtc.getRecentBreaker());
                EventManager.tickEvent(dtc);

                dtc.setRecentBreaker(null);
                DTCManager.updateResetTimer(dtc);
            }

            if(dtc.getResetTime() != -1L && dtc.getResetTime() <= System.currentTimeMillis()) {
                if(dtc.getCappingFaction() != null) {
                    if(dtc.isPalace())
                        Bukkit.broadcastMessage(EventsMessages.asPalace(EventsMessages.controlLost(dtc.getCappingFaction(), dtc)));
                    else
                        Bukkit.broadcastMessage(EventsMessages.asDTC(EventsMessages.controlLost(dtc.getCappingFaction(), dtc)));
                }

                dtc.setResetTime(-1L);
                dtc.getTickets().clear();;
                dtc.setRecentBreaker(null);
                dtc.setCappingFaction(null);
            }
        }
    }

}
