package gg.revival.factions.core.bastion.shield;

import gg.revival.factions.claims.Claim;
import gg.revival.factions.claims.ClaimManager;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ShieldTools {

    /**
     * Returns a collection of nearby claims based on the given BlockPos object
     * @param pos
     * @return
     */
    public Collection<Claim> getNearbyClaims(BlockPos pos) {
        Collection<Claim> nearbyClaims = new CopyOnWriteArrayList<>();
        List<Claim> claimCache = new CopyOnWriteArrayList<>(ClaimManager.getActiveClaims());

        for(Claim claims : claimCache) {
            if(!isNearbyClaim(claims, pos, 10)) continue;

            nearbyClaims.add(claims);
        }

        return nearbyClaims;
    }

    /**
     * Returns true if the given BlockPos is inside the given Claim
     * @param claim
     * @param pos
     * @return
     */
    public boolean isInsideClaim(Claim claim, BlockPos pos) {
        if (!pos.getWorldName().equals(claim.getWorldName())) return false;

        double xMin = Math.min(claim.getX1(), claim.getX2());
        double xMax = Math.max(claim.getX1(), claim.getX2());
        double yMin = Math.min(claim.getY1(), claim.getY2());
        double yMax = Math.max(claim.getY1(), claim.getY2());
        double zMin = Math.min(claim.getZ1(), claim.getZ2());
        double zMax = Math.max(claim.getZ1(), claim.getZ2());

        return pos.getX() >= xMin && pos.getX() <= xMax && pos.getY() >= yMin && pos.getY() <= yMax && pos.getZ() >= zMin && pos.getZ() <= zMax;
    }

    /**
     * Returns true if the given BlockPos is nearby the given Claim
     * @param claim
     * @param pos
     * @param dist
     * @return
     */
    public boolean isNearbyClaim(Claim claim, BlockPos pos, int dist) {
        if (!pos.getWorldName().equalsIgnoreCase(claim.getWorldName())) return false;

        if(isInsideClaim(claim, new BlockPos(pos.getX(), pos.getY(), pos.getZ() + dist, pos.getWorldName())))
            return true;

        if(isInsideClaim(claim, new BlockPos(dist + pos.getX(), pos.getY(), pos.getZ(), pos.getWorldName())))
            return true;

        if(isInsideClaim(claim, new BlockPos(pos.getX(), pos.getY(), pos.getZ() - dist, pos.getWorldName())))
            return true;

        if(isInsideClaim(claim, new BlockPos(pos.getX() - dist, pos.getY(), pos.getZ(), pos.getWorldName())))
            return true;

        if(isInsideClaim(claim, new BlockPos(pos.getX() - dist, pos.getY(), pos.getZ() + dist, pos.getWorldName())))
            return true;

        if(isInsideClaim(claim, new BlockPos(pos.getX() + dist, pos.getY(), pos.getZ() - dist, pos.getWorldName())))
            return true;

        if(isInsideClaim(claim, new BlockPos(pos.getX() - dist, pos.getY(), pos.getZ() - dist, pos.getWorldName())))
            return true;

        if(isInsideClaim(claim, new BlockPos(pos.getX() + dist, pos.getY(), pos.getZ() + dist, pos.getWorldName())))
            return true;

        return false;
    }

    /**
     * Gets perimeter blocks of a claim in BlockPos form, useful for quick async calculations
     * @param claim
     * @param yLevel
     * @return
     */
    public Collection<BlockPos> getClaimPerimeterAsBlockPos(Claim claim, int yLevel) {
        Collection<BlockPos> blocks = new CopyOnWriteArrayList<>();

        double xMin = Math.min(claim.getX1(), claim.getX2());
        double xMax = Math.max(claim.getX1(), claim.getX2());
        double zMin = Math.min(claim.getZ1(), claim.getZ2());
        double zMax = Math.max(claim.getZ1(), claim.getZ2());

        for(int x = (int)xMin; x <= xMax; x++) {
            for(int z = (int)zMin; z <= zMax; z++) {
                if(x == xMin || x == xMax || z == zMin || z == zMax) {
                    BlockPos block = new BlockPos(x, yLevel, z, claim.getWorldName());
                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

}
