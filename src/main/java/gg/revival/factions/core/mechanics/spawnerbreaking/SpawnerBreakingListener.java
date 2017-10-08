package gg.revival.factions.core.mechanics.spawnerbreaking;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.tools.Permissions;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class SpawnerBreakingListener implements Listener {

    @Getter private FC core;

    public SpawnerBreakingListener(FC core) {
        this.core = core;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if(!core.getConfiguration().settingsDisableBreakingSpawners)
            return;

        Block block = event.getBlock();

        if(event.isCancelled())
            return;

        if(!block.getType().equals(Material.MOB_SPAWNER)) return;
        if(block.getWorld().getEnvironment().equals(World.Environment.NORMAL)) return;
        if(event.getPlayer().hasPermission(Permissions.CORE_ADMIN)) return;

        event.setCancelled(true);
    }

}
