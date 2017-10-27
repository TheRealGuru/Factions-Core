package gg.revival.factions.core.kits.command;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.kits.FKit;
import gg.revival.factions.core.tools.Permissions;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class KitCommandExecutor implements CommandExecutor {

    @Getter private FC core;

    public KitCommandExecutor(FC core) {
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!command.getName().equalsIgnoreCase("kit")) return false;

        if(!(sender instanceof Player)) {
            sender.sendMessage("This command can not be performed through console");
            return false;
        }

        Player player = (Player)sender;

        if(!player.hasPermission(Permissions.CORE_ADMIN)) {
            player.sendMessage(ChatColor.RED + "You do not have permission to perform this command");
            return false;
        }

        if(args.length == 0) {
            if(!core.getConfiguration().kitsEnabled) {
                player.sendMessage(ChatColor.RED + "Kits are disabled on this server");
                return false;
            }

            List<String> kitNames = Lists.newArrayList();

            for(FKit kits : core.getKits().getLoadedKits())
                kitNames.add(kits.getName());

            player.sendMessage(ChatColor.GOLD + "Loaded Kits: " + ChatColor.BLUE + Joiner.on(ChatColor.YELLOW + ", " + ChatColor.BLUE).join(kitNames));

            return false;
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("load")) {
                String namedKit = args[1];
                FKit kit = core.getKits().getKitByName(namedKit);

                if(kit == null) {
                    player.sendMessage(ChatColor.RED + "Kit not found");
                    return false;
                }

                core.getKits().giveKit(player, kit, true);

                return false;
            }

            if(args[0].equalsIgnoreCase("save")) {
                String namedKit = args[1];

                if(core.getKits().getKitByName(namedKit) != null) {
                    player.sendMessage(ChatColor.RED + "Kit with the name " + ChatColor.BLUE + namedKit + ChatColor.RED + " already exists");
                    return false;
                }

                core.getKits().saveKit(player, namedKit);

                return false;
            }
        }

        player.sendMessage(ChatColor.RED + "/kit" + "\n" + ChatColor.RED + "/kit load <name>" + "\n" + ChatColor.RED + "/kit save <name>");

        return false;
    }

}
