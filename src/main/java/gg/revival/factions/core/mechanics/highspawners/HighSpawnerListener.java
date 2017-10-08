package gg.revival.factions.core.mechanics.highspawners;

import gg.revival.factions.core.FC;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class HighSpawnerListener implements Listener {

    @Getter private FC core;

    public HighSpawnerListener(FC core) {
        this.core = core;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!core.getConfiguration().highSpawnersDisabled) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();
        int yLevel = block.getLocation().getBlockY();

        if(block.getType() == null || !block.getType().equals(Material.MOB_SPAWNER)) return;

        if(yLevel >= core.getConfiguration().highSpawnersHeight) {
            player.sendMessage(ChatColor.RED + "Mob Spawners will not work at any block above Y: " + core.getConfiguration().highSpawnersHeight);
            event.setCancelled(true);
        }
    }

}
