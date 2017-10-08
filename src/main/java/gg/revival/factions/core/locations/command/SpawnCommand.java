package gg.revival.factions.core.locations.command;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.tools.Permissions;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    @Getter private FC core;

    public SpawnCommand(FC core) {
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!command.getName().equalsIgnoreCase("spawn")) return false;

        if(!(sender instanceof Player)) {
            sender.sendMessage("This command can not be ran by console");
            return false;
        }

        Player player = (Player)sender;

        if(args.length == 0) {
            if(!player.hasPermission(Permissions.CORE_MOD) && !player.hasPermission(Permissions.CORE_ADMIN)) {
                player.sendMessage(ChatColor.RED + "The /spawn command is disabled on this server. You must run to the center of the map (0, 0) to get to spawn");
                return false;
            }

            player.teleport(core.getLocations().getSpawnLocation());
            player.sendMessage(ChatColor.GREEN + "Returned to spawn");
            return false;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("set")) {
                if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                    return false;
                }

                core.getLocations().saveSpawnLocation(player.getLocation());
                player.sendMessage(ChatColor.GREEN + "Spawn location has been updated");
                return false;
            }
        }

        return false;
    }

}
