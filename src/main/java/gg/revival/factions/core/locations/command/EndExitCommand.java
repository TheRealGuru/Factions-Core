package gg.revival.factions.core.locations.command;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.tools.Permissions;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EndExitCommand implements CommandExecutor {

    @Getter private FC core;

    public EndExitCommand(FC core) {
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!command.getName().equalsIgnoreCase("endexit")) return false;

        if(!(sender instanceof Player)) {
            sender.sendMessage("This command can not be ran by console");
            return false;
        }

        Player player = (Player)sender;

        if(args.length == 0) {
            if(!player.hasPermission(Permissions.CORE_MOD) && !player.hasPermission(Permissions.CORE_ADMIN)) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return false;
            }

            player.teleport(core.getLocations().getEndExitLocation());
            player.sendMessage(ChatColor.GREEN + "Teleported to End Exit");
            return false;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("set")) {
                if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    return false;
                }

                core.getLocations().saveEndExitLocation(player.getLocation());
                player.sendMessage(ChatColor.GREEN + "End Exit location has been updated");
                return false;
            }
        }

        return false;
    }

}
