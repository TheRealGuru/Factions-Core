package gg.revival.factions.core.locations.listener;

import gg.revival.factions.claims.Claim;
import gg.revival.factions.claims.ClaimManager;
import gg.revival.factions.claims.ServerClaimType;
import gg.revival.factions.core.locations.Locations;
import gg.revival.factions.obj.ServerFaction;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
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
        event.setRespawnLocation(Locations.getSpawnLocation());
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Location from = event.getFrom(), to = event.getTo();

        if(from.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
            Claim inside = ClaimManager.getClaimAt(from, false);

            if(inside == null || !(inside.getClaimOwner() instanceof ServerFaction)) return;

            ServerFaction serverFaction = (ServerFaction)inside.getClaimOwner();

            if(!serverFaction.getType().equals(ServerClaimType.SAFEZONE)) return;

            event.setTo(Locations.getSpawnLocation());
        }

        if(to.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            event.setTo(Locations.getEndSpawnLocation());
        }

        if(from.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            event.setTo(Locations.getEndExitLocation());
        }
    }

}
