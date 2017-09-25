package gg.revival.factions.core.bastion.combatprotection;

import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.core.db.DBManager;
import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.timers.TimerManager;
import gg.revival.factions.timers.TimerType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CombatProtection {

    /**
     * Returns true if the given player has PvP protection
     * @param player
     * @return
     */
    public static boolean hasProt(Player player) {
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer == null) return false;

        return facPlayer.isBeingTimed(TimerType.PVPPROT);
    }

    /**
     * Returns true if the given player has PvP safety
     * @param player
     * @return
     */
    public static boolean hasSafety(Player player) {
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer == null) return false;

        return facPlayer.isBeingTimed(TimerType.SAFETY);
    }

    /**
     * Removes a given players PvP protection
     * @param player
     */
    public static void takeProtection(Player player) {
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer == null) return;

        if(facPlayer.isBeingTimed(TimerType.PVPPROT))
            facPlayer.removeTimer(TimerType.PVPPROT);

        player.sendMessage(ChatColor.YELLOW + "Your PvP protection has been removed");

        DBManager.saveTimerData(facPlayer);
    }

    /**
     * Returns a given players PvP safety
     * @param player
     */
    public static void takeSafety(Player player) {
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
    public static void giveProtection(Player player, int duration) {
        if(!Configuration.pvpProtEnabled) return;

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
    public static void giveSafety(Player player, int duration) {
        if(!Configuration.pvpSafetyEnabled) return;

        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer == null) return;

        if(facPlayer.isBeingTimed(TimerType.SAFETY))
            facPlayer.getTimer(TimerType.SAFETY).setExpire(System.currentTimeMillis() + (duration * 1000L));
        else
            facPlayer.addTimer(TimerManager.createTimer(TimerType.SAFETY, duration));
    }

}
