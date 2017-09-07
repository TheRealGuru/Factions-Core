package gg.revival.factions.core.deathbans.listener;

import gg.revival.factions.core.deathbans.Death;
import gg.revival.factions.core.deathbans.Deathbans;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class DeathbanListener implements Listener
{

    @EventHandler
    public void onPlayerLoginAttempt(AsyncPlayerPreLoginEvent event)
    {
        UUID uuid = event.getUniqueId();
        Death death = Deathbans.getActiveDeathban(uuid);

        if(death == null) return;

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Deathbans.getDeathbanMessage(death));
    }

    // TODO: Perform deathban action on player death, need to create player stats w/ playtime first though!

}
