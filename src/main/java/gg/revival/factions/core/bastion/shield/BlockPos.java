package gg.revival.factions.core.bastion.shield;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

class BlockPos {

    @Getter int x, y, z;
    @Getter String worldName;

    BlockPos(int x, int y, int z, String worldName) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
    }

    Location getBukkitLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    int distanceSquared(BlockPos otherPos) {
        if(!otherPos.getWorldName().equalsIgnoreCase(worldName)) return 0;
        return square(x - otherPos.x) + square(y - otherPos.y) + square(z - otherPos.z);
    }

    private int square(int i) {
        return i * i;
    }

    boolean isSolid() {
        return getBukkitLocation().getBlock() != null && !getBukkitLocation().getBlock().getType().equals(Material.AIR);
    }

}
