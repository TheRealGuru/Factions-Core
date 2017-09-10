package gg.revival.factions.core.bastion.shield;

import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ListenableFuture;
import gg.revival.factions.claims.Claim;
import gg.revival.factions.claims.ServerClaimType;
import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.obj.PlayerFaction;
import gg.revival.factions.obj.ServerFaction;
import gg.revival.factions.timers.TimerType;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class ShieldUpdateTask extends AbstractFuture implements Runnable, ListenableFuture
{

    private final ShieldUpdateRequest request;

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        Set<BlockPos> shownGlass = new HashSet<>();
        FPlayer facPlayer = PlayerManager.getPlayer(request.getPlayer().getUuid());

        if(facPlayer == null) return;

        for(Claim claims : ShieldTools.getNearbyClaims(request.getPosition()))
        {
            if(!facPlayer.isBeingTimed(TimerType.TAG))
            {
                if(claims.getClaimOwner() instanceof ServerFaction)
                {
                    ServerFaction serverFaction = (ServerFaction)claims.getClaimOwner();

                    if(serverFaction.getType().equals(ServerClaimType.SAFEZONE)) continue;
                }
            }

            else
            {
                if(claims.getClaimOwner() instanceof ServerFaction)
                {
                    ServerFaction serverFaction = (ServerFaction)claims.getClaimOwner();

                    if(serverFaction.getType().equals(ServerClaimType.EVENT) && !facPlayer.isBeingTimed(TimerType.PROGRESSION) && !facPlayer.isBeingTimed(TimerType.PVPPROT)) continue;
                }
            }

            if(facPlayer.isBeingTimed(TimerType.PROGRESSION))
            {
                if(claims.getClaimOwner() instanceof PlayerFaction)
                {
                    PlayerFaction playerFaction = (PlayerFaction)claims.getClaimOwner();

                    if(playerFaction.getRoster(true).contains(facPlayer.getUuid()) && !facPlayer.isBeingTimed(TimerType.PVPPROT)) continue;
                }

                if(claims.getClaimOwner() instanceof ServerFaction)
                {
                    ServerFaction serverFaction = (ServerFaction)claims.getClaimOwner();

                    if(serverFaction.getType().equals(ServerClaimType.ROAD)) continue;
                    if(serverFaction.getType().equals(ServerClaimType.SAFEZONE) && !facPlayer.isBeingTimed(TimerType.TAG)) continue;
                }
            }

            if(facPlayer.isBeingTimed(TimerType.PVPPROT))
            {
                if(claims.getClaimOwner() instanceof ServerFaction)
                {
                    ServerFaction serverFaction = (ServerFaction)claims.getClaimOwner();

                    if(!serverFaction.getType().equals(ServerClaimType.EVENT)) continue;
                }
            }

            int y = request.getPosition().getY() - 2;
            int endingY = y + 5;

            for(int i = y; i < endingY; i++)
            {
                for(BlockPos nearby : ShieldTools.getClaimPerimeterAsBlockPos(claims, i))
                {
                    if(nearby.distanceSquared(request.getPosition()) > 100) continue;
                    if(request.getPlayer().getLastShownBlocks() != null && request.getPlayer().getLastShownBlocks().contains(shownGlass)) continue;
                    if(nearby.isSolid()) continue;

                    shownGlass.add(nearby);
                }
            }
        }

        Collection<BlockPos> lastShown = request.getPlayer().getLastShownBlocks();
        if(lastShown == null) lastShown = new HashSet<>();

        for(BlockPos noLongerShown : lastShown)
        {
            if(shownGlass.contains(noLongerShown)) continue;
            request.getBukkitPlayer().sendBlockChange(noLongerShown.getBukkitLocation(), Material.AIR, (byte)0);
        }

        for(BlockPos toShow : shownGlass)
            request.getBukkitPlayer().sendBlockChange(toShow.getBukkitLocation(), Material.STAINED_GLASS, (byte) 14);

        request.getPlayer().setLastShownBlocks(shownGlass);
        set(null);
    }

}
