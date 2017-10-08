package gg.revival.factions.core.mechanics.endermite;

import gg.revival.factions.core.FC;
import lombok.Getter;
import org.bukkit.entity.Endermite;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class EndermiteListener implements Listener {

    @Getter private FC core;

    public EndermiteListener(FC core) {
        this.core = core;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if(!core.getConfiguration().settingsDisableEndermites)
            return;

        if(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) return;

        if(event.getEntity() instanceof Endermite)
            event.setCancelled(true);
    }

}
