package gg.revival.factions.core.events.messages;

import gg.revival.factions.core.events.obj.DTCEvent;
import gg.revival.factions.core.events.obj.Event;
import gg.revival.factions.core.events.obj.KOTHEvent;
import gg.revival.factions.obj.PlayerFaction;
import org.bukkit.ChatColor;

public class EventsMessages {

    public static String asKOTH(String message) {
        return ChatColor.GOLD + "[King of the Hill] " + ChatColor.RESET + message;
    }

    public static String asDTC(String message) {
        return ChatColor.GOLD + "[Destroy the Core] " + ChatColor.RESET + message;
    }

    public static String asPalace(String message) {
        return ChatColor.GOLD + "[Palace] " + ChatColor.RESET + message;
    }

    public static String asEOTW(String message) {
        return ChatColor.GOLD + "[End of the World] " + ChatColor.RESET + message;
    }

    public static String nowControlling(Event event) {
        return ChatColor.GOLD + "You" + ChatColor.YELLOW + " are now controlling " + event.getDisplayName();
    }

    public static String controlLost(PlayerFaction controller, Event event) {
        return ChatColor.GOLD + controller.getDisplayName() + ChatColor.YELLOW + " lost control of " + event.getDisplayName();
    }

    public static String beingControlled(PlayerFaction controller, Event event) {
        return event.getDisplayName() + ChatColor.YELLOW + " is being controlled by " + ChatColor.GOLD + controller.getDisplayName();
    }

    public static String beingContested(Event event) {
        return event.getDisplayName() + ChatColor.YELLOW + " is being " + ChatColor.RED + "contested";
    }

    public static String ticked(Event event) {
        if(event instanceof KOTHEvent) {
            KOTHEvent koth = (KOTHEvent)event;

            return ChatColor.GOLD + koth.getCappingFaction().getDisplayName() + ChatColor.YELLOW + " has gained a ticket for controlling " + event.getDisplayName() + ChatColor.GOLD +
                    " [" + ChatColor.YELLOW + koth.getTickets().get(koth.getCappingFaction()) + ChatColor.GOLD + "/" + ChatColor.YELLOW + koth.getWinCond() + ChatColor.GOLD + "]";
        }

        if(event instanceof DTCEvent) {
            DTCEvent dtc = (DTCEvent)event;

            return ChatColor.GOLD + dtc.getCappingFaction().getDisplayName() + ChatColor.YELLOW + " is destroying the core at " + event.getDisplayName() + ChatColor.GOLD +
                    " [" + ChatColor.YELLOW + dtc.getTickets().get(dtc.getCappingFaction()) + ChatColor.GOLD + "/" + ChatColor.YELLOW + dtc.getWinCond() + ChatColor.GOLD + "]";
        }

        return null;
    }

    public static String started(Event event) {
        return event.getDisplayName() + ChatColor.YELLOW + " has started";
    }

    public static String stopped(Event event) {
        return event.getDisplayName() + ChatColor.YELLOW + " has stopped";
    }

    public static String captured(Event event) {
        if(event instanceof KOTHEvent) {
            KOTHEvent koth = (KOTHEvent)event;

            return event.getDisplayName() + ChatColor.YELLOW + " has been captured by " + ChatColor.GOLD + koth.getCappingFaction().getDisplayName();
        }

        if(event instanceof DTCEvent) {
            DTCEvent dtc = (DTCEvent)event;

            return event.getDisplayName() + ChatColor.YELLOW + " has been destroyed by " + ChatColor.GOLD + dtc.getCappingFaction().getDisplayName();
        }

        return null;
    }

}
