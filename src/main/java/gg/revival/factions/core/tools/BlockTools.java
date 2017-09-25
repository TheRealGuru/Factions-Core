package gg.revival.factions.core.tools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.List;

public class BlockTools {

    /**
     * Returns a list of all nearby blocks
     * @param location Location to check for nearby blocks
     * @param radius Radius that should be checked
     * @return List of nearby blocks
     */
    public static List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<>();

        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }

        return blocks;
    }

    public static Block getTargetBlock(Player player, int dist) {
        BlockIterator iterator = new BlockIterator(player, dist);
        Block lastBlock = iterator.next();

        while (iterator.hasNext()) {
            lastBlock = iterator.next();
            if (lastBlock.getType().equals(Material.AIR)) continue;
            break;
        }

        return lastBlock;
    }

}
