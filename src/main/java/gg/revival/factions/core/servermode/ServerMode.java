package gg.revival.factions.core.servermode;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.servermode.listeners.ServerModeEventsListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

public class ServerMode {

    @Getter @Setter static ServerState currentState;

    public static void onEnable() {
        loadCommands();
        loadListeners();
    }

    public static void loadCommands() {
        // TODO: Add /servermode commands
    }

    public static void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new ServerModeEventsListener(), FC.getFactionsCore());
    }

}
