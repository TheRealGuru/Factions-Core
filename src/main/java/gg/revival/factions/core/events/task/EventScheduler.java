package gg.revival.factions.core.events.task;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.events.engine.EventManager;
import gg.revival.factions.core.events.obj.Event;
import gg.revival.factions.core.tools.Configuration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;

public class EventScheduler extends BukkitRunnable implements Runnable {

    @Override
    public void run() {
        if(EventManager.getEvents().isEmpty() || !Configuration.automateEvents) return;

        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int hr = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);

        for(Event events : EventManager.getEvents()) {
            if(events.isActive() || !events.getSchedule().containsKey(day)) continue;

            int foundHr = events.getSchedule().get(day).keySet().iterator().next();
            int foundMin = events.getSchedule().get(day).get(foundHr);

            if(foundHr != hr || foundMin != min) continue;

            new BukkitRunnable() {
                public void run() {
                    EventManager.startEvent(events);
                }
            }.runTask(FC.getFactionsCore());
        }
    }
}
