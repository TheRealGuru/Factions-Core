package gg.revival.factions.core.events;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.events.builder.EventBuilder;
import gg.revival.factions.core.events.chests.ChestManager;
import gg.revival.factions.core.events.chests.EventKey;
import gg.revival.factions.core.events.chests.LootTables;
import gg.revival.factions.core.events.command.EventsCommand;
import gg.revival.factions.core.events.command.PalaceCommand;
import gg.revival.factions.core.events.engine.DTCManager;
import gg.revival.factions.core.events.engine.EventManager;
import gg.revival.factions.core.events.engine.KOTHManager;
import gg.revival.factions.core.events.engine.PalaceManager;
import gg.revival.factions.core.events.gui.EventsGUI;
import gg.revival.factions.core.events.listener.*;
import gg.revival.factions.core.events.messages.EventsMessages;
import gg.revival.factions.core.events.task.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class Events {

    @Getter private FC core;
    @Getter private EventManager eventManager;
    @Getter private PalaceManager palaceManager;
    @Getter private KOTHManager kothManager;
    @Getter private DTCManager dtcManager;
    @Getter private ChestManager chestManager;
    @Getter private EventBuilder eventBuilder;
    @Getter private EventsMessages eventMessages;
    @Getter private EventsGUI eventsGUI;
    @Getter private LootTables lootTables;
    @Getter public EventKey eventKeys;

    public Events(FC core) {
        this.core = core;
        this.eventManager = new EventManager(core);
        this.palaceManager = new PalaceManager(core);
        this.kothManager = new KOTHManager(core);
        this.dtcManager = new DTCManager(core);
        this.chestManager = new ChestManager(core);
        this.eventBuilder = new EventBuilder(core);
        this.eventMessages = new EventsMessages();
        this.eventsGUI = new EventsGUI(core);
        this.lootTables = new LootTables(core);
        this.eventKeys = new EventKey();

        onEnable();
    }

    public void onEnable() {
        new BukkitRunnable() {
            public void run() {
                loadListeners();
                loadCommands();
                loadThreads();

                eventManager.loadEvents();
                palaceManager.loadPalace();
            }
        }.runTask(core);
    }

    public void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new EventBuilderListener(core), core);
        Bukkit.getPluginManager().registerEvents(new EventsGUIListener(core), core);
        Bukkit.getPluginManager().registerEvents(new DTCEventListener(core), core);
        Bukkit.getPluginManager().registerEvents(new EventChestListener(core), core);
        Bukkit.getPluginManager().registerEvents(new LootTablesListener(core), core);
        Bukkit.getPluginManager().registerEvents(new EventsListener(core), core);
    }

    private void loadCommands() {
        core.getCommand("events").setExecutor(new EventsCommand(core));
        core.getCommand("palace").setExecutor(new PalaceCommand(core));
    }

    @SuppressWarnings("deprecation")
    private void loadThreads() {
        Bukkit.getScheduler().runTaskTimer(core, new KOTHTask(core), 0L, 5L);
        Bukkit.getScheduler().runTaskTimer(core, new DTCTask(core), 0L, 5L);
        Bukkit.getScheduler().runTaskTimer(core, new GUIUpdaterTask(core), 0L, 20L);
        Bukkit.getScheduler().runTaskTimer(core, new ChestTask(core), 0L, 10L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(core, new EventScheduler(core), 0L, 20L);
    }

}
