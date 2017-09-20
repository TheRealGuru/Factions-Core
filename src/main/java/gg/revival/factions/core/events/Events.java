package gg.revival.factions.core.events;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.events.command.EventsCommand;
import gg.revival.factions.core.events.engine.EventManager;
import gg.revival.factions.core.events.listener.DTCEventListener;
import gg.revival.factions.core.events.listener.EventBuilderListener;
import gg.revival.factions.core.events.listener.EventsGUIListener;
import gg.revival.factions.core.events.task.DTCTask;
import gg.revival.factions.core.events.task.EventScheduler;
import gg.revival.factions.core.events.task.GUIUpdaterTask;
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
        Bukkit.getPluginManager().registerEvents(new EventsGUIListener(), FC.getFactionsCore());
        Bukkit.getPluginManager().registerEvents(new DTCEventListener(), FC.getFactionsCore());
    }

    private static void loadCommands() {
        FC.getFactionsCore().getCommand("events").setExecutor(new EventsCommand());
    }

    @SuppressWarnings("deprecation")
    private static void loadThreads() {
        Bukkit.getScheduler().runTaskTimer(FC.getFactionsCore(), new KOTHTask(), 0L, 5L);
        Bukkit.getScheduler().runTaskTimer(FC.getFactionsCore(), new DTCTask(), 0L, 5L);
        Bukkit.getScheduler().runTaskTimer(FC.getFactionsCore(), new GUIUpdaterTask(), 0L, 20L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(FC.getFactionsCore(), new EventScheduler(), 0L, 20L);
    }

}
