package gg.revival.factions.core.progression;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.progression.command.ProgressionCommand;
import gg.revival.factions.core.progression.listener.ProgressionListener;
import gg.revival.factions.core.tools.Configuration;
import org.bukkit.Bukkit;

public class Progression {

    public static void onEnable() {
        loadCommands();
        loadListeners();
    }

    private static void loadCommands() {
        FC.getFactionsCore().getCommand("progression").setExecutor(new ProgressionCommand());
    }

    private static void loadListeners() {
        if(Configuration.progressEnabled)
            Bukkit.getPluginManager().registerEvents(new ProgressionListener(), FC.getFactionsCore());
    }

}
