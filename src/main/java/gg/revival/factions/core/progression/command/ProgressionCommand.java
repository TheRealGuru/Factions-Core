package gg.revival.factions.core.progression.command;

import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.core.tools.Permissions;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.timers.TimerType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProgressionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!command.getName().equalsIgnoreCase("progression")) return false;

        if(!Configuration.progressEnabled) {
            sender.sendMessage(ChatColor.RED + "Progression is disabled on this server");
            return false;
        }

        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                sender.sendMessage("This command can not be ran by console");
                return false;
            }

            Player player = (Player)sender;
            FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

            if(facPlayer.isBeingTimed(TimerType.PROGRESSION)) {
                player.sendMessage(ChatColor.AQUA + "Progression is a system we've developed to discourage cheaters and players using alt-accounts.");
                player.sendMessage("     ");
                player.sendMessage(ChatColor.UNDERLINE + "Once you have finished your Progression, you will be allowed to:");
                player.sendMessage("     ");
                player.sendMessage(ChatColor.YELLOW + " - Enter other factions claims");
                player.sendMessage(ChatColor.YELLOW + " - Enter event claims");
                player.sendMessage("     ");
                player.sendMessage(ChatColor.GREEN + "You can see your current Progression above your hotbar!");

                return false;
            }

            player.sendMessage(ChatColor.GREEN + "You have filled your progression!");

            return false;
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("bypass")) {
                String remover = "Console";

                if(sender instanceof Player) {
                    Player player = (Player) sender;

                    if (!player.hasPermission(Permissions.CORE_ADMIN)) {
                        player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                        return false;
                    }

                    remover = player.getName();
                }

                String namedPlayer = args[1];

                if(Bukkit.getPlayer(namedPlayer) == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found");
                    return false;
                }

                Player progressPlayer = Bukkit.getPlayer(namedPlayer);
                FPlayer fProgressPlayer = PlayerManager.getPlayer(progressPlayer.getUniqueId());

                if(!fProgressPlayer.isBeingTimed(TimerType.PROGRESSION)) {
                    sender.sendMessage(ChatColor.RED + "This player has already finished their progression");
                    return false;
                }

                fProgressPlayer.removeTimer(TimerType.PROGRESSION);
                progressPlayer.sendMessage(ChatColor.YELLOW + "Your progression has been" + ChatColor.GREEN + " bypassed " + ChatColor.YELLOW + " by " + ChatColor.BLUE + remover);
                sender.sendMessage(ChatColor.GREEN + "You have bypassed " + progressPlayer.getName() + "'s Progression");

                return false;
            }
        }

        if(sender instanceof Player) {
            Player player = (Player)sender;

            player.sendMessage(ChatColor.RED + "/progression");

            if(player.hasPermission(Permissions.CORE_ADMIN))
                player.sendMessage(ChatColor.RED + "/progression bypass <player>");
        } else {
            sender.sendMessage("/progression bypass <player>");
        }

        return false;
    }

}
