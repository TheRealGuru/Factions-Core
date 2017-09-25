package gg.revival.factions.core.deathbans.command;

import gg.revival.factions.core.deathbans.Deathbans;
import gg.revival.factions.core.tools.OfflinePlayerLookup;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeathsCommand implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[]) {
        if(!command.getName().equalsIgnoreCase("deaths")) return false;

        if(!(sender instanceof Player)) {
            sender.sendMessage("This command can not be ran by console");
            return false;
        }

        Player player = (Player)sender;

        if(args.length == 0) {
            Deathbans.getDeathsByUUID(player.getUniqueId(), result -> {
                if(result.isEmpty()) {
                    player.sendMessage(ChatColor.RED + "You do not have any previous deathbans");
                    return;
                }

                Deathbans.sendDeathbans(player, player.getName(), result);
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

                Deathbans.getDeathsByUUID(uuid, result -> {
                    if(result.isEmpty()) {
                        player.sendMessage(ChatColor.RED + "This player does not have any previous deathbans");
                        return;
                    }

                    Deathbans.sendDeathbans(player, username, result);
                });
            });

            return false;
        }

        player.sendMessage(ChatColor.RED + "/deaths [player]");

        return false;
    }

}
