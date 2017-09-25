package gg.revival.factions.core.events.command;

import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.events.loot.LootTableManager;
import gg.revival.factions.core.events.palace.PalaceChest;
import gg.revival.factions.core.events.palace.PalaceManager;
import gg.revival.factions.core.tools.BlockTools;
import gg.revival.factions.core.tools.Permissions;
import gg.revival.factions.obj.Faction;
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

        if(!(sender instanceof Player)) {
            sender.sendMessage("This command can not be ran by console");
            return false;
        }

        Player player = (Player)sender;

        if(args.length == 0) {
            if(PalaceManager.isCaptured() && PalaceManager.getCappingFaction() != null) {
                player.sendMessage(ChatColor.GOLD + "Palace" + ChatColor.YELLOW + " has been captured by " + ChatColor.DARK_GREEN + PalaceManager.getCappingFaction().getDisplayName());
            } else {
                player.sendMessage(ChatColor.GOLD + "Palace" + ChatColor.YELLOW + " is currently neutral");
            }

            player.sendMessage(ChatColor.BLUE + "Palace security level" + ChatColor.WHITE + ": " + PalaceManager.getPublicChestLevel());

            return false;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("deletechest")) {
                if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    return false;
                }

                Block targetBlock = BlockTools.getTargetBlock(player, 3);

                if(targetBlock == null || !targetBlock.getType().equals(Material.CHEST) || PalaceManager.getPalaceChestByLocation(targetBlock.getLocation()) == null) {
                    player.sendMessage(ChatColor.RED + "You are not looking at a Palace chest");
                    return false;
                }

                PalaceChest palaceChest = PalaceManager.getPalaceChestByLocation(targetBlock.getLocation());

                PalaceManager.deletePalaceChest(palaceChest);
                PalaceManager.getPalaceChests().remove(palaceChest);

                player.sendMessage(ChatColor.GREEN + "You have deleted this Palace chest");

                return false;
            }

            if(args[0].equalsIgnoreCase("respawnloot")) {
                if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    return false;
                }

                PalaceManager.spawnLoot();

                player.sendMessage(ChatColor.GREEN + "You have respawned the Palace loot");

                return false;
            }

            return false;
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("setfaction")) {
                if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    return false;
                }

                String namedFaction = args[1];

                if(FactionManager.getFactionByName(namedFaction) == null) {
                    player.sendMessage(ChatColor.RED + "Faction not found");
                    return false;
                }

                Faction faction = FactionManager.getFactionByName(namedFaction);

                if(!(faction instanceof PlayerFaction)) {
                    player.sendMessage(ChatColor.RED + "Faction not found");
                    return false;
                }

                PlayerFaction playerFaction = (PlayerFaction)faction;

                PalaceManager.setCaptured(true);
                PalaceManager.setCappingFaction(playerFaction);

                player.sendMessage(ChatColor.GREEN + "Palace faction has been updated");
                playerFaction.sendMessage(ChatColor.GREEN + "Your faction has been set as the controlling faction of Palace");

                return false;
            }

            player.sendMessage(ChatColor.RED + "/palace setfaction <faction>");
            return false;
        }

        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("setchest")) {
                if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    return false;
                }

                String namedLevel = args[1];
                String namedLootTable = args[2];

                if(!NumberUtils.isNumber(namedLevel)) {
                    player.sendMessage(ChatColor.RED + "Invalid chest level");
                    return false;
                }

                if(LootTableManager.getLootTableByName(namedLootTable) == null) {
                    player.sendMessage(ChatColor.RED + "Invalid loot table");
                    return false;
                }

                int level = NumberUtils.toInt(namedLevel);
                Block targetBlock = BlockTools.getTargetBlock(player, 3);

                if(targetBlock == null || !targetBlock.getType().equals(Material.CHEST)) {
                    player.sendMessage(ChatColor.RED + "You are not looking at a chest");
                    return false;
                }

                if(PalaceManager.getPalaceChestByLocation(targetBlock.getLocation()) != null) {
                    player.sendMessage(ChatColor.RED + "This is already a Palace chest");
                    return false;
                }

                PalaceChest palaceChest = new PalaceChest(UUID.randomUUID(), level, targetBlock.getLocation(), namedLootTable);

                PalaceManager.savePalaceChest(palaceChest);
                PalaceManager.getPalaceChests().add(palaceChest);

                player.sendMessage(ChatColor.GREEN + "Palace chest created");
            }
        }

        return false;
    }

}
