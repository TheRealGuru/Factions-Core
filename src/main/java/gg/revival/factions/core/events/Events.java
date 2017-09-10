package gg.revival.factions.core.events;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.events.command.EventsCommand;
import gg.revival.factions.core.events.engine.EventManager;
import gg.revival.factions.core.events.listener.EventBuilderListener;
import gg.revival.factions.core.events.task.DTCTask;
import gg.revival.factions.core.events.task.KOTHTask;
import org.bukkit.Bukkit;

public class Events {

    public static void onEnable() {
        loadListeners();
        loadCommands();
        loadThreads();

        EventManager.loadEvents();
    }

    public static void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new EventBuilderListener(), FC.getFactionsCore());
    }

    public static void loadCommands() {
        FC.getFactionsCore().getCommand("events").setExecutor(new EventsCommand());
    }

    public static void loadThreads() {
        Bukkit.getScheduler().runTaskTimer(FC.getFactionsCore(), new KOTHTask(), 0L, 5L);
        Bukkit.getScheduler().runTaskTimer(FC.getFactionsCore(), new DTCTask(), 0L, 5L);
    }

}
