package gg.revival.factions.core.progression.command;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.core.tools.Permissions;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.timers.TimerType;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProgressionCommand implements CommandExecutor {

    @Getter private FC core;

    public ProgressionCommand(FC core) {
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!command.getName().equalsIgnoreCase("progression")) return false;

        if(!core.getConfiguration().progressEnabled) {
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
                player.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "-----------------------------------");
                player.sendMessage(ChatColor.YELLOW + "Your progression has not been completed yet. Check your progress above your hotbar.");
                player.sendMessage("     ");
                player.sendMessage(ChatColor.RED + "You will not be able to perform the following until your progression has been completed" + ChatColor.WHITE + ": ");
                player.sendMessage("     ");
                player.sendMessage(ChatColor.GOLD + " - " + ChatColor.YELLOW + "Enter enemy faction claims");
                player.sendMessage(ChatColor.GOLD + " - " + ChatColor.YELLOW + "Enter event claims");
                player.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "-----------------------------------");

                return false;
            }

            player.sendMessage(ChatColor.GREEN + "Your progression has been completed");

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
