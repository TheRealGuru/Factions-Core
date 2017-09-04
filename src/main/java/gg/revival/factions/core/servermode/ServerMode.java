package gg.revival.factions.core.servermode;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.servermode.listeners.ServerModeEventsListener;
import org.bukkit.Bukkit;

public class ServerMode
{

    public static void onEnable()
    {
        loadCommands();
        loadListeners();
    }

    public static void onDisable()
    {

    }

    public static void loadCommands()
    {

    }

    public static void loadListeners()
    {
        Bukkit.getPluginManager().registerEvents(new ServerModeEventsListener(), FC.getFactionsCore());
    }

}
