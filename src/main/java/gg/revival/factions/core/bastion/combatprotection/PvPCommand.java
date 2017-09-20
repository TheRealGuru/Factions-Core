package gg.revival.factions.core.bastion.combatprotection;

import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.core.tools.Permissions;
import gg.revival.factions.core.tools.TimeTools;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.timers.TimerManager;
import gg.revival.factions.timers.TimerType;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PvPCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[]) {
        if(!command.getName().equalsIgnoreCase("pvp")) return false;

        if(!(sender instanceof Player)) {
            sender.sendMessage("This command can not be used by console");
            return false;
        }

        Player player = (Player)sender;

        if(!Configuration.pvpProtEnabled) {
            player.sendMessage(ChatColor.RED + "PvP protection is disabled on this server");
            return false;
        }

        if(args.length == 0) {
            player.sendMessage(ChatColor.RED + "/pvp enable");

            if(player.hasPermission(Permissions.CORE_ADMIN)) {
                player.sendMessage(ChatColor.RED + "/pvp give <player> <seconds>");
                player.sendMessage(ChatColor.RED + "/pvp remove <player>");
            }
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("on")) {
                FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

                if(!facPlayer.isBeingTimed(TimerType.PVPPROT)) {
                    player.sendMessage(ChatColor.RED + "You do not have PvP protection");
                    return false;
                }

                CombatProtection.takeProtection(player);

                return false;
            }

            player.sendMessage(ChatColor.RED + "/pvp enable");

            return false;
        }

        if(args.length == 2) {
            if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                player.sendMessage(ChatColor.RED + "/pvp enable");
                return false;
            }

            if(args[0].equalsIgnoreCase("remove")) {
                String namedPlayer = args[1];

                if(Bukkit.getPlayer(namedPlayer) == null) {
                    player.sendMessage(ChatColor.RED + "Player not found");
                    return false;
                }

                Player pvpPlayer = Bukkit.getPlayer(namedPlayer);
                FPlayer facPlayer = PlayerManager.getPlayer(pvpPlayer.getUniqueId());

                if(!facPlayer.isBeingTimed(TimerType.PVPPROT)) {
                    player.sendMessage(ChatColor.RED + "This player does not have PvP protection");
                    return false;
                }

                CombatProtection.takeProtection(pvpPlayer);

                return false;
            }
        }

        if(args.length == 3) {
            if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                player.sendMessage(ChatColor.RED + "/pvp enable");
                return false;
            }

            if(args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("give")) {
                String namedPlayer = args[1];
                String namedTime = args[2];

                if(Bukkit.getPlayer(namedPlayer) == null) {
                    player.sendMessage(ChatColor.RED + "Player not found");
                    return false;
                }

                if(!NumberUtils.isNumber(namedTime)) {
                    player.sendMessage(ChatColor.RED + "/pvp set <player> <time>");
                    return false;
                }

                Player pvpPlayer = Bukkit.getPlayer(namedPlayer);
                FPlayer facPlayer = PlayerManager.getPlayer(pvpPlayer.getUniqueId());
                int duration = NumberUtils.toInt(namedTime);

                if(facPlayer.isBeingTimed(TimerType.PVPPROT)) {
                    facPlayer.getTimer(TimerType.PVPPROT).setExpire(duration * 1000L);
                    pvpPlayer.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.YELLOW + " updated your PvP protection time to " + ChatColor.GOLD + TimeTools.formatIntoHHMMSS(duration));
                }

                else {
                    facPlayer.addTimer(TimerManager.createTimer(TimerType.PVPPROT, duration));
                    pvpPlayer.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.YELLOW + " gave you PvP protection for " + ChatColor.GOLD + TimeTools.formatIntoHHMMSS(duration));
                }

                player.sendMessage(ChatColor.GREEN + "You have updated this players PvP protection");

                return false;
            }
        }

        player.sendMessage(ChatColor.RED + "/pvp enable");

        if(player.hasPermission(Permissions.CORE_ADMIN)) {
            player.sendMessage(ChatColor.RED + "/pvp give <player> <seconds>");
            player.sendMessage(ChatColor.RED + "/pvp remove <player>");
        }

        return false;
    }

}
