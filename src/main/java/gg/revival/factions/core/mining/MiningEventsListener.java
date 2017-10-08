package gg.revival.factions.core.mining;

import gg.revival.factions.core.FC;
import lombok.Getter;
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

public class MiningEventsListener implements Listener {

    @Getter private FC core;

    MiningEventsListener(FC core) {
        this.core = core;
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if(event.isCancelled()) return;

        if(!core.getConfiguration().miningEnabled) return;

        if(event.getBlocks().isEmpty()) return;

        for(Block blocks : event.getBlocks()) {
            if(core.getMining().getPlacedBlocks().contains(blocks.getLocation())) {
                core.getMining().getPlacedBlocks().remove(blocks.getLocation());
                core.getMining().getPlacedBlocks().add(blocks.getRelative(event.getDirection()).getLocation());
                return;
            }
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if(event.isCancelled()) return;

        if(!core.getConfiguration().miningEnabled) return;

        if(event.getBlocks().isEmpty()) return;

        for(Block blocks : event.getBlocks()) {
            if(core.getMining().getPlacedBlocks().contains(blocks.getLocation())) {
                core.getMining().getPlacedBlocks().remove(blocks.getLocation());
                core.getMining().getPlacedBlocks().add(blocks.getRelative(event.getDirection()).getLocation());
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.isCancelled()) return;

        if(!core.getConfiguration().miningEnabled) return;

        Block block = event.getBlock();

        if(!block.getType().equals(Material.STONE) && !block.getType().equals(Material.NETHERRACK)) return;

        if(!core.getMining().getPlacedBlocks().contains(block.getLocation()))
            core.getMining().getPlacedBlocks().add(block.getLocation());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.isCancelled()) return;

        if(!core.getConfiguration().miningEnabled) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if(block.getType().equals(Material.STONE) || block.getType().equals(Material.NETHERRACK)) {
            core.getMining().runLottery(player, block.getLocation());

            if(core.getMining().getPlacedBlocks().contains(block.getLocation()))
                core.getMining().getPlacedBlocks().remove(block.getLocation());
        }
    }

}
