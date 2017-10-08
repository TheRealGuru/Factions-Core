package gg.revival.factions.core.stats.command;

import gg.revival.factions.core.FC;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {

    @Getter private FC core;

    public StatsCommand(FC core) {
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!command.getName().equalsIgnoreCase("statistics")) return false;

        if(!(sender instanceof Player)) {
            sender.sendMessage("This command can not be ran by console");
            return false;
        }

        Player player = (Player)sender;

        if(args.length == 0) {
            core.getStats().getStats(player.getUniqueId(), stats -> {
                if(stats == null) {
                    player.sendMessage(ChatColor.RED + "Player not found");
                    return;
                }

                player.sendMessage(core.getStats().getFormattedStats(stats, player.getName()));
            });

            return false;
        }

        if(args.length == 1) {
            String namedPlayer = args[0];

            core.getOfflinePlayerLookup().getOfflinePlayerByName(namedPlayer, (uuid, username) -> {
                if(uuid == null || username == null) {
                    player.sendMessage(ChatColor.RED + "Player not found");
                    return;
                }

                core.getStats().getStats(uuid, stats -> {
                    if(stats == null) {
                        player.sendMessage(ChatColor.RED + "Player not found");
                        return;
                    }

                    player.sendMessage(core.getStats().getFormattedStats(stats, username));
                });
            });

            return false;
        }

        player.sendMessage(ChatColor.RED + "/stats");
        player.sendMessage(ChatColor.RED + "/stats [player]");

        return false;
    }

}
