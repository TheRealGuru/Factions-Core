package gg.revival.factions.core.mechanics.enderpearlcd;

import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.core.tools.TimeTools;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.timers.TimerManager;
import gg.revival.factions.timers.TimerType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EnderpearlCDTask {

    /**
     * Attempts to throw an enderpearl. Returns true if possible
     * @param uuid The player UUID
     * @param dur The duration the enderpearl should be locked for
     * @return Enderpearl throw was successful
     */
    public static boolean attemptEnderpearl(UUID uuid, int dur) {
        if(Bukkit.getPlayer(uuid) == null)
            return false;

        Player player = Bukkit.getPlayer(uuid);
        FPlayer facPlayer = PlayerManager.getPlayer(uuid);

        if(facPlayer == null) return false;

        if(facPlayer.isBeingTimed(TimerType.ENDERPEARL)) {
            long expireDur = facPlayer.getTimer(TimerType.ENDERPEARL).getExpire() - System.currentTimeMillis();
            player.sendMessage(ChatColor.RED + "Your enderpearl is locked for another " + ChatColor.RED + "" + ChatColor.BOLD + TimeTools.getFormattedCooldown(true, expireDur) + ChatColor.RED + " seconds");
            return false;
        }

        facPlayer.addTimer(TimerManager.createTimer(TimerType.ENDERPEARL, dur));
        return true;
    }

}
