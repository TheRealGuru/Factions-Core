package gg.revival.factions.core.bastion.combatprotection;

import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.timers.TimerManager;
import gg.revival.factions.timers.TimerType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CombatProtection
{

    public static boolean hasProt(Player player)
    {
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer.isBeingTimed(TimerType.PVPPROT))
            return true;

        return false;
    }

    public static boolean hasSafety(Player player)
    {
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer.isBeingTimed(TimerType.SAFETY))
            return true;

        return false;
    }

    public static void takeProtection(Player player)
    {
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer.isBeingTimed(TimerType.PVPPROT))
        {
            facPlayer.removeTimer(TimerType.PVPPROT);
        }

        player.sendMessage(ChatColor.RED + "Your PvP protection has been removed");
    }

    public static void takeSafety(Player player)
    {
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer.isBeingTimed(TimerType.SAFETY))
        {
            facPlayer.removeTimer(TimerType.SAFETY);
        }

        player.sendMessage(ChatColor.RED + "Your PvP safety has been removed");
    }

    public static void giveProtection(Player player, int duration)
    {
        if(!Configuration.pvpProtEnabled) return;

        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer.isBeingTimed(TimerType.PVPPROT))
        {
            facPlayer.getTimer(TimerType.PVPPROT).setExpire(System.currentTimeMillis() + (duration * 1000L));
        }

        else
        {
            facPlayer.addTimer(TimerManager.createTimer(TimerType.PVPPROT, duration));
        }
    }

    public static void giveSafety(Player player, int duration)
    {
        if(!Configuration.pvpSafetyEnabled) return;

        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer.isBeingTimed(TimerType.SAFETY))
        {
            facPlayer.getTimer(TimerType.SAFETY).setExpire(System.currentTimeMillis() + (duration * 1000L));
        }

        else
        {
            facPlayer.addTimer(TimerManager.createTimer(TimerType.SAFETY, duration));
        }
    }

}
