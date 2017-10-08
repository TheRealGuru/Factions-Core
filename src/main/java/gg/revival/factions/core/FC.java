package gg.revival.factions.core;

import gg.revival.factions.core.bastion.Bastion;
import gg.revival.factions.core.classes.Classes;
import gg.revival.factions.core.command.FactionsCoreCommand;
import gg.revival.factions.core.db.DBManager;
import gg.revival.factions.core.deathbans.Deathbans;
import gg.revival.factions.core.events.Events;
import gg.revival.factions.core.limits.Limiter;
import gg.revival.factions.core.lives.Lives;
import gg.revival.factions.core.locations.Locations;
import gg.revival.factions.core.mechanics.Mechanics;
import gg.revival.factions.core.mining.Mining;
import gg.revival.factions.core.progression.Progression;
import gg.revival.factions.core.servermode.ServerMode;
import gg.revival.factions.core.signs.Signs;
import gg.revival.factions.core.stats.Stats;
import gg.revival.factions.core.tools.*;
import gg.revival.factions.core.ui.UI;
import gg.revival.factions.core.ui.UIManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class FC extends JavaPlugin {

    @Getter public static FC factionsCore;
    @Getter public Logger log;
    @Getter public ItemTools itemTools;
    @Getter public PlayerTools playerTools;
    @Getter public Configuration configuration;
    @Getter public FileManager fileManager;
    @Getter public DBManager databaseManager;
    @Getter public Bastion bastion;
    @Getter public Classes classes;
    @Getter public Lives lives;
    @Getter public Limiter limiter;
    @Getter public Mechanics mechanics;
    @Getter public Mining mining;
    @Getter public Progression progression;
    @Getter public ServerMode serverMode;
    @Getter public Signs signs;
    @Getter public Events events;
    @Getter public Deathbans deathbans;
    @Getter public Stats stats;
    @Getter public Locations locations;
    @Getter public UI ui;
    @Getter public UIManager uiManager;

    @Getter public OfflinePlayerLookup offlinePlayerLookup;

    @Override
    public void onEnable() {
        factionsCore = this;

        this.log = new Logger();
        this.itemTools = new ItemTools(this);
        this.playerTools = new PlayerTools(this);
        this.fileManager = new FileManager(this);
        this.serverMode = new ServerMode(this);
        this.classes = new Classes(this);
        this.events = new Events(this);
        this.bastion = new Bastion(this);
        this.lives = new Lives(this);
        this.limiter = new Limiter(this);
        this.mining = new Mining(this);
        this.progression = new Progression(this);
        this.signs = new Signs(this);
        this.deathbans = new Deathbans(this);
        this.locations = new Locations(this);
        this.ui = new UI(this);
        this.uiManager = new UIManager(this);
        this.offlinePlayerLookup = new OfflinePlayerLookup(this);
        this.configuration = new Configuration(this);
        this.mechanics = new Mechanics(this);
        this.stats = new Stats(this);
        this.databaseManager = new DBManager(this);

        getCommand("factionscore").setExecutor(new FactionsCoreCommand(this));
    }

    @Override
    public void onDisable() {
        bastion.onDisable();
        stats.onDisable();

        factionsCore = null;

        this.fileManager = null;
        this.configuration = null;
        this.databaseManager = null;
        this.bastion = null;
        this.classes = null;
        this.lives = null;
        this.limiter = null;
        this.mechanics = null;
        this.mining = null;
        this.progression = null;
        this.serverMode = null;
        this.signs = null;
        this.events = null;
        this.deathbans = null;
        this.stats = null;
        this.locations = null;
        this.ui = null;
        this.uiManager = null;
        this.log = null;
        this.playerTools = null;
        this.itemTools = null;
        this.offlinePlayerLookup = null;
    }
}
