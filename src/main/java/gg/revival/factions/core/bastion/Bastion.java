package gg.revival.factions.core.bastion;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.bastion.combatprotection.CombatProtectionListener;
import gg.revival.factions.core.bastion.combatprotection.PvPCommand;
import gg.revival.factions.core.bastion.logout.commands.LogoutCommand;
import gg.revival.factions.core.bastion.logout.listeners.LogoutListener;
import gg.revival.factions.core.bastion.shield.Shield;
import gg.revival.factions.core.bastion.tag.CombatListener;
import gg.revival.factions.core.bastion.tag.CombatLogger;
import gg.revival.factions.core.bastion.tag.CombatManager;
import gg.revival.factions.core.tools.Configuration;
import org.bukkit.Bukkit;

public class Bastion
{

    public static void onEnable()
    {
        Shield.onEnable();

        loadCommands();
        loadListeners();
    }

    public static void onDisable()
    {
        for(CombatLogger loggers : CombatManager.getCombatLoggers().values())
        {
            loggers.destroy();
        }
    }

    public static void loadListeners()
    {
        Bukkit.getPluginManager().registerEvents(new CombatListener(), FC.getFactionsCore());
        Bukkit.getPluginManager().registerEvents(new LogoutListener(), FC.getFactionsCore());

        if(Configuration.pvpSafetyEnabled && Configuration.pvpProtEnabled)
            Bukkit.getPluginManager().registerEvents(new CombatProtectionListener(), FC.getFactionsCore());
    }

    public static void loadCommands()
    {
        FC.getFactionsCore().getCommand("logout").setExecutor(new LogoutCommand());
        FC.getFactionsCore().getCommand("pvp").setExecutor(new PvPCommand());
    }

}
