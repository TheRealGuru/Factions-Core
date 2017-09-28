package gg.revival.factions.core.events.listener;

import gg.revival.factions.core.events.chests.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventChestListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if(event.isCancelled())
            return;

        if(block.getType() == null || !block.getType().equals(Material.CHEST))
            return;

        if(ChestManager.getEventChestByLocation(block.getLocation()) != null)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Action action = event.getAction();

        if(!action.equals(Action.RIGHT_CLICK_BLOCK))
            return;

        if(block.getType() == null || !block.getType().equals(Material.CHEST))
            return;

        if(ChestManager.getEventChestByLocation(block.getLocation()) == null)
            return;

        EventChest eventChest = ChestManager.getEventChestByLocation(block.getLocation());

        if(eventChest instanceof ClaimChest) {
            ClaimChest claimChest = (ClaimChest)eventChest;

            event.setCancelled(true);

            if(player.getItemInHand() != null && EventKey.isKey(player.getItemInHand())) {
                // TODO: Claim loot here

                return;
            }

            LootTables.viewTable(player, claimChest.getLootTable());

            return;
        }

        if(eventChest instanceof PalaceChest) {
            PalaceChest palaceChest = (PalaceChest)eventChest;

            if(event.isCancelled())
                return;

            palaceChest.setRecentlyLooted(true);
        }
    }

}
