package gg.revival.factions.core.locations.listener;

import gg.revival.factions.core.locations.Locations;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class LocationsListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(!player.hasPlayedBefore())
            player.teleport(Locations.getSpawnLocation());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        player.teleport(Locations.getSpawnLocation());
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        Location toLocation = player.getLocation();

        if(toLocation.getWorld().getEnvironment().equals(World.Environment.THE_END))
            player.teleport(Locations.getEndSpawnLocation());
    }

}
