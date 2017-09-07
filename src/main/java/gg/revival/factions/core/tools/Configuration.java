package gg.revival.factions.core.tools;

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

}
