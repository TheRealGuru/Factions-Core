package gg.revival.factions.core.mechanics.spawnerlimits;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.tools.Permissions;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class SpawnerLimitListener implements Listener {

    @Getter private FC core;

    public SpawnerLimitListener(FC core) {
        this.core = core;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        int yLevel = block.getLocation().getBlockY();

        if(block.getType() == null || !block.getType().equals(Material.MOB_SPAWNER)) return;

        if(player.hasPermission(Permissions.CORE_ADMIN)) return;

        if(!block.getWorld().getEnvironment().equals(World.Environment.NORMAL) && core.getConfiguration().nonOverworldSpawnersDisabled) {
            player.sendMessage(ChatColor.RED + "Mob Spawners will not work in the Nether or End");
            event.setCancelled(true);
            return;
        }

        if(yLevel >= core.getConfiguration().highSpawnersHeight && core.getConfiguration().highSpawnersDisabled) {
            player.sendMessage(ChatColor.RED + "Mob Spawners will not work at any block above Y: " + core.getConfiguration().highSpawnersHeight);
            event.setCancelled(true);
        }
    }

}
