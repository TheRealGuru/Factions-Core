package gg.revival.factions.core.events.command;

import com.google.common.base.Joiner;
import gg.revival.factions.core.events.builder.DTCBuilder;
import gg.revival.factions.core.events.builder.EventBuilder;
import gg.revival.factions.core.events.builder.KOTHBuilder;
import gg.revival.factions.core.events.chests.ChestManager;
import gg.revival.factions.core.events.chests.ClaimChest;
import gg.revival.factions.core.events.chests.ClaimChestType;
import gg.revival.factions.core.events.chests.LootTables;
import gg.revival.factions.core.events.engine.EventManager;
import gg.revival.factions.core.events.gui.EventsGUI;
import gg.revival.factions.core.events.messages.EventsMessages;
import gg.revival.factions.core.events.obj.Event;
import gg.revival.factions.core.tools.BlockTools;
import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.core.tools.FileManager;
import gg.revival.factions.core.tools.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

            if(args[0].equalsIgnoreCase("chest") && args[1].equalsIgnoreCase("delete")) {
                if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    return false;
                }

                Block targetBlock = BlockTools.getTargetBlock(player, 4);

                if(targetBlock == null || ChestManager.getClaimChestByLocation(targetBlock.getLocation()) == null) {
                    player.sendMessage(ChatColor.RED + "You are not looking at a Claim Chest");
                    return false;
                }

                ClaimChest claimChest = ChestManager.getClaimChestByLocation(targetBlock.getLocation());

                if(claimChest != null && claimChest.getAboveArmorStand() != null)
                    claimChest.getAboveArmorStand().remove();

                ChestManager.deleteChest(claimChest);

                player.sendMessage(ChatColor.GREEN + "Claim chest deleted");

                return false;
            }
        }

        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("loot")) {
                if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    return false;
                }

                if(args[1].equalsIgnoreCase("create") || args[1].equalsIgnoreCase("update") || args[1].equalsIgnoreCase("edit")) {
                    String namedTable = args[2];

                    LootTables.openEditor(player, namedTable);

                    return false;
                }

                if(args[1].equalsIgnoreCase("delete")) {
                    String namedTable = args[2];

                    if(LootTables.getLootTableByName(namedTable) == null) {
                        player.sendMessage(ChatColor.RED + "Invalid loot table. Valid loot tables" + ChatColor.WHITE + ": " + Joiner.on(", ").join(LootTables.getLootTables().keySet()));
                        return false;
                    }

                    LootTables.deleteLootTable(namedTable);
                    player.sendMessage(ChatColor.RED + "Loot table deleted." + ChatColor.DARK_RED + " Warning: All event chests connected to this table are now fucked and you need to remove them.");
                    return false;
                }
            }
        }

        if(args.length == 4) {
            if(args[0].equalsIgnoreCase("chest") && args[1].equalsIgnoreCase("create")) {
                if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    return false;
                }

                String namedType = args[2];
                String namedTable = args[3];
                ClaimChestType type = null;
                Block targetBlock = BlockTools.getTargetBlock(player, 4);

                for(ClaimChestType claimChestTypes : ClaimChestType.values())
                    if(claimChestTypes.toString().equalsIgnoreCase(namedType)) type = claimChestTypes;

                if(type == null) {
                    player.sendMessage(ChatColor.RED + "Invalid claim chest type. Valid types" + ChatColor.WHITE + ": " + Joiner.on(", ").join(ClaimChestType.values()));
                    return false;
                }

                if(LootTables.getLootTableByName(namedTable) == null) {
                    player.sendMessage(ChatColor.RED + "Invalid loot table. Valid tables" + ChatColor.WHITE + ": " + Joiner.on(", ").join(LootTables.getLootTables().keySet()));
                    return false;
                }

                if(targetBlock == null || !targetBlock.getType().equals(Material.CHEST)) {
                    player.sendMessage(ChatColor.RED + "You are not looking at a chest");
                    return false;
                }

                type = ClaimChestType.valueOf(namedType.toUpperCase());

                ClaimChest claimChest = new ClaimChest(UUID.randomUUID(), targetBlock.getLocation(), namedTable, type);
                ChestManager.createChest(claimChest);

                ArmorStand nameplate = (ArmorStand)targetBlock.getLocation().getWorld().spawnEntity(
                        new Location(targetBlock.getWorld(), targetBlock.getX() + 0.5, targetBlock.getY() - 1, targetBlock.getZ() + 0.5), EntityType.ARMOR_STAND);

                switch(type) {
                    case RARE: nameplate.setCustomName(ChatColor.GRAY + "[ " + ChatColor.DARK_GREEN + "Claim Rare Loot" + ChatColor.GRAY + " ]");
                        break;
                    case COMBAT: nameplate.setCustomName(ChatColor.GRAY + "[ " + ChatColor.DARK_RED + "Claim Combat Loot" + ChatColor.GRAY + " ]");
                        break;
                    case BREWING: nameplate.setCustomName(ChatColor.GRAY + "[ " + ChatColor.AQUA + "Claim Brewing Loot" + ChatColor.GRAY + " ]");
                        break;
                    default: nameplate.setCustomName(ChatColor.RED + "Error");
                }

                nameplate.setVisible(false);
                nameplate.setCustomNameVisible(true);

                player.sendMessage(ChatColor.GREEN + "Claim Chest created");

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
            player.sendMessage(ChatColor.RED + "/event chest delete");
            player.sendMessage(ChatColor.RED + "/event chest create <type> <table>");
        }

        return false;
    }

}
