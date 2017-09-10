package gg.revival.factions.core.events.obj;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

public class CapZone {

    @Getter @Setter public int xMin, xMax, yMin, yMax, zMin, zMax;
    @Getter @Setter String worldName;
    @Getter @Setter Location cornerOne, cornerTwo;

    public CapZone(Location cornerOne, Location cornerTwo, String worldName) {
        this.cornerOne = cornerOne;
        this.cornerTwo = cornerTwo;
        this.worldName = worldName;

        if(cornerOne != null && cornerTwo != null)
            update();
    }

    public void update() {
        this.xMin = Math.min(cornerOne.getBlockX(), cornerTwo.getBlockX());
        this.xMax = Math.max(cornerOne.getBlockX(), cornerTwo.getBlockX());
        this.yMin = Math.min(cornerOne.getBlockY(), cornerTwo.getBlockY());
        this.yMax = Math.max(cornerOne.getBlockY(), cornerTwo.getBlockY());
        this.zMin = Math.min(cornerOne.getBlockZ(), cornerTwo.getBlockZ());
        this.zMax = Math.max(cornerOne.getBlockZ(), cornerTwo.getBlockZ());
    }

    public boolean inside(Location location, boolean isPlayer) {
        if(!location.getWorld().getName().equalsIgnoreCase(worldName)) return false;

        int x1 = Math.min(this.xMin, this.xMax);
        int z1 = Math.min(this.zMin, this.zMax);
        int y1 = Math.min(this.yMin, this.yMax);
        int x2 = Math.max(this.xMin, this.xMax);
        int y2 = Math.max(this.yMin, this.yMax);
        int z2 = Math.max(this.zMin, this.zMax);

        if(isPlayer) {
            ++x2; ++z2;
        }

        return location.getX() >= (double)x1 && location.getX() <= (double)x2 && location.getZ() >= (double)z1 && location.getZ() <= (double)z2 && location.getY() >= (double)y1 && location.getY() <= (double)y2;
    }

}
