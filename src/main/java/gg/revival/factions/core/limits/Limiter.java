package gg.revival.factions.core.limits;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.limits.listeners.EnchantLimitListener;
import gg.revival.factions.core.limits.listeners.PotionLimitListener;
import lombok.Getter;
import org.bukkit.Bukkit;

public class Limiter {

    @Getter private FC core;

    public Limiter(FC core) {
        this.core = core;

        onEnable();
    }

    public void onEnable() {
        loadListeners();
    }

    public void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new EnchantLimitListener(core), core);
        Bukkit.getPluginManager().registerEvents(new PotionLimitListener(core), core);
    }

}
