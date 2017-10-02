package gg.revival.factions.core.events.command;

import com.google.common.base.Joiner;
import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.events.chests.ChestManager;
import gg.revival.factions.core.events.chests.EventChest;
import gg.revival.factions.core.events.chests.LootTables;
import gg.revival.factions.core.events.chests.PalaceChest;
import gg.revival.factions.core.events.engine.PalaceManager;
import gg.revival.factions.core.tools.BlockTools;
import gg.revival.factions.core.tools.Permissions;
import gg.revival.factions.obj.PlayerFaction;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PalaceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!command.getName().equalsIgnoreCase("palace")) return false;

        if(args.length == 0) {
            if(PalaceManager.isCaptured())
                sender.sendMessage(ChatColor.BLUE + "Palace" + ChatColor.YELLOW + " is currently controlled by " + ChatColor.GREEN + PalaceManager.getCappedFaction().getDisplayName());
            else
                sender.sendMessage(ChatColor.BLUE + "Palace" + ChatColor.YELLOW + " is currently not being controlled");

            return false;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("reset")) {
                if(sender instanceof Player) {
                    Player player = (Player)sender;

                    if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                        player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                        return false;
                    }
                }

                if(!PalaceManager.isCaptured()) {
                    sender.sendMessage(ChatColor.RED + "Palace is not being controlled");
                    return false;
                }

                PalaceManager.resetPalace();
                sender.sendMessage(ChatColor.GREEN + "Palace has been reset");

                return false;
            }

            return false;
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("setcapper")) {
                if(sender instanceof Player) {
                    Player player = (Player)sender;

                    if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                        player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                        return false;
                    }
                }

                String namedFaction = args[1];

                if(FactionManager.getFactionByName(namedFaction) == null || !(FactionManager.getFactionByName(namedFaction) instanceof PlayerFaction)) {
                    sender.sendMessage(ChatColor.RED + "Faction not found");
                    return false;
                }

                PlayerFaction faction = (PlayerFaction)FactionManager.getFactionByName(namedFaction);

                PalaceManager.setCappers(faction);

                faction.sendMessage(ChatColor.GREEN + "Your faction is now controlling the Palace");
                sender.sendMessage(ChatColor.GREEN + faction.getDisplayName() + " is now controlling the Palace");

                return false;
            }

            if(args[0].equalsIgnoreCase("chest") && args[1].equalsIgnoreCase("delete")) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage("This command can not be ran through console");
                    return false;
                }

                Player player = (Player)sender;

                if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    return false;
                }

                Block targetBlock = BlockTools.getTargetBlock(player, 4);

                if(targetBlock == null || ChestManager.getEventChestByLocation(targetBlock.getLocation()) == null) {
                    player.sendMessage(ChatColor.RED + "You are not looking at a Palace Chest");
                    return false;
                }

                EventChest eventChest = ChestManager.getEventChestByLocation(targetBlock.getLocation());

                if(!(eventChest instanceof PalaceChest)) {
                    player.sendMessage(ChatColor.RED + "This is indeed an Event chest, but not a Palace chest");
                    return false;
                }

                ChestManager.deleteChest(eventChest);
                player.sendMessage(ChatColor.GREEN + "Palace chest deleted");

                return false;
            }

            return false;
        }

        if(args.length == 4) {
            if(args[0].equalsIgnoreCase("chest") && args[1].equalsIgnoreCase("create")) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage("This command can not be ran through console");
                    return false;
                }

                Player player = (Player)sender;
                String namedTier = args[2];
                String namedTable = args[3];

                if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    return false;
                }

                Block targetBlock = BlockTools.getTargetBlock(player, 4);

                if(targetBlock == null || !targetBlock.getType().equals(Material.CHEST)) {
                    player.sendMessage(ChatColor.RED + "You are not looking at a valid chest");
                    return false;
                }

                if(!NumberUtils.isNumber(namedTier)) {
                    player.sendMessage(ChatColor.RED + "Invalid chest tier. Valid chest tiers" + ChatColor.WHITE + ": 1, 2, 3");
                    return false;
                }

                if(LootTables.getLootTableByName(namedTable) == null) {
                    player.sendMessage(ChatColor.RED + "Invalid loot table. Valid loot tables" + ChatColor.WHITE + ": " + Joiner.on(", ").join(LootTables.getLootTables().keySet()));
                    return false;
                }

                int tier = NumberUtils.toInt(namedTier);

                PalaceChest palaceChest = new PalaceChest(UUID.randomUUID(), targetBlock.getLocation(), namedTable, tier);
                ChestManager.createChest(palaceChest);

                player.sendMessage(ChatColor.GREEN + "Palace chest created");

                return false;
            }

            return false;
        }

        return false;
    }
}
