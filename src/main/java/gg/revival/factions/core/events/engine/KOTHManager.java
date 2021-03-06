package gg.revival.factions.core.events.engine;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.events.obj.Event;
import gg.revival.factions.core.events.obj.KOTHEvent;
import gg.revival.factions.obj.Faction;
import gg.revival.factions.obj.PlayerFaction;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class KOTHManager {

    @Getter private FC core;

    public KOTHManager(FC core) {
        this.core = core;
    }

    public Set<KOTHEvent> getActiveKOTHEvents() {
        List<Event> cache = new CopyOnWriteArrayList<>(core.getEvents().getEventManager().getActiveEvents());
        Set<KOTHEvent> result = new HashSet<>();

        for(Event event : cache) {
            if(!(event instanceof KOTHEvent)) continue;

            KOTHEvent koth = (KOTHEvent)event;

            result.add(koth);
        }

        return result;
    }

    public boolean shouldBeContested(Set<UUID> capzonePlayers) {
        if(capzonePlayers.size() <= 1) return false;

        Set<Faction> factions = new HashSet<>();

        for(UUID uuid : capzonePlayers) {
            if(Bukkit.getPlayer(uuid) == null) continue;

            Faction faction = FactionManager.getFactionByPlayer(uuid);

            if(faction == null || !(faction instanceof PlayerFaction)) continue;

            if(factions.contains(faction)) continue;

            factions.add(faction);
        }

        return factions.size() > 1;
    }

    public void updateCapTimer(KOTHEvent event) {
        event.setNextTicketTime(System.currentTimeMillis() + (event.getDuration() * 1000L));
    }
}
