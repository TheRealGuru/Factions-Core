package gg.revival.factions.core.deathbans.command;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.tools.Permissions;
import lombok.Getter;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeathbanCommand implements CommandExecutor {

    @Getter private FC core;

    public DeathbanCommand(FC core) {
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[]) {
        if(!command.getName().equalsIgnoreCase("deathban")) return false;

        if(sender instanceof Player) {
            Player player = (Player)sender;

            if(!player.hasPermission(Permissions.CORE_ADMIN)) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return false;
            }
        }

        if(args.length != 2) {
            sender.sendMessage(ChatColor.RED + "/deathban <player> <seconds>");
            return false;
        }

        String namedPlayer = args[0];
        String namedDuration = args[1];

        int duration = NumberUtils.toInt(namedDuration);

        if(!NumberUtils.isNumber(namedDuration)) {
            sender.sendMessage(ChatColor.RED + "/deathban <player> <seconds>");
            return false;
        }

        core.getOfflinePlayerLookup().getOfflinePlayerByName(namedPlayer, (uuid, username) -> {
            if(uuid == null || username == null) {
                sender.sendMessage(ChatColor.RED + "Player not found");
                return;
            }

            String creator = sender.getName();

            core.getDeathbans().getActiveDeathban(uuid, death -> {
                if(death != null) {
                    death.setExpiresTime(System.currentTimeMillis());
                    core.getDeathbans().saveDeathban(death);
                }
            });

            core.getDeathbans().deathbanPlayer(uuid, "Applied by Admin", duration);

            sender.sendMessage(ChatColor.GREEN + "You have deathbanned " + username + " for " + duration + " seconds");

            core.getLog().log(creator + " has applied a manual deathban to " + username + " for " + duration + " seconds");
        });

        return false;
    }

}
