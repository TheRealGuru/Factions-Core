package gg.revival.factions.core.events.command;

import com.google.common.base.Joiner;
import gg.revival.factions.core.events.builder.DTCBuilder;
import gg.revival.factions.core.events.builder.EventBuilder;
import gg.revival.factions.core.events.builder.KOTHBuilder;
import gg.revival.factions.core.events.engine.EventManager;
import gg.revival.factions.core.events.messages.EventsMessages;
import gg.revival.factions.core.events.obj.Event;
import gg.revival.factions.core.events.obj.EventsGUI;
import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.core.tools.FileManager;
import gg.revival.factions.core.tools.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EventsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[]) {
        if(!command.getName().equalsIgnoreCase("events")) return false;

        if(!(sender instanceof Player)) return false;

        Player player = (Player)sender;

        if(args.length == 0) {
            EventsGUI.open(player);
            return false;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("toggleauto")) {
                if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    return false;
                }

                Configuration.automateEvents = !Configuration.automateEvents;
                FileManager.getEvents().set("configuration.automated", Configuration.automateEvents);
                FileManager.saveEvents();

                if(Configuration.automateEvents)
                    player.sendMessage(ChatColor.GREEN + "Events will now run automatically");
                else
                    player.sendMessage(ChatColor.GREEN + "Events will no longer run automatically");

                return false;
            }

            String namedEvent = args[0];

            if(EventManager.getEventByName(namedEvent) == null) {
                player.sendMessage(ChatColor.RED + "Event not found, here's a list of valid events: ");

                List<String> eventNames = new ArrayList<>();

                for(Event events : EventManager.getActiveEvents()) {
                    eventNames.add(events.getEventName());
                }

                player.sendMessage(ChatColor.BLUE + Joiner.on(ChatColor.RED + ", ").join(eventNames));

                return false;
            }

            Event event = EventManager.getEventByName(namedEvent);
            player.sendMessage(EventsMessages.eventInfo(event));

            return false;
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("start")) {
                if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    return false;
                }

                String namedEvent = args[1];

                if(EventManager.getEventByName(namedEvent) == null) {
                    player.sendMessage(ChatColor.RED + "Event not found");
                    return false;
                }

                Event event = EventManager.getEventByName(namedEvent);

                if(event.isActive()) {
                    player.sendMessage(ChatColor.RED + "Event is already active");
                    return false;
                }

                EventManager.startEvent(event);
                player.sendMessage(ChatColor.GREEN + "Event started");

                return false;
            }

            if(args[0].equalsIgnoreCase("stop")) {
                if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    return false;
                }

                String namedEvent = args[1];

                if(EventManager.getEventByName(namedEvent) == null) {
                    player.sendMessage(ChatColor.RED + "Event not found");
                    return false;
                }

                Event event = EventManager.getEventByName(namedEvent);

                if(!event.isActive()) {
                    player.sendMessage(ChatColor.RED + "Event is already inactive");
                    return false;
                }

                EventManager.stopEvent(event);
                player.sendMessage(ChatColor.GREEN + "Event stopped");

                return false;
            }

            if(args[0].equalsIgnoreCase("delete")) {
                if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    return false;
                }

                String namedEvent = args[1];

                if(EventManager.getEventByName(namedEvent) == null) {
                    player.sendMessage(ChatColor.RED + "Event not found");
                    return false;
                }

                return false;
            }

            if(args[0].equalsIgnoreCase("create")) {
                if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    return false;
                }

                String namedEventType = args[1];

                if(namedEventType.equalsIgnoreCase("KOTH")) {
                    if(EventBuilder.getKOTHBuilder(player.getUniqueId()) != null) {
                        player.sendMessage(ChatColor.RED + "You are already building an event. Finish building that one to start a new one.");
                        return false;
                    }

                    KOTHBuilder builder = new KOTHBuilder(Configuration.defaultKothDuration, Configuration.defaultKothWinCondition);
                    EventBuilder.getKothBuilders().put(player.getUniqueId(), builder);
                    player.sendMessage(builder.getPhaseResponse());

                    return false;
                }

                if(namedEventType.equalsIgnoreCase("DTC")) {
                    if(EventBuilder.getDTCBuilder(player.getUniqueId()) != null) {
                        player.sendMessage(ChatColor.RED + "You are already building an event. Finish building that one to start a new one.");
                        return false;
                    }

                    DTCBuilder builder = new DTCBuilder(Configuration.defaultDtcRegen, Configuration.defaultDtcWincond);
                    EventBuilder.getDtcBuilders().put(player.getUniqueId(), builder);
                    player.sendMessage(builder.getPhaseResponse());
                    return false;
                }

                player.sendMessage(ChatColor.RED + "Invalid event type");

                return false;
            }
        }

        player.sendMessage(ChatColor.RED + "/event");
        player.sendMessage(ChatColor.RED + "/event <event>");

        if(player.hasPermission(Permissions.CORE_ADMIN)) {
            player.sendMessage(ChatColor.RED + "/event toggleauto");
            player.sendMessage(ChatColor.RED + "/event start <event>");
            player.sendMessage(ChatColor.RED + "/event stop <event>");
            player.sendMessage(ChatColor.RED + "/event delete <event>");
            player.sendMessage(ChatColor.RED + "/event create <koth/dtc>");
        }

        return false;
    }

}
