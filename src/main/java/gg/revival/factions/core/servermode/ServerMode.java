package gg.revival.factions.core.servermode;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.servermode.commands.ServerModeCommand;
import gg.revival.factions.core.servermode.listeners.ServerModeEventsListener;
import gg.revival.factions.core.tools.Permissions;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ServerMode {

    @Getter private FC core;
    @Getter @Setter ServerState currentState;

    public ServerMode(FC core) {
        this.core = core;

        onEnable();
    }

    public void updateServerState(ServerState newState) {
        setCurrentState(newState);
        core.getFileManager().getConfig().set("servermode", newState.toString());
        core.getFileManager().saveConfig();

        core.getPlayerTools().sendPermissionMessage(Permissions.CORE_ADMIN,
                ChatColor.GREEN + "Server state has been updated to" + ChatColor.WHITE + ": " + newState.toString().replace("_", " "));
    }

    public void onEnable() {
        loadCommands();
        loadListeners();
    }

    private void loadCommands() {
        core.getCommand("servermode").setExecutor(new ServerModeCommand(core));
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new ServerModeEventsListener(core), core);
    }

}
