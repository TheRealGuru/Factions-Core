package gg.revival.factions.core.events.task;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.events.obj.Event;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;

public class EventScheduler extends BukkitRunnable implements Runnable {

    @Getter private FC core;

    public EventScheduler(FC core) {
        this.core = core;
    }

    @Override
    public void run() {
        if(core.getEvents().getEventManager() == null || core.getEvents().getEventManager().getEvents().isEmpty() || !core.getConfiguration().automateEvents) return;

        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int hr = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);

        for(Event events : core.getEvents().getEventManager().getEvents()) {
            if(events.isActive() || !events.getSchedule().containsKey(day)) continue;

            int foundHr = events.getSchedule().get(day).getKey();
            int foundMin = events.getSchedule().get(day).getValue();

            if(foundHr != hr || foundMin != min) continue;

            new BukkitRunnable() {
                public void run() {
                    core.getEvents().getEventManager().startEvent(events);
                }
            }.runTask(core);
        }
    }
}
