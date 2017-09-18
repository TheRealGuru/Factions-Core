package gg.revival.factions.core.bastion.tag;

import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.timers.TimerManager;
import gg.revival.factions.timers.TimerType;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager
{

    @Getter static Map<UUID, CombatLogger> combatLoggers = new HashMap<>();

    public static boolean hasLogger(UUID uuid)
    {
        return combatLoggers.containsKey(uuid);
    }

    public static CombatLogger getLogger(UUID uuid) {
        if(!hasLogger(uuid)) return null;
        return combatLoggers.get(uuid);
    }

    public static long getTag(UUID uuid) {
        FPlayer facPlayer = PlayerManager.getPlayer(uuid);

        if(facPlayer == null) return 0L;

        if(facPlayer.isBeingTimed(TimerType.TAG))
            return facPlayer.getTimer(TimerType.TAG).getExpire() - System.currentTimeMillis();

        return 0L;
    }

    public static void tagPlayer(Player player, TagReason reason) {
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer == null) return;

        int duration = 0;

        if(reason.equals(TagReason.ATTACKED))
            duration = Configuration.tagAttacked;

        if(reason.equals(TagReason.ATTACKER))
            duration = Configuration.tagAttacker;

        if(facPlayer.isBeingTimed(TimerType.TAG)) {
            int current = (int)((getTag(player.getUniqueId()) - System.currentTimeMillis()) / 1000L);

            if(current >= duration) return;

            long newExpire = System.currentTimeMillis() + (duration * 1000L);

            facPlayer.getTimer(TimerType.TAG).setExpire(newExpire);

            return;
        }

        facPlayer.addTimer(TimerManager.createTimer(TimerType.TAG, duration));

        player.sendMessage(ChatColor.RED + "You are now combat-tagged. You will not be able to enter SafeZone claims until this timer expires");
    }

}
