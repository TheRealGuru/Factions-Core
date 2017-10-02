package gg.revival.factions.core.events;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.events.command.EventsCommand;
import gg.revival.factions.core.events.command.PalaceCommand;
import gg.revival.factions.core.events.engine.EventManager;
import gg.revival.factions.core.events.engine.PalaceManager;
import gg.revival.factions.core.events.listener.*;
import gg.revival.factions.core.events.task.*;
import org.bukkit.Bukkit;

public class Events {

    public static void onEnable() {
        loadListeners();
        loadCommands();
        loadThreads();

        EventManager.loadEvents();
        PalaceManager.loadPalace();
    }

    public static void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new EventBuilderListener(), FC.getFactionsCore());
        Bukkit.getPluginManager().registerEvents(new EventsGUIListener(), FC.getFactionsCore());
        Bukkit.getPluginManager().registerEvents(new DTCEventListener(), FC.getFactionsCore());
        Bukkit.getPluginManager().registerEvents(new EventChestListener(), FC.getFactionsCore());
        Bukkit.getPluginManager().registerEvents(new LootTablesListener(), FC.getFactionsCore());
        Bukkit.getPluginManager().registerEvents(new EventsListener(), FC.getFactionsCore());
    }

    private static void loadCommands() {
        FC.getFactionsCore().getCommand("events").setExecutor(new EventsCommand());
        FC.getFactionsCore().getCommand("palace").setExecutor(new PalaceCommand());
    }

    @SuppressWarnings("deprecation")
    private static void loadThreads() {
        Bukkit.getScheduler().runTaskTimer(FC.getFactionsCore(), new KOTHTask(), 0L, 5L);
        Bukkit.getScheduler().runTaskTimer(FC.getFactionsCore(), new DTCTask(), 0L, 5L);
        Bukkit.getScheduler().runTaskTimer(FC.getFactionsCore(), new GUIUpdaterTask(), 0L, 20L);
        Bukkit.getScheduler().runTaskTimer(FC.getFactionsCore(), new ChestTask(), 0L, 10L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(FC.getFactionsCore(), new EventScheduler(), 0L, 20L);
    }

}
