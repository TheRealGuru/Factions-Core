package gg.revival.factions.core.tools;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.classes.cont.Archer;
import gg.revival.factions.core.classes.cont.Bard;
import gg.revival.factions.core.classes.cont.Miner;
import gg.revival.factions.core.classes.cont.Scout;
import gg.revival.factions.core.events.chests.ClaimChest;
import gg.revival.factions.core.events.chests.ClaimChestType;
import gg.revival.factions.core.events.chests.PalaceChest;
import gg.revival.factions.core.servermode.ServerState;
import lombok.Getter;
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

    @Getter private FC core;

    public Configuration(FC core) {
        this.core = core;

        load();
    }

    public String databaseName = "factions";

    public boolean automateEvents = true;
    public boolean playChestEffects = true;
    public int pullsPerKey = 3;
    public int defaultKothDuration = 60;
    public int defaultKothWinCondition = 15;
    public int defaultKothKeys = 3;
    public int defaultDtcRegen = 15;
    public int defaultDtcWincond = 500;
    public int defaultDtcKeys = 3;
    public int defaultPalaceKeys = 10;

    public int logoutTimer = 30;
    public int tagAttacker = 60;
    public int tagAttacked = 30;
    public boolean pvpProtEnabled = true;
    public int pvpProtDuration = 1800;
    public boolean pvpSafetyEnabled = true;
    public int pvpSafetyDuration = 5;
    public int loggerDuration = 20;
    public int loggerEnemyDistance = 30;

    public int classWarmupDelay = 15;
    public int activeSpeedCooldown = 60;
    public int activeJumpCooldown = 60;
    public int activeRegenCooldown = 90;
    public int activeStrengthCooldown = 120;
    public boolean archerEnabled = true;
    public double maxArcherDamage = 4.0;
    public boolean scoutEnabled = true;
    public boolean bardEnabled = true;
    public double bardNearbyCheckDistance = 15.0;
    public boolean minerEnabled = true;

    public boolean deathbansEnabled = true;
    public long normalDeathban = 10000L;
    public long eventDeathban = 10000L;

    public boolean livesEnabled = true;

    public boolean emeraldXpEnabled = true;
    public boolean hardmodeEnabled = true;
    public boolean enderpearlCooldownsEnabled = true;
    public int enderpearlCooldownsDuration = 16;
    public boolean mobstackingEnabled = true;
    public int mobstackingMaxStack = 30;
    public int mobstackingInterval = 5;
    public boolean crowbarsEnabled = true;
    public int crowbarSpawnerUse = 1;
    public int crowbarPortalUse = 6;
    public boolean bookUnenchantingEnabled = true;
    public boolean invalidPearlBlocksEnabled = true;
    public boolean highSpawnersDisabled = true;
    public int highSpawnersHeight = 125;

    public boolean miningEnabled = true;
    public float miningGoldChance = 0.006f;
    public float miningDiamondChance = 0.004f;
    public float miningEmeraldChance = 0.001f;
    public float miningGlowstoneChance = 0.002f;
    public boolean announceFoundGold = false;
    public boolean announceFoundDiamond = true;
    public boolean announceFoundEmerald = true;
    public boolean announceFoundGlowstone = false;

    public boolean progressEnabled = true;
    public int progressDuration = 3600;

    public boolean settingsDisableEndermites = true;
    public boolean settingsDisableBreakingSpawners = true;
    public boolean settingsRemoveGApples = true;
    public boolean protectNetherPortals = true;

    public boolean limitEnchants = true;
    public Map<Enchantment, Integer> enchantmentLimits = new HashMap<>();

    public boolean limitPotions = true;
    public Map<PotionEffectType, Integer> potionLimits = new HashMap<>();

    public boolean statsEnabled = true;
    public boolean trackStats = true;

    public void reload() {
        core.getFileManager().reloadFiles();

        core.getClasses().getEnabledClasses().clear();
        core.getEvents().getEventManager().getEvents().clear();
        core.getEvents().getChestManager().getLoadedChests().clear();
        enchantmentLimits.clear();
        potionLimits.clear();

        load();

        core.getLog().log("Reloaded Configuration");
    }

    public void load() {
        core.getFileManager().createFiles();

        FileConfiguration config = core.getFileManager().getConfig();
        FileConfiguration events = core.getFileManager().getEvents();

        databaseName = config.getString("database.database-name");

        for(ServerState states : ServerState.values()) {
            if(!states.toString().equalsIgnoreCase(config.getString("servermode"))) continue;
            core.getServerMode().setCurrentState(states);
        }

        if(core.getServerMode().getCurrentState() == null) {
            core.getServerMode().setCurrentState(ServerState.NORMAL);
            core.getLog().log(Level.SEVERE, "Server state not found in config.yml! Defaulting to normal settings...");
        } else {
            core.getLog().log("Server state has been set to '" + core.getServerMode().getCurrentState().toString() + "'");
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
            core.getClasses().getEnabledClasses().add(archer);
        }

        if(scoutEnabled) {
            Scout scout = new Scout();
            core.getClasses().getEnabledClasses().add(scout);
        }

        if(bardEnabled) {
            Bard bard = new Bard();
            core.getClasses().getEnabledClasses().add(bard);
        }

        if(minerEnabled) {
            Miner miner = new Miner();
            core.getClasses().getEnabledClasses().add(miner);
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

                core.getEvents().getChestManager().getLoadedChests().add(claimChest);
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

                core.getEvents().getChestManager().getLoadedChests().add(palaceChest);
            }
        }

        if(events.get("loot-tables") != null) {
            for(String lootTableKeys : events.getConfigurationSection("loot-tables").getKeys(false)) {
                try {
                    Inventory inventory = InvTools.inventoryFromBase64(events.getString("loot-tables." + lootTableKeys + ".contents"));

                    core.getEvents().getLootTables().getLootTables().put(lootTableKeys, inventory);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Location overworldSpawn = new Location(Bukkit.getWorlds().get(0), 0, 100, 0), endSpawn = new Location(Bukkit.getWorlds().get(2), 0, 100, 0), endExit = new Location(Bukkit.getWorlds().get(0), 0, 100, 0);

        if(config.getString("locations.overworld-spawn.world") != null) {
            overworldSpawn.setX(config.getDouble("locations.overworld-spawn.x"));
            overworldSpawn.setY(config.getDouble("locations.overworld-spawn.y"));
            overworldSpawn.setZ(config.getDouble("locations.overworld-spawn.z"));
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

        core.getLocations().setSpawnLocation(overworldSpawn);
        core.getLocations().setEndSpawnLocation(endSpawn);
        core.getLocations().setEndExitLocation(endExit);

        core.getLog().log("Loaded " + enchantmentLimits.size() + " Enchantment limits");
        core.getLog().log("Loaded " + potionLimits.size() + " Potion limits");
        core.getLog().log("Loaded " + core.getEvents().getChestManager().getLoadedChests().size() + " Event chests");
        core.getLog().log("Loaded " + core.getEvents().getLootTables().getLootTables().size() + " Loot tables");
    }

}
