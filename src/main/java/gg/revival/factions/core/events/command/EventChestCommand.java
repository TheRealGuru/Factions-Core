package gg.revival.factions.core.events.command;

import gg.revival.factions.core.events.loot.EventChest;
import gg.revival.factions.core.events.loot.EventChestManager;
import gg.revival.factions.core.events.loot.EventChestType;
import gg.revival.factions.core.events.loot.LootTableManager;
import gg.revival.factions.core.tools.BlockTools;
import gg.revival.factions.core.tools.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EventChestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!command.getName().equalsIgnoreCase("eventchest")) return false;

        if(!(sender instanceof Player)) {
            sender.sendMessage("This command can not be ran through console");
            return false;
        }

        Player player = (Player)sender;

        if(!player.hasPermission(Permissions.CORE_ADMIN)) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
            return false;
        }

        if(args.length == 0) {
            player.sendMessage(ChatColor.RED + "/eventchest create <type> <lootTable>");
            player.sendMessage(ChatColor.RED + "/eventchest delete");
            return false;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("delete")) {
                Block targetBlock = BlockTools.getTargetBlock(player, 5);

                if(targetBlock == null || EventChestManager.getEventChestAtLocation(targetBlock.getLocation()) == null) {
                    player.sendMessage(ChatColor.RED + "You are not looking at an Event Chest");
                    return false;
                }

                EventChest foundChest = EventChestManager.getEventChestAtLocation(targetBlock.getLocation());
                EventChestManager.deleteEventChest(foundChest);

                for(Entity nearbyEntities : player.getNearbyEntities(2, 2, 2)) {
                    if(!(nearbyEntities instanceof ArmorStand)) continue;
                    nearbyEntities.remove();
                }

                player.sendMessage(ChatColor.GREEN + "Deleted Event Chest");
                return false;
            }

            player.sendMessage(ChatColor.RED + "/eventchest delete");
            return false;
        }

        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("create")) {
                String namedType = args[1];
                String namedLootTable = args[2];

                if(LootTableManager.getLootTableByName(namedLootTable) == null) {
                    player.sendMessage(ChatColor.RED + "Loot table not found. Use '/tables' to view all configured loot tables");
                    return false;
                }

                Block targetBlock = BlockTools.getTargetBlock(player, 5);

                if(targetBlock == null || !targetBlock.getType().equals(Material.CHEST)) {
                    player.sendMessage(ChatColor.RED + "You are not looking at a valid chest");
                    return false;
                }

                EventChestType type = null;

                switch(namedType.toUpperCase()) {
                    case "RARE": type = EventChestType.RARE;
                        break;
                    case "COMBAT": type = EventChestType.COMBAT;
                        break;
                    case "BREWING": type = EventChestType.BREWING;
                        break;
                }

                if(type == null) {
                    player.sendMessage(ChatColor.RED + "Invalid Event Chest type");
                    return false;
                }

                EventChest eventChest = new EventChest(UUID.randomUUID(), type, targetBlock.getLocation(), namedLootTable);
                EventChestManager.createEventChest(eventChest);

                player.sendMessage(ChatColor.GREEN + "Event Chest Created");

                return false;
            }

            player.sendMessage(ChatColor.RED + "/eventchest create <type> <lootTable>");

            return false;
        }

        return false;
    }

}
