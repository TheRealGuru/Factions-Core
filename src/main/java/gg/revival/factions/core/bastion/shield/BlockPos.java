package gg.revival.factions.core.bastion.shield;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class BlockPos
{

    @Getter int x, y, z;
    @Getter String worldName;

    public BlockPos(int x, int y, int z, String worldName)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
    }

    public Location getBukkitLocation()
    {
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public int distanceSquared(BlockPos otherPos)
    {
        if(!otherPos.getWorldName().equalsIgnoreCase(worldName)) return 0;
        return square(x - otherPos.x) + square(y - otherPos.y) + square(z - otherPos.z);
    }

    public int square(int i)
    {
        return i * i;
    }

    public boolean isSolid()
    {
        if(getBukkitLocation().getBlock() != null && !getBukkitLocation().getBlock().getType().equals(Material.AIR))
            return true;

        return false;
    }

}
