package gg.revival.factions.core.mechanics.crowbars;

import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.core.tools.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrowbarCommand implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[])
    {
        if(!command.getName().equalsIgnoreCase("crowbar")) return false;

        if(!(sender instanceof Player)) return false;

        Player player = (Player)sender;

        if(!player.hasPermission(Permissions.CORE_ADMIN))
        {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
            return false;
        }

        if(!Configuration.crowbarsEnabled)
        {
            player.sendMessage(ChatColor.RED + "Crowbars are disabled");
            return false;
        }

        player.getInventory().addItem(Crowbar.getCrowbar());
        player.sendMessage(ChatColor.YELLOW + "Given Crowbar (1)");

        return false;
    }

}
