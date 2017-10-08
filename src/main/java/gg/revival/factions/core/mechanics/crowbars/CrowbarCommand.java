package gg.revival.factions.core.mechanics.crowbars;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.tools.Permissions;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrowbarCommand implements CommandExecutor {

    @Getter private FC core;

    public CrowbarCommand(FC core) {
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[]) {
        if(!command.getName().equalsIgnoreCase("crowbar")) return false;

        if(!(sender instanceof Player)) return false;

        Player player = (Player)sender;

        if(!player.hasPermission(Permissions.CORE_ADMIN)) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
            return false;
        }

        if(!core.getConfiguration().crowbarsEnabled) {
            player.sendMessage(ChatColor.RED + "Crowbars are disabled");
            return false;
        }

        player.getInventory().addItem(core.getMechanics().getCrowbars().getCrowbar());
        player.sendMessage(ChatColor.YELLOW + "Given Crowbar (1)");

        return false;
    }

}
