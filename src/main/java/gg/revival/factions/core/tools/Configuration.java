package gg.revival.factions.core.tools;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class Configuration
{

    public static String databaseName = "factions";

    public static int logoutTimer = 30;
    public static int tagAttacker = 60;
    public static int tagAttacked = 30;
    public static boolean pvpProtEnabled = true;
    public static int pvpProtDuration = 1800;
    public static boolean pvpSafetyEnabled = true;
    public static int pvpSafetyDuration = 5;
    public static int loggerDuration = 20;
    public static int loggerEnemyDistance = 30;

    public static boolean archerEnabled = true;
    public static double maxArcherDamage = 4.0;
    public static boolean scoutEnabled = true;
    public static boolean bardEnabled = true;

    public static boolean deathbansEnabled = true;
    public static int normalDeathban = 86400;
    public static int eventDeathban = 10800;
    public static int newDeathban = 3600;

    public static boolean livesEnabled = true;

    public static boolean emeraldXpEnabled = true;
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
    public static float miningGoldChance = 0.02f;
    public static float miningDiamondChance = 0.007f;
    public static float miningEmeraldChance = 0.003f;
    public static boolean announceFoundGold = false;
    public static boolean announceFoundDiamond = true;
    public static boolean announceFoundEmerald = true;

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

    public static void load()
    {
        FileManager.createFiles();

        FileConfiguration config = FileManager.getConfig();

        databaseName = config.getString("database.database-name");

        logoutTimer = config.getInt("bastion.logout-timer");
        tagAttacker = config.getInt("bastion.combat-tag.attacker-duration");
        tagAttacked = config.getInt("bastion.combat-tag.attacked-duration");
        pvpProtEnabled = config.getBoolean("bastion.pvp-prot.enabled");
        pvpProtDuration = config.getInt("bastion.pvp-prot.duration");
        pvpSafetyEnabled = config.getBoolean("bastion.pvp-safety.enabled");
        pvpSafetyDuration = config.getInt("bastion.pvp-safety.duration");
        loggerDuration = config.getInt("bastion.loggers.duration");
        loggerEnemyDistance = config.getInt("bastion.loggers.enemy-distance");

        archerEnabled = config.getBoolean("classes.archer.enabled");
        maxArcherDamage = config.getDouble("classes.archer.max-damage");
        scoutEnabled = config.getBoolean("classes.scout.enabled");
        bardEnabled = config.getBoolean("classes.bard.enabled");

        deathbansEnabled = config.getBoolean("deathbans.enabled");
        normalDeathban = config.getInt("deathbans.durations.normal");
        eventDeathban = config.getInt("deathbans.durations.event");
        newDeathban = config.getInt("deathbans.durations.new");

        livesEnabled = config.getBoolean("lives.enabled");

        emeraldXpEnabled = config.getBoolean("mechanics.emerald-xp");
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
        announceFoundGold = config.getBoolean("mining.announce.gold");
        announceFoundDiamond = config.getBoolean("mining.announce.diamond");
        announceFoundEmerald = config.getBoolean("mining.announce.emerald");

        progressEnabled = config.getBoolean("progression.enabled");
        progressDuration = config.getInt("progression.duration");

        limitEnchants = config.getBoolean("enchant-limits.enabled");
        limitPotions = config.getBoolean("potion-limits.enabled");

        for(String enchantKeys : config.getConfigurationSection("enchant-limits.enchants").getKeys(false))
        {
            Enchantment enchantment = Enchantment.getByName(enchantKeys);
            int lvl = config.getInt("enchant-limits.enchants." + enchantKeys);

            enchantmentLimits.put(enchantment, lvl);
        }

        for(String potionKeys : config.getConfigurationSection("potion-limits.potions").getKeys(false))
        {
            PotionEffectType potionEffectType = PotionEffectType.getByName(potionKeys);
            int lvl = config.getInt("potion-limits.potions." + potionKeys);

            potionLimits.put(potionEffectType, lvl);
        }

        Logger.log("Loaded " + enchantmentLimits.size() + " Enchantment limits");
        Logger.log("Loaded " + potionLimits.size() + " Potion limits");
    }

    public static void reload()
    {
        FileManager.reloadFiles();

        load();
    }

}
