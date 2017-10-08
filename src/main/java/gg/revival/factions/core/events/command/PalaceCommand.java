package gg.revival.factions.core.events.command;

import com.google.common.base.Joiner;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.events.chests.EventChest;
import gg.revival.factions.core.events.chests.PalaceChest;
import gg.revival.factions.core.tools.BlockTools;
import gg.revival.factions.core.tools.Permissions;
import gg.revival.factions.obj.PlayerFaction;
import lombok.Getter;
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

    @Getter private FC core;

    public PalaceCommand(FC core) {
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!command.getName().equalsIgnoreCase("palace")) return false;

        if(args.length == 0) {
            if(core.getEvents().getPalaceManager().isCaptured())
                sender.sendMessage(ChatColor.BLUE + "Palace" + ChatColor.YELLOW + " is currently controlled by " + ChatColor.GREEN + core.getEvents().getPalaceManager().getCappedFaction().getDisplayName());
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

                if(!core.getEvents().getPalaceManager().isCaptured()) {
                    sender.sendMessage(ChatColor.RED + "Palace is not being controlled");
                    return false;
                }

                core.getEvents().getPalaceManager().resetPalace();
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

                core.getEvents().getPalaceManager().setCappers(faction);

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

                if(targetBlock == null || core.getEvents().getChestManager().getEventChestByLocation(targetBlock.getLocation()) == null) {
                    player.sendMessage(ChatColor.RED + "You are not looking at a Palace Chest");
                    return false;
                }

                EventChest eventChest = core.getEvents().getChestManager().getEventChestByLocation(targetBlock.getLocation());

                if(!(eventChest instanceof PalaceChest)) {
                    player.sendMessage(ChatColor.RED + "This is indeed an Event chest, but not a Palace chest");
                    return false;
                }

                core.getEvents().getChestManager().deleteChest(eventChest);
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

                if(core.getEvents().getLootTables().getLootTableByName(namedTable) == null) {
                    player.sendMessage(ChatColor.RED + "Invalid loot table. Valid loot tables" + ChatColor.WHITE + ": " + Joiner.on(", ").join(core.getEvents().getLootTables().getLootTables().keySet()));
                    return false;
                }

                int tier = NumberUtils.toInt(namedTier);

                PalaceChest palaceChest = new PalaceChest(UUID.randomUUID(), targetBlock.getLocation(), namedTable, tier);
                core.getEvents().getChestManager().createChest(palaceChest);

                player.sendMessage(ChatColor.GREEN + "Palace chest created");

                return false;
            }

            return false;
        }

        return false;
    }
}
