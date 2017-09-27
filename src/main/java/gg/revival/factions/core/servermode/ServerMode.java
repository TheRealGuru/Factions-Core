package gg.revival.factions.core.servermode;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.servermode.commands.ServerModeCommand;
import gg.revival.factions.core.servermode.listeners.ServerModeEventsListener;
import gg.revival.factions.core.tools.FileManager;
import gg.revival.factions.core.tools.Permissions;
import gg.revival.factions.core.tools.PlayerTools;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ServerMode {

    @Getter @Setter static ServerState currentState;

    public static void updateServerState(ServerState newState) {
        setCurrentState(newState);
        FileManager.getConfig().set("servermode", newState.toString());
        FileManager.saveConfig();

        PlayerTools.sendPermissionMessage(Permissions.CORE_ADMIN,
                ChatColor.GREEN + "Server state has been updated to" + ChatColor.WHITE + ": " + newState.toString().replace("_", " "));
    }

    public static void onEnable() {
        loadCommands();
        loadListeners();
    }

    public static void loadCommands() {
        FC.getFactionsCore().getCommand("servermode").setExecutor(new ServerModeCommand());
    }

    public static void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new ServerModeEventsListener(), FC.getFactionsCore());
    }

}
