package gg.revival.factions.core.limits;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.limits.listeners.EnchantLimitListener;
import gg.revival.factions.core.limits.listeners.PotionLimitListener;
import org.bukkit.Bukkit;

public class Limiter {

    public static void onEnable() {
        loadListeners();
    }

    public static void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new EnchantLimitListener(), FC.getFactionsCore());
        Bukkit.getPluginManager().registerEvents(new PotionLimitListener(), FC.getFactionsCore());
    }

}
