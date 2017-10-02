package gg.revival.factions.core.events.listener;

import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.events.chests.*;
import gg.revival.factions.core.events.engine.PalaceManager;
import gg.revival.factions.core.events.messages.EventsMessages;
import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.core.tools.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

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
                if(player.getItemInHand().getAmount() == 1) {
                    player.setItemInHand(null);
                } else {
                    ItemStack hand = player.getItemInHand();
                    hand.setAmount(hand.getAmount() - 1);
                    player.setItemInHand(hand);
                }

                List<ItemStack> receivedLoot = LootTables.getLoot(LootTables.getLootTableByName(claimChest.getLootTable()), Configuration.pullsPerKey);

                for(ItemStack loot : receivedLoot) {
                    if(player.getInventory().firstEmpty() == -1) {
                        player.getLocation().getWorld().dropItem(player.getLocation(), loot);
                        player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Loot has been dropped at your feet because your inventory is full!");
                        continue;
                    }

                    player.getInventory().addItem(loot);
                }

                Bukkit.broadcastMessage(EventsMessages.asGeneral(EventsMessages.receivedLoot(player.getName(), receivedLoot)));
                return;
            }

            LootTables.viewTable(player, claimChest.getLootTable());
            return;
        }

        if(eventChest instanceof PalaceChest) {
            PalaceChest palaceChest = (PalaceChest)eventChest;

            if(event.isCancelled())
                return;

            if(player.hasPermission(Permissions.CORE_ADMIN))
                return;

            if(!PalaceManager.isCaptured()) {
                player.sendMessage(ChatColor.RED + "This chest belongs to " + ChatColor.BLUE + "Palace");
                event.setCancelled(true);
                return;
            }

            if(PalaceManager.getPalaceSecurityLevel() <= palaceChest.getTier()) return;

            if(FactionManager.getFactionByUUID(PalaceManager.getCapturedFaction()) == null) {
                player.sendMessage(ChatColor.RED + "This chest belongs to " + ChatColor.BLUE + "Palace");
                event.setCancelled(true);
                return;
            }

            if(!PalaceManager.isCapper(player)) {
                if(palaceChest.getTier() > PalaceManager.getPalaceSecurityLevel()) {
                    player.sendMessage(ChatColor.RED + "This chest belongs to " + ChatColor.BLUE + PalaceManager.getCappedFaction().getDisplayName());
                    event.setCancelled(true);
                    return;
                }
            }

            palaceChest.setRecentlyLooted(true);
        }
    }

}
