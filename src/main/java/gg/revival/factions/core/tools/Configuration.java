package gg.revival.factions.core.tools;

import gg.revival.factions.core.classes.Classes;
import gg.revival.factions.core.classes.cont.Archer;
import gg.revival.factions.core.classes.cont.Bard;
import gg.revival.factions.core.classes.cont.Miner;
import gg.revival.factions.core.classes.cont.Scout;
import gg.revival.factions.core.events.chests.*;
import gg.revival.factions.core.events.engine.EventManager;
import gg.revival.factions.core.locations.Locations;
import gg.revival.factions.core.servermode.ServerMode;
import gg.revival.factions.core.servermode.ServerState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class Configuration {

    public static String databaseName = "factions";

    public static boolean automateEvents = true;
    public static boolean playChestEffects = true;
    public static int pullsPerKey = 3;
    public static int defaultKothDuration = 60;
    public static int defaultKothWinCondition = 15;
    public static int defaultKothKeys = 3;
    public static int defaultDtcRegen = 15;
    public static int defaultDtcWincond = 500;
    public static int defaultDtcKeys = 3;
    public static int defaultPalaceKeys = 10;

    public static int logoutTimer = 30;
    public static int tagAttacker = 60;
    public static int tagAttacked = 30;
    public static boolean pvpProtEnabled = true;
    public static int pvpProtDuration = 1800;
    public static boolean pvpSafetyEnabled = true;
    public static int pvpSafetyDuration = 5;
    public static int loggerDuration = 20;
    public static int loggerEnemyDistance = 30;

    public static int classWarmupDelay = 15;
    public static int activeSpeedCooldown = 60;
    public static int activeJumpCooldown = 60;
    public static int activeRegenCooldown = 90;
    public static int activeStrengthCooldown = 120;
    public static boolean archerEnabled = true;
    public static double maxArcherDamage = 4.0;
    public static boolean scoutEnabled = true;
    public static boolean bardEnabled = true;
    public static double bardNearbyCheckDistance = 15.0;
    public static boolean minerEnabled = true;

    public static boolean deathbansEnabled = true;
    public static long normalDeathban = 10000L;
    public static long eventDeathban = 10000L;

    public static boolean livesEnabled = true;

    public static boolean emeraldXpEnabled = true;
    public static boolean hardmodeEnabled = true;
    public static boolean enderpearlCooldownsEnabled = true;
    public static int enderpearlCooldownsDuration = 16;
    public static boolean mobstackingEnabled = true;
    public static int mobstackingMaxStack = 30;
    public static int mobstackingInterval = 5;
    public static boolean crowbarsEnabled = true;
    public static int crowbarSpawnerUse = 1;
    public static int crowbarPortalUse = 6;
    public static boolean bookUnenchantingEnabled = true;
    public static boolean invalidPearlBlocksEnabled = true;
    public static boolean highSpawnersDisabled = true;
    public static int highSpawnersHeight = 125;

    public static boolean miningEnabled = true;
    public static float miningGoldChance = 0.006f;
    public static float miningDiamondChance = 0.004f;
    public static float miningEmeraldChance = 0.001f;
    public static float miningGlowstoneChance = 0.002f;
    public static boolean announceFoundGold = false;
    public static boolean announceFoundDiamond = true;
    public static boolean announceFoundEmerald = true;
    public static boolean announceFoundGlowstone = false;

    public static boolean progressEnabled = true;
    public static int progressDuration = 3600;

    public static boolean settingsDisableEndermites = true;
    public static boolean settingsDisableBreakingSpawners = true;
    public static boolean settingsRemoveGApples = true;
    public static boolean protectNetherPortals = true;

    public static boolean limitEnchants = true;
    public static Map<Enchantment, Integer> enchantmentLimits = new HashMap<>();

    public static boolean limitPotions = true;
    public static Map<PotionEffectType, Integer> potionLimits = new HashMap<>();

    public static boolean statsEnabled = true;
    public static boolean trackStats = true;

    public static void reload() {
        FileManager.reloadFiles();

        Classes.getEnabledClasses().clear();
        EventManager.getEvents().clear();
        ChestManager.getLoadedChests().clear();
        enchantmentLimits.clear();
        potionLimits.clear();

        load();

        Logger.log("Reloaded Configuration");
    }

    public static void load() {
        FileManager.createFiles();

        FileConfiguration config = FileManager.getConfig();
        FileConfiguration events = FileManager.getEvents();

        databaseName = config.getString("database.database-name");

        for(ServerState states : ServerState.values()) {
            if(!states.toString().equalsIgnoreCase(config.getString("servermode"))) continue;
            ServerMode.setCurrentState(states);
        }

        if(ServerMode.getCurrentState() == null) {
            ServerMode.setCurrentState(ServerState.NORMAL);
            Logger.log(Level.SEVERE, "Server state not found in config.yml! Defaulting to normal settings...");
        } else {
            Logger.log("Server state has been set to '" + ServerMode.getCurrentState().toString() + "'");
        }

        automateEvents = events.getBoolean("configuration.automated");
        playChestEffects = events.getBoolean("configuration.play-chest-effects");
        pullsPerKey = events.getInt("configuration.pulls-per-key");
        defaultKothDuration = events.getInt("configuration.koth.default-duration");
        defaultKothWinCondition = events.getInt("configuration.koth.default-wincond");
        defaultKothKeys = events.getInt("configuration.koth.default-keys");
        defaultDtcRegen = events.getInt("configuration.dtc.default-regen");
        defaultDtcWincond = events.getInt("configuration.dtc.default-wincond");
        defaultDtcKeys = events.getInt("configuration.dtc.default-keys");
        defaultPalaceKeys = events.getInt("configuration.palace.default-keys");

        logoutTimer = config.getInt("bastion.logout-timer");
        tagAttacker = config.getInt("bastion.combat-tag.attacker-duration");
        tagAttacked = config.getInt("bastion.combat-tag.attacked-duration");
        pvpProtEnabled = config.getBoolean("bastion.pvp-prot.enabled");
        pvpProtDuration = config.getInt("bastion.pvp-prot.duration");
        pvpSafetyEnabled = config.getBoolean("bastion.pvp-safety.enabled");
        pvpSafetyDuration = config.getInt("bastion.pvp-safety.duration");
        loggerDuration = config.getInt("bastion.loggers.duration");
        loggerEnemyDistance = config.getInt("bastion.loggers.enemy-distance");

        classWarmupDelay = config.getInt("classes.warmup");
        activeSpeedCooldown = config.getInt("classes.active-cooldowns.speed");
        activeJumpCooldown = config.getInt("classes.active-cooldowns.jump");
        activeRegenCooldown = config.getInt("classes.active-cooldowns.regen");
        activeStrengthCooldown = config.getInt("classes.active-cooldowns.strength");
        archerEnabled = config.getBoolean("classes.archer.enabled");
        maxArcherDamage = config.getDouble("classes.archer.max-damage");
        scoutEnabled = config.getBoolean("classes.scout.enabled");
        bardEnabled = config.getBoolean("classes.bard.enabled");
        bardNearbyCheckDistance = config.getDouble("classes.bard.nearby-check-distance");
        minerEnabled = config.getBoolean("classes.miner.enabled");

        if(archerEnabled) {
            Archer archer = new Archer();
            Classes.getEnabledClasses().add(archer);
        }

        if(scoutEnabled) {
            Scout scout = new Scout();
            Classes.getEnabledClasses().add(scout);
        }

        if(bardEnabled) {
            Bard bard = new Bard();
            Classes.getEnabledClasses().add(bard);
        }

        if(minerEnabled) {
            Miner miner = new Miner();
            Classes.getEnabledClasses().add(miner);
        }

        deathbansEnabled = config.getBoolean("deathbans.enabled");
        normalDeathban = config.getInt("deathbans.durations.normal") * 1000L;
        eventDeathban = config.getInt("deathbans.durations.event") * 1000L;

        livesEnabled = config.getBoolean("lives.enabled");

        emeraldXpEnabled = config.getBoolean("mechanics.emerald-xp");
        hardmodeEnabled = config.getBoolean("mechanics.hardmode");
        enderpearlCooldownsEnabled = config.getBoolean("mechanics.enderpearl-cooldowns.enabled");
        enderpearlCooldownsDuration = config.getInt("mechanics.enderpearl-cooldowns.duration");
        mobstackingEnabled = config.getBoolean("mechanics.mobstacking.enabled");
        mobstackingMaxStack = config.getInt("mechanics.mobstacking.max-stack");
        mobstackingInterval = config.getInt("mechanics.mobstacking.update-interval");
        crowbarsEnabled = config.getBoolean("mechanics.crowbars.enabled");
        crowbarSpawnerUse = config.getInt("mechanics.crowbars.spawner-uses");
        crowbarPortalUse = config.getInt("mechanics.crowbars.frame-uses");
        bookUnenchantingEnabled = config.getBoolean("mechanics.unenchant-books");
        invalidPearlBlocksEnabled = config.getBoolean("mechanics.invalid-pearling");
        highSpawnersDisabled = config.getBoolean("mechanics.high-spawners-disabled.enabled");
        highSpawnersHeight = config.getInt("mechanics.high-spawners-disabled.height");

        miningEnabled = config.getBoolean("mining.enabled");
        miningGoldChance = (float)config.getDouble("mining.odds.gold");
        miningDiamondChance = (float)config.getDouble("mining.odds.diamond");
        miningEmeraldChance = (float)config.getDouble("mining.odds.emerald");
        miningGlowstoneChance = (float)config.getDouble("mining.odds.glowstone");
        announceFoundGold = config.getBoolean("mining.announce.gold");
        announceFoundDiamond = config.getBoolean("mining.announce.diamond");
        announceFoundEmerald = config.getBoolean("mining.announce.emerald");
        announceFoundGlowstone = config.getBoolean("mining.announce.glowstone");

        progressEnabled = config.getBoolean("progression.enabled");
        progressDuration = config.getInt("progression.duration");

        limitEnchants = config.getBoolean("enchant-limits.enabled");
        limitPotions = config.getBoolean("potion-limits.enabled");

        for(String enchantKeys : config.getConfigurationSection("enchant-limits.enchants").getKeys(false)) {
            Enchantment enchantment = Enchantment.getByName(enchantKeys);
            int lvl = config.getInt("enchant-limits.enchants." + enchantKeys);

            enchantmentLimits.put(enchantment, lvl);
        }

        for(String potionKeys : config.getConfigurationSection("potion-limits.potions").getKeys(false)) {
            PotionEffectType potionEffectType = PotionEffectType.getByName(potionKeys);
            int lvl = config.getInt("potion-limits.potions." + potionKeys);

            potionLimits.put(potionEffectType, lvl);
        }

        statsEnabled = config.getBoolean("stats.enabled");
        trackStats = config.getBoolean("stats.track-stats");

        if(events.get("claim-chests") != null) {
            for(String claimChestKeys : events.getConfigurationSection("claim-chests").getKeys(false)) {
                UUID uuid = UUID.fromString(claimChestKeys);
                int x = events.getInt("claim-chests." + claimChestKeys + ".location.x");
                int y = events.getInt("claim-chests." + claimChestKeys + ".location.y");
                int z = events.getInt("claim-chests." + claimChestKeys + ".location.z");
                String worldName = events.getString("claim-chests." + claimChestKeys + ".location.world");

                Location chestLocation = new Location(Bukkit.getWorld(worldName), x, y, z);

                String lootTable = events.getString("claim-chests." + claimChestKeys + ".table");
                ClaimChestType type = ClaimChestType.valueOf(events.getString("claim-chests." + claimChestKeys + ".type"));

                ClaimChest claimChest = new ClaimChest(uuid, chestLocation, lootTable, type);

                ChestManager.getLoadedChests().add(claimChest);
            }
        }

        if(events.get("palace-chests") != null) {
            for(String palaceChestKeys : events.getConfigurationSection("palace-chests").getKeys(false)) {
                UUID uuid = UUID.fromString(palaceChestKeys);
                int x = events.getInt("palace-chests." + palaceChestKeys + ".location.x");
                int y = events.getInt("palace-chests." + palaceChestKeys + ".location.y");
                int z = events.getInt("palace-chests." + palaceChestKeys + ".location.z");
                String worldName = events.getString("palace-chests." + palaceChestKeys + ".location.world");

                Location chestLocation = new Location(Bukkit.getWorld(worldName), x, y, z);

                String lootTable = events.getString("palace-chests." + palaceChestKeys + ".table");
                int tier = events.getInt("palace-chests." + palaceChestKeys + ".tier");

                PalaceChest palaceChest = new PalaceChest(uuid, chestLocation, lootTable, tier);

                ChestManager.getLoadedChests().add(palaceChest);
            }
        }

        if(events.get("loot-tables") != null) {
            for(String lootTableKeys : events.getConfigurationSection("loot-tables").getKeys(false)) {
                try {
                    Inventory inventory = InvTools.inventoryFromBase64(events.getString("loot-tables." + lootTableKeys + ".contents"));

                    LootTables.getLootTables().put(lootTableKeys, inventory);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Location overworldSpawn = new Location(Bukkit.getWorlds().get(0), 0, 100, 0), endSpawn = new Location(Bukkit.getWorlds().get(2), 0, 100, 0), endExit = new Location(Bukkit.getWorlds().get(0), 0, 100, 0);

        if(config.getString("locations.overworld-spawn.world") != null) {
            overworldSpawn.setX(config.getDouble("locations.overworld-spawn.x"));
            overworldSpawn.setY(config.getDouble("locations.overworld-spawn.y"));
            overworldSpawn.setZ(config.getDouble("locations.overworld.spawn.z"));
            overworldSpawn.setYaw((float)config.getDouble("locations.overworld-spawn.yaw"));
            overworldSpawn.setPitch((float)config.getDouble("locations.overworld-spawn.pitch"));
            overworldSpawn.setWorld(Bukkit.getWorld(config.getString("locations.overworld-spawn.world")));
        }

        if(config.getString("locations.end-spawn.world") != null) {
            endSpawn.setX(config.getDouble("locations.end-spawn.x"));
            endSpawn.setY(config.getDouble("locations.end-spawn.y"));
            endSpawn.setZ(config.getDouble("locations.end-spawn.z"));
            endSpawn.setYaw((float)config.getDouble("locations.end-spawn.yaw"));
            endSpawn.setPitch((float)config.getDouble("locations.end.spawn-pitch"));
            endSpawn.setWorld(Bukkit.getWorld(config.getString("locations.end-spawn.world")));
        }

        if(config.getString("locations.end-exit.world") != null) {
            endExit.setX(config.getDouble("locations.end-exit.x"));
            endExit.setY(config.getDouble("locations.end-exit.y"));
            endExit.setZ(config.getDouble("locations.end-exit.z"));
            endExit.setYaw((float)config.getDouble("locations.end-exit.yaw"));
            endExit.setPitch((float)config.getDouble("locations.end.exit-pitch"));
            endExit.setWorld(Bukkit.getWorld(config.getString("locations.end-exit.world")));
        }

        Locations.setSpawnLocation(overworldSpawn);
        Locations.setEndSpawnLocation(endSpawn);
        Locations.setEndExitLocation(endExit);

        Logger.log("Loaded " + enchantmentLimits.size() + " Enchantment limits");
        Logger.log("Loaded " + potionLimits.size() + " Potion limits");
        Logger.log("Loaded " + ChestManager.getLoadedChests().size() + " Event chests");
        Logger.log("Loaded " + LootTables.getLootTables().size() + " Loot tables");
    }

}
