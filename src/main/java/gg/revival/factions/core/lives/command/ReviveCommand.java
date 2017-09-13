package gg.revival.factions.core.lives.command;

import gg.revival.factions.core.deathbans.Deathbans;
import gg.revival.factions.core.lives.Lives;
import gg.revival.factions.core.tools.Logger;
import gg.revival.factions.core.tools.OfflinePlayerLookup;
import gg.revival.factions.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReviveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!command.getName().equalsIgnoreCase("revive")) return false;

        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                sender.sendMessage("This command can not be ran by console");
                return false;
            }

            Player revivedPlayer = (Player)sender;

            Deathbans.getActiveDeathban(revivedPlayer.getUniqueId(), death -> {
                if(death == null) {
                    revivedPlayer.sendMessage(ChatColor.GREEN + "You are not deathbanned");
                    return;
                }

                Lives.getLives(revivedPlayer.getUniqueId(), lives -> {
                    if(!revivedPlayer.hasPermission(Permissions.CORE_ADMIN) && lives <= 0) {
                        revivedPlayer.sendMessage(ChatColor.RED + "You do not have enough lives");
                        return;
                    }

                    if(!revivedPlayer.hasPermission(Permissions.CORE_ADMIN)) {
                        Lives.setLives(revivedPlayer.getUniqueId(), lives - 1, (uuid, newLives) -> {
                            revivedPlayer.sendMessage(ChatColor.GREEN + "You now have " + newLives + " lives");
                        });
                    }

                    revivedPlayer.sendMessage(ChatColor.GREEN + "You have been revived");
                    death.setExpiresTime(System.currentTimeMillis());
                    Deathbans.saveDeathban(death);
                });
            });

            return false;
        }

        if(args.length == 1) {
            String namedPlayer = args[0];

            OfflinePlayerLookup.getOfflinePlayerByName(namedPlayer, (uuid, username) -> {
                if(uuid == null || username == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found");
                    return;
                }

                Deathbans.getActiveDeathban(uuid, death -> {
                    if(death == null) {
                        sender.sendMessage(ChatColor.GREEN + "This player is not deathbanned");
                        return;
                    }

                    if(sender instanceof Player) {
                        Player revivingPlayer = (Player)sender;

                        if(!revivingPlayer.hasPermission(Permissions.CORE_ADMIN)) {
                            Lives.getLives(revivingPlayer.getUniqueId(), lives -> {
                                if(lives <= 0) {
                                    revivingPlayer.sendMessage(ChatColor.RED + "You do not have enough lives");
                                    return;
                                }

                                Lives.setLives(revivingPlayer.getUniqueId(), lives - 1, (revivingUUID, newLives) ->
                                        revivingPlayer.sendMessage(ChatColor.GREEN + "You now have " + newLives + " lives"));
                            });
                        }

                        if(Bukkit.getPlayer(uuid) != null)
                            Bukkit.getPlayer(uuid).sendMessage(ChatColor.GREEN + "You have been revived");

                        revivingPlayer.sendMessage(ChatColor.GREEN + username + " has been revived");

                        death.setExpiresTime(System.currentTimeMillis());
                        Deathbans.saveDeathban(death);

                        Logger.log(username + " has been revived");
                    }

                    else {
                        if(Bukkit.getPlayer(uuid) != null)
                            Bukkit.getPlayer(uuid).sendMessage(ChatColor.GREEN + "You have been revived");

                        death.setExpiresTime(System.currentTimeMillis());
                        Deathbans.saveDeathban(death);

                        Logger.log(username + " has been revived");
                    }
                });
            });
        }

        return false;
    }

}
