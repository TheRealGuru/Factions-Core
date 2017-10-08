package gg.revival.factions.core.locations.listener;

import gg.revival.factions.claims.Claim;
import gg.revival.factions.claims.ClaimManager;
import gg.revival.factions.claims.ServerClaimType;
import gg.revival.factions.core.FC;
import gg.revival.factions.obj.ServerFaction;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class LocationsListener implements Listener {

    @Getter private FC core;

    public LocationsListener(FC core) {
        this.core = core;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(!player.hasPlayedBefore()) {
            player.teleport(core.getLocations().getSpawnLocation());
            core.getPlayerTools().cleanupPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(core.getLocations().getSpawnLocation());
        core.getPlayerTools().cleanupPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Location from = event.getFrom(), to = event.getTo();

        if(from.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
            Claim inside = ClaimManager.getClaimAt(from, false);

            if(inside == null || !(inside.getClaimOwner() instanceof ServerFaction)) return;

            ServerFaction serverFaction = (ServerFaction)inside.getClaimOwner();

            if(!serverFaction.getType().equals(ServerClaimType.SAFEZONE)) return;

            event.setTo(core.getLocations().getSpawnLocation());
        }

        if(to.getWorld().getEnvironment().equals(World.Environment.THE_END))
            event.setTo(core.getLocations().getEndSpawnLocation());

        if(from.getWorld().getEnvironment().equals(World.Environment.THE_END))
            event.setTo(core.getLocations().getEndExitLocation());
    }

}
