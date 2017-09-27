package gg.revival.factions.core.servermode.commands;

import gg.revival.factions.core.servermode.ServerMode;
import gg.revival.factions.core.servermode.ServerState;
import gg.revival.factions.core.tools.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ServerModeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!command.getName().equalsIgnoreCase("servermode")) return false;

        if(sender instanceof Player) {
            Player player = (Player)sender;

            if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return false;
            }
        }

        if(args.length != 1) {
            sender.sendMessage(ChatColor.YELLOW + "Current Server Mode" + ChatColor.WHITE + ": " + ServerMode.getCurrentState().toString());
            return false;
        }

        if(args[0].equalsIgnoreCase("sotw")) {
            ServerMode.updateServerState(ServerState.SOTW);
            return false;
        }

        if(args[0].equalsIgnoreCase("normal")) {
            ServerMode.updateServerState(ServerState.NORMAL);
            return false;
        }

        if(args[0].equalsIgnoreCase("eotwopen")) {
            ServerMode.updateServerState(ServerState.EOTW_OPEN);
            return false;
        }

        if(args[0].equalsIgnoreCase("eotwclosed")) {
            ServerMode.updateServerState(ServerState.EOTW_CLOSED);
            return false;
        }

        sender.sendMessage(ChatColor.RED + "Valid server states" + ChatColor.WHITE + ": " + "sotw, normal, eotwopen, eotwclosed");

        return false;
    }

}
