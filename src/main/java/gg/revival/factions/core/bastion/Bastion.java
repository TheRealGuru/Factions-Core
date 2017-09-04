package gg.revival.factions.core.bastion;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.bastion.commands.LogoutCommand;
import gg.revival.factions.core.bastion.listeners.LogoutListener;
import org.bukkit.Bukkit;

public class Bastion
{

    public static void onEnable()
    {
        loadCommands();
        loadListeners();
    }

    public static void onDisable()
    {

    }

    public static void loadListeners()
    {
        Bukkit.getPluginManager().registerEvents(new LogoutListener(), FC.getFactionsCore());
    }

    public static void loadCommands()
    {
        FC.getFactionsCore().getCommand("logout").setExecutor(new LogoutCommand());
    }

}
