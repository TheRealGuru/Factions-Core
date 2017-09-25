package gg.revival.factions.core.events.command;

import com.google.common.base.Joiner;
import gg.revival.factions.core.events.loot.LootTableManager;
import gg.revival.factions.core.tools.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class LootTablesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!command.getName().equalsIgnoreCase("tables")) return false;

        if(!(sender instanceof Player)) {
            sender.sendMessage("This command can not be ran through console");
            return false;
        }

        Player player = (Player)sender;

        if(args.length == 0) {
            List<String> tables = new ArrayList<>(LootTableManager.getLootTables().keySet());

            player.sendMessage(ChatColor.YELLOW + "Here is a list of valid loot tables: ");
            player.sendMessage(ChatColor.AQUA + Joiner.on(ChatColor.YELLOW + ", ").join(tables));

            return false;
        }

        if(args.length == 1) {
            String namedTable = args[0];

            Inventory lootTable = LootTableManager.getLootTableByName(namedTable);

            if(lootTable == null) {
                List<String> tables = new ArrayList<>(LootTableManager.getLootTables().keySet());

                player.sendMessage(ChatColor.RED + "Invalid loot table");
                player.sendMessage(ChatColor.YELLOW + "Here is a list of valid loot tables: ");
                player.sendMessage(ChatColor.AQUA + Joiner.on(ChatColor.YELLOW + ", ").join(tables));

                return false;
            }

            LootTableManager.openLootTable(player, lootTable);
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("update")) {
                if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    return false;
                }

                String namedTable = args[1];

                if(LootTableManager.getLootTableByName(namedTable) != null)
                    LootTableManager.openLootTableEditor(player, namedTable, LootTableManager.getLootTableByName(namedTable));
                else
                    LootTableManager.openLootTableEditor(player, namedTable, null);

                player.sendMessage(ChatColor.GREEN + "Now editing table '" + namedTable + "'");

                return false;
            }

            if(args[0].equalsIgnoreCase("delete")) {
                if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    return false;
                }

                String namedTable = args[1];

                if(LootTableManager.getLootTableByName(namedTable) == null) {
                    player.sendMessage(ChatColor.RED + "Loot table not found");
                    return false;
                }

                LootTableManager.deleteLootTable(namedTable);
                player.sendMessage(ChatColor.GREEN + "Loot table deleted");

                return false;
            }

            if(player.hasPermission(Permissions.CORE_ADMIN)) {
                player.sendMessage(ChatColor.RED + "/table create <name>");
                player.sendMessage(ChatColor.RED + "/table update <name>");
                player.sendMessage(ChatColor.RED + "/table delete <name>");
            } else {
                player.sendMessage(ChatColor.RED + "/table [lootTable]");
            }

            return false;
        }

        return false;
    }

}
