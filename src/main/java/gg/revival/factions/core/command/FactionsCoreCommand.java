package gg.revival.factions.core.command;

import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.core.tools.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionsCoreCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!command.getName().equalsIgnoreCase("factionscore")) return false;

        if(sender instanceof Player) {
            Player player = (Player)sender;

            if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return false;
            }
        }

        if(args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/factionscore reload");
            return false;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("reload")) {
                sender.sendMessage(ChatColor.GREEN + "Reload request has been submitted for the plug-in 'FactionsCore'");
                Configuration.reload();
                return false;
            }

            sender.sendMessage(ChatColor.RED + "/factionscore reload");

            return false;
        }

        sender.sendMessage(ChatColor.RED + "/factionscore reload");

        return false;
    }

}
