package gg.revival.factions.core.stats.command;

import gg.revival.factions.core.stats.Stats;
import gg.revival.factions.core.tools.OfflinePlayerLookup;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!command.getName().equalsIgnoreCase("statistics")) return false;

        if(!(sender instanceof Player)) {
            sender.sendMessage("This command can not be ran by console");
            return false;
        }

        Player player = (Player)sender;

        if(args.length == 0) {
            Stats.getStats(player.getUniqueId(), stats -> {
                if(stats == null) {
                    player.sendMessage(ChatColor.RED + "Player not found");
                    return;
                }

                player.sendMessage(Stats.getFormattedStats(stats, player.getName()));
            });

            return false;
        }

        if(args.length == 1) {
            String namedPlayer = args[0];

            OfflinePlayerLookup.getOfflinePlayerByName(namedPlayer, (uuid, username) -> {
                if(uuid == null || username == null) {
                    player.sendMessage(ChatColor.RED + "Player not found");
                    return;
                }

                Stats.getStats(uuid, stats -> {
                    if(stats == null) {
                        player.sendMessage(ChatColor.RED + "Player not found");
                        return;
                    }

                    player.sendMessage(Stats.getFormattedStats(stats, username));
                });
            });

            return false;
        }

        player.sendMessage(ChatColor.RED + "/stats");
        player.sendMessage(ChatColor.RED + "/stats [player]");

        return false;
    }

}
