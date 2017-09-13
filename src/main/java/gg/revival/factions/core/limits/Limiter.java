package gg.revival.factions.core.limits;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.limits.listeners.EnchantLimitListener;
import gg.revival.factions.core.limits.listeners.PotionLimitListener;
import gg.revival.factions.core.tools.Configuration;
import org.bukkit.Bukkit;

public class Limiter {

    public static void onEnable() {
        loadListeners();
    }

    public static void loadListeners() {
        if(Configuration.limitEnchants)
            Bukkit.getPluginManager().registerEvents(new EnchantLimitListener(), FC.getFactionsCore());

        if(Configuration.limitPotions)
            Bukkit.getPluginManager().registerEvents(new PotionLimitListener(), FC.getFactionsCore());
    }

}
