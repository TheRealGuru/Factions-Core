package gg.revival.factions.core.events.task;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.events.obj.DTCEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class DTCTask extends BukkitRunnable {

    @Getter private FC core;

    public DTCTask(FC core) {
        this.core = core;
    }

    @Override
    public void run() {
        if(core.getEvents().getDtcManager() == null) return;

        for(DTCEvent dtc : core.getEvents().getDtcManager().getActiveDTCEvents()) {
            if(dtc.getRecentBreaker() != null) {
                if(dtc.getCappingFaction() != null && !dtc.getCappingFaction().equals(dtc.getRecentBreaker())) {
                    if(dtc.isPalace())
                        Bukkit.broadcastMessage(core.getEvents().getEventMessages().asPalace(core.getEvents().getEventMessages().controlLost(dtc.getCappingFaction(), dtc)));
                    else
                        Bukkit.broadcastMessage(core.getEvents().getEventMessages().asDTC(core.getEvents().getEventMessages().controlLost(dtc.getCappingFaction(), dtc)));

                    dtc.getTickets().clear();
                }

                dtc.setCappingFaction(dtc.getRecentBreaker());
                core.getEvents().getEventManager().tickEvent(dtc);

                dtc.setRecentBreaker(null);
                core.getEvents().getDtcManager().updateResetTimer(dtc);
            }

            if(dtc.getResetTime() != -1L && dtc.getResetTime() <= System.currentTimeMillis()) {
                if(dtc.getCappingFaction() != null) {
                    if(dtc.isPalace())
                        Bukkit.broadcastMessage(core.getEvents().getEventMessages().asPalace(core.getEvents().getEventMessages().controlLost(dtc.getCappingFaction(), dtc)));
                    else
                        Bukkit.broadcastMessage(core.getEvents().getEventMessages().asDTC(core.getEvents().getEventMessages().controlLost(dtc.getCappingFaction(), dtc)));
                }

                dtc.setResetTime(-1L);
                dtc.getTickets().clear();;
                dtc.setRecentBreaker(null);
                dtc.setCappingFaction(null);
            }
        }
    }

}
