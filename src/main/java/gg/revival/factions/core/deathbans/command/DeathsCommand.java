package gg.revival.factions.core.deathbans.command;

import gg.revival.factions.core.FC;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeathsCommand implements CommandExecutor {

    @Getter private FC core;

    public DeathsCommand(FC core) {
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[]) {
        if(!command.getName().equalsIgnoreCase("deaths")) return false;

        if(!(sender instanceof Player)) {
            sender.sendMessage("This command can not be ran by console");
            return false;
        }

        Player player = (Player)sender;

        if(args.length == 0) {
            core.getDeathbans().getDeathsByUUID(player.getUniqueId(), result -> {
                if(result.isEmpty()) {
                    player.sendMessage(ChatColor.RED + "You do not have any previous deathbans");
                    return;
                }

                core.getDeathbans().sendDeathbans(player, player.getName(), result);
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

                core.getDeathbans().getDeathsByUUID(uuid, result -> {
                    if(result.isEmpty()) {
                        player.sendMessage(ChatColor.RED + "This player does not have any previous deathbans");
                        return;
                    }

                    core.getDeathbans().sendDeathbans(player, username, result);
                });
            });

            return false;
        }

        player.sendMessage(ChatColor.RED + "/deaths [player]");

        return false;
    }

}
