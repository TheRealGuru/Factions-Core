package gg.revival.factions.core.bastion.combatprotection;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.timers.TimerManager;
import gg.revival.factions.timers.TimerType;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CombatProtection {

    @Getter private FC core;

    public CombatProtection(FC core) {
        this.core = core;
    }

    /**
     * Returns true if the given player has PvP protection
     * @param player
     * @return
     */
    public boolean hasProt(Player player) {
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer == null) return false;

        return facPlayer.isBeingTimed(TimerType.PVPPROT);
    }

    /**
     * Returns true if the given player has PvP safety
     * @param player
     * @return
     */
    public boolean hasSafety(Player player) {
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer == null) return false;

        return facPlayer.isBeingTimed(TimerType.SAFETY);
    }

    /**
     * Removes a given players PvP protection
     * @param player
     */
    public void takeProtection(Player player) {
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer == null) return;

        if(facPlayer.isBeingTimed(TimerType.PVPPROT))
            facPlayer.removeTimer(TimerType.PVPPROT);

        player.sendMessage(ChatColor.YELLOW + "Your PvP protection has been removed");

        core.getDatabaseManager().saveTimerData(facPlayer, false);
    }

    /**
     * Returns a given players PvP safety
     * @param player
     */
    public void takeSafety(Player player) {
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer == null) return;

        if(facPlayer.isBeingTimed(TimerType.SAFETY))
            facPlayer.removeTimer(TimerType.SAFETY);

        player.sendMessage(ChatColor.YELLOW + "Your PvP safety has been removed");
    }

    /**
     * Gives a given player PvP protection for a set amount of time (in seconds)
     * @param player
     * @param duration
     */
    public void giveProtection(Player player, int duration) {
        if(!core.getConfiguration().pvpProtEnabled) return;

        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer == null) return;

        if(facPlayer.isBeingTimed(TimerType.PVPPROT))
            facPlayer.getTimer(TimerType.PVPPROT).setExpire(System.currentTimeMillis() + (duration * 1000L));
        else
            facPlayer.addTimer(TimerManager.createTimer(TimerType.PVPPROT, duration));
    }

    /**
     * Gives a given player PvP safety for a set amount of time (in seconds)
     * @param player
     * @param duration
     */
    public void giveSafety(Player player, int duration) {
        if(!core.getConfiguration().pvpSafetyEnabled) return;

        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer == null) return;

        if(facPlayer.isBeingTimed(TimerType.SAFETY))
            facPlayer.getTimer(TimerType.SAFETY).setExpire(System.currentTimeMillis() + (duration * 1000L));
        else
            facPlayer.addTimer(TimerManager.createTimer(TimerType.SAFETY, duration));
    }

}
