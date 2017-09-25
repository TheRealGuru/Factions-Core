package gg.revival.factions.core.mechanics.emeraldxp;

import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;

public class EmeraldEXPListener implements Listener {

    @EventHandler
    public void onExpUseEvent(ExpBottleEvent event) {
        ThrownExpBottle bottle = event.getEntity();

        if(!(bottle.getShooter() instanceof Player)) return;

        Player player = (Player)bottle.getShooter();

        event.setExperience(0);

        player.setLevel(player.getLevel() + 1);
    }

}
