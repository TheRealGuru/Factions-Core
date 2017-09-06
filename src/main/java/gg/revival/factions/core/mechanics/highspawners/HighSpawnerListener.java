package gg.revival.factions.core.mechanics.highspawners;

import gg.revival.factions.core.tools.Configuration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class HighSpawnerListener implements Listener
{

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        int yLevel = block.getLocation().getBlockY();

        if(block.getType() == null || !block.getType().equals(Material.MOB_SPAWNER)) return;

        if(yLevel >= Configuration.highSpawnersHeight)
        {
            player.sendMessage(ChatColor.RED + "Mob Spawners will not work at any block above Y: " + Configuration.highSpawnersHeight);
            event.setCancelled(true);
        }
    }

}
