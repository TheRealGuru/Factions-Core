package gg.revival.factions.core;

import gg.revival.factions.core.bastion.Bastion;
import gg.revival.factions.core.classes.Classes;
import gg.revival.factions.core.deathbans.Deathbans;
import gg.revival.factions.core.lives.Lives;
import gg.revival.factions.core.mechanics.Mechanics;
import gg.revival.factions.core.mining.Mining;
import gg.revival.factions.core.progression.Progression;
import gg.revival.factions.core.servermode.ServerMode;
import gg.revival.factions.core.ui.UI;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class FC extends JavaPlugin
{

    @Getter static FC factionsCore;

    @Override
    public void onEnable()
    {
        factionsCore = this;

        Bastion.onEnable();
        Deathbans.onEnable();
        Classes.onEnable();
        Lives.onEnable();
        Mechanics.onEnable();
        Mining.onEnable();
        Progression.onEnable();
        ServerMode.onEnable();
        UI.onEnable();
    }

    @Override
    public void onDisable()
    {
        Bastion.onDisable();
        Deathbans.onDisable();
        Classes.onDisable();
        Lives.onDisable();
        Progression.onDisable();
        ServerMode.onDisable();
        UI.onDisable();

        factionsCore = null;
    }
}
