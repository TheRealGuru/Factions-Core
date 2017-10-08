package gg.revival.factions.core.bastion.logout.commands;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.timers.TimerManager;
import gg.revival.factions.timers.TimerType;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LogoutCommand implements CommandExecutor {

    @Getter private FC core;

    public LogoutCommand(FC core) {
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[]) {
        if(!command.getName().equalsIgnoreCase("logout")) return false;

        if(!(sender instanceof Player)) {
            sender.sendMessage("This command can not be used by console");
            return false;
        }

        Player player = (Player)sender;
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer.isBeingTimed(TimerType.LOGOUT) || core.getBastion().getLogoutTask().getSafeloggers().contains(player.getUniqueId())) {
            int dur = (int)((facPlayer.getTimer(TimerType.LOGOUT).getExpire() - System.currentTimeMillis()) / 1000L);
            player.sendMessage(ChatColor.YELLOW + "You will be safely logged out from the server in " + ChatColor.GOLD + dur + ChatColor.YELLOW + " seconds");
            return false;
        }

        facPlayer.addTimer(TimerManager.createTimer(TimerType.LOGOUT, core.getConfiguration().logoutTimer));
        core.getBastion().getLogoutTask().getStartingLocations().put(player.getUniqueId(), player.getLocation());
        core.getBastion().getLogoutTask().getSafeloggers().add(player.getUniqueId());
        player.sendMessage(ChatColor.YELLOW + "You will be safely logged out from the server in " + ChatColor.GOLD + core.getConfiguration().logoutTimer + ChatColor.YELLOW + " seconds");

        return false;
    }

}
