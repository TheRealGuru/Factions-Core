package gg.revival.factions.core.bastion;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.bastion.combatprotection.CombatProtection;
import gg.revival.factions.core.bastion.combatprotection.CombatProtectionListener;
import gg.revival.factions.core.bastion.combatprotection.PvPCommand;
import gg.revival.factions.core.bastion.logout.commands.LogoutCommand;
import gg.revival.factions.core.bastion.logout.listeners.LogoutListener;
import gg.revival.factions.core.bastion.logout.tasks.LogoutTask;
import gg.revival.factions.core.bastion.shield.Shield;
import gg.revival.factions.core.bastion.shield.ShieldListener;
import gg.revival.factions.core.bastion.shield.ShieldTools;
import gg.revival.factions.core.bastion.tag.CombatListener;
import gg.revival.factions.core.bastion.tag.CombatLogger;
import gg.revival.factions.core.bastion.tag.CombatManager;
import gg.revival.factions.core.bastion.tag.NPCTools;
import lombok.Getter;
import org.bukkit.Bukkit;

public class Bastion {

    @Getter private FC core;
    @Getter public CombatManager combatManager;
    @Getter public CombatProtection combatProtection;
    @Getter public NPCTools npcTools;
    @Getter public Shield shield;
    @Getter public ShieldTools shieldTools;
    @Getter public LogoutTask logoutTask;

    public Bastion(FC core) {
        this.core = core;
        this.combatManager = new CombatManager(core);
        this.combatProtection = new CombatProtection(core);
        this.npcTools = new NPCTools(core);
        this.shield = new Shield();
        this.shieldTools = new ShieldTools();
        this.logoutTask = new LogoutTask(core);

        onEnable();
    }

    public void onEnable() {
        loadCommands();
        loadListeners();
    }

    public void onDisable() {
        for(CombatLogger loggers : combatManager.getCombatLoggers().values())
            loggers.destroy();
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new CombatListener(core), core);
        Bukkit.getPluginManager().registerEvents(new LogoutListener(core), core);
        Bukkit.getPluginManager().registerEvents(new ShieldListener(core), core);
        Bukkit.getPluginManager().registerEvents(new CombatProtectionListener(core), core);
    }

    private void loadCommands() {
        core.getCommand("logout").setExecutor(new LogoutCommand(core));
        core.getCommand("pvp").setExecutor(new PvPCommand(core));
    }

}
