package gg.revival.factions.core.mining;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class MiningEventsListener implements Listener
{

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event)
    {
        if(event.isCancelled()) return;

        if(event.getBlocks().isEmpty()) return;

        for(Block blocks : event.getBlocks())
        {
            if(Mining.getPlacedStone().contains(blocks.getLocation()))
            {
                Mining.getPlacedStone().remove(blocks.getLocation());
                Mining.getPlacedStone().add(blocks.getRelative(event.getDirection()).getLocation());
                return;
            }
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event)
    {
        if(event.isCancelled()) return;

        if(event.getBlocks().isEmpty()) return;

        for(Block blocks : event.getBlocks())
        {
            if(Mining.getPlacedStone().contains(blocks.getLocation()))
            {
                Mining.getPlacedStone().remove(blocks.getLocation());
                Mining.getPlacedStone().add(blocks.getRelative(event.getDirection()).getLocation());
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if(event.isCancelled()) return;

        Block block = event.getBlock();

        if(!block.getType().equals(Material.STONE)) return;

        if(!Mining.getPlacedStone().contains(block.getLocation()))
            Mining.getPlacedStone().add(block.getLocation());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event)
    {
        if(event.isCancelled()) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if(block.getType().equals(Material.STONE))
        {
            Mining.runLottery(player, block.getLocation());

            if(Mining.getPlacedStone().contains(block.getLocation()))
                Mining.getPlacedStone().remove(block.getLocation());
        }
    }

}
