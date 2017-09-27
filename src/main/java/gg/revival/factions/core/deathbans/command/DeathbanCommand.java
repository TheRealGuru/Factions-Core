package gg.revival.factions.core.deathbans.command;

import gg.revival.factions.core.deathbans.Deathbans;
import gg.revival.factions.core.tools.Logger;
import gg.revival.factions.core.tools.OfflinePlayerLookup;
import gg.revival.factions.core.tools.Permissions;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeathbanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[]) {
        if(!command.getName().equalsIgnoreCase("deathban")) return false;

        if(sender instanceof Player) {
            Player player = (Player)sender;

            if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return false;
            }
        }

        if(args.length != 2) {
            sender.sendMessage(ChatColor.RED + "/deathban <player> <seconds>");
            return false;
        }

        String namedPlayer = args[0];
        String namedDuration = args[1];

        int duration = NumberUtils.toInt(namedDuration);

        if(!NumberUtils.isNumber(namedDuration)) {
            sender.sendMessage(ChatColor.RED + "/deathban <player> <seconds>");
            return false;
        }

        OfflinePlayerLookup.getOfflinePlayerByName(namedPlayer, (uuid, username) -> {
            if(uuid == null || username == null) {
                sender.sendMessage(ChatColor.RED + "Player not found");
                return;
            }

            String creator = sender.getName();

            Deathbans.getActiveDeathban(uuid, death -> {
                if(death != null) {
                    death.setExpiresTime(System.currentTimeMillis());
                    Deathbans.saveDeathban(death);
                }
            });

            Deathbans.deathbanPlayer(uuid, "Applied by Admin", duration);

            sender.sendMessage(ChatColor.GREEN + "You have deathbanned " + username + " for " + duration + " seconds");

            Logger.log(creator + " has applied a manual deathban to " + username + " for " + duration + " seconds");
        });

        return false;
    }

}
