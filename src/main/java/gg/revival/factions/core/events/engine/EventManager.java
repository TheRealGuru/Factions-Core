package gg.revival.factions.core.events.engine;

import com.google.common.collect.Maps;
import gg.revival.factions.core.events.obj.BeaconEvent;
import gg.revival.factions.core.events.obj.DTCEvent;
import gg.revival.factions.core.events.obj.Event;
import gg.revival.factions.core.events.obj.KOTHEvent;
import gg.revival.factions.obj.PlayerFaction;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventManager {

    @Getter static Set<Event> events = new HashSet<>();

    public static Event getEventByName(String name) {
        List<Event> cache = new CopyOnWriteArrayList<>(events);

        for(Event event : cache) {
            if(event.getEventName().equalsIgnoreCase(name))
                return event;
        }

        return null;
    }

    public static Event getEventByLootChest(Location lootChestLocation) {
        List<Event> cache = new CopyOnWriteArrayList<>(events);

        for(Event event : cache) {
            if(event.getLootChest().equals(lootChestLocation))
                return event;
        }

        return null;
    }

    public static Set<Event> getActiveEvents() {
        List<Event> cache = new CopyOnWriteArrayList<>(events);
        Set<Event> result = new HashSet<>();

        for(Event event : cache) {
            if(event.isActive())
                result.add(event);
        }

        return result;
    }

    public static void startEvent(Event event) {
        if(event.isActive()) return;

        event.setActive(true);

        if(event instanceof KOTHEvent) {
            KOTHEvent koth = (KOTHEvent)event;

            koth.setNextTicketTime(-1L);
            koth.setCappingFaction(null);
            koth.getTickets().clear();
        }

        if(event instanceof DTCEvent) {
            DTCEvent dtc = (DTCEvent)event;

            dtc.setResetTime(-1L);
            dtc.setCappingFaction(null);
            dtc.getTickets().clear();
        }

        if(event instanceof BeaconEvent) {
            BeaconEvent beacon = (BeaconEvent)event;

            beacon.getTickets().clear();
        }
    }

    public static void stopEvent(Event event) {

    }

    public static void finishEvent(Event event) {
        if(!event.isActive()) return;

        event.setActive(false);

        PlayerFaction winner = null;

        if(event instanceof KOTHEvent) {
            KOTHEvent koth = (KOTHEvent)event;

            koth.setNextTicketTime(-1L);
            koth.setContested(false);
            koth.getTickets().clear();

            winner = koth.getCappingFaction();
        }

        if(event instanceof DTCEvent) {
            DTCEvent dtc = (DTCEvent)event;

            dtc.setResetTime(-1L);
            dtc.getTickets().clear();

            winner = dtc.getCappingFaction();
        }

        if(event instanceof BeaconEvent) {
            BeaconEvent beacon = (BeaconEvent)event;

            beacon.getTickets().clear();

            winner = beacon.getTicketsInOrder().keySet().iterator().next();
        }
    }

    public static void tickEvent(Event event) {
        if(event instanceof KOTHEvent) {
            KOTHEvent koth = (KOTHEvent)event;
            PlayerFaction capper = koth.getCappingFaction();

            Map<PlayerFaction, Integer> ticketCache = new HashMap<>(koth.getTickets());

            koth.getTickets().clear();

            for(PlayerFaction factions : ticketCache.keySet()) {
                int newTicketCount = ticketCache.get(factions);

                if(factions.getFactionID().equals(capper.getFactionID())) {
                    newTicketCount += 1;
                } else {
                    newTicketCount -= 1;
                }

                if(newTicketCount >= koth.getWinCond()) {
                    finishEvent(event);
                    return;
                }

                if(newTicketCount <= 0) continue;

                koth.getTickets().put(factions, newTicketCount);
            }
        }

        if(event instanceof DTCEvent) {
            DTCEvent dtc = (DTCEvent)event;
            PlayerFaction capper = dtc.getCappingFaction();

            Map<PlayerFaction, Integer> ticketCache = new HashMap<>(dtc.getTickets());

            dtc.getTickets().clear();

            for(PlayerFaction factions : ticketCache.keySet()) {
                int newTicketCount = ticketCache.get(factions);

                if(factions.getFactionID().equals(capper.getFactionID())) {
                    newTicketCount += 1;
                } else {
                    newTicketCount -= 1;
                }

                if(newTicketCount >= dtc.getWinCond()) {
                    finishEvent(event);
                    return;
                }

                if(newTicketCount <= 0) continue;

                dtc.getTickets().put(factions, newTicketCount);
            }
        }

        if(event instanceof BeaconEvent) {
            BeaconEvent beacon = (BeaconEvent)event;

            Map<PlayerFaction, Integer> ticketCache = new HashMap<>(beacon.getTickets());

            beacon.getTickets().clear();

            for(PlayerFaction factions : ticketCache.keySet()) {
                int points = ticketCache.get(factions);
                int toAdd = 0;

                for(UUID roster : factions.getRoster(true)) {
                    Player player = Bukkit.getPlayer(roster);

                    if(beacon.isInRange(player.getLocation()))
                        toAdd++;
                }

                if((points + toAdd) >= beacon.getWinCond()) {
                    finishEvent(event);
                    return;
                }

                if(toAdd == 0) {
                    points -= 1;

                    if(points <= 0) continue;

                    beacon.getTickets().put(factions, points);

                    continue;
                }

                beacon.getTickets().put(factions, (points + toAdd));
            }
        }
    }

}
