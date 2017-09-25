package gg.revival.factions.core.events.listener;

import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.events.loot.EventChest;
import gg.revival.factions.core.events.loot.EventChestManager;
import gg.revival.factions.core.events.loot.EventKey;
import gg.revival.factions.core.events.loot.LootTableManager;
import gg.revival.factions.core.events.messages.EventsMessages;
import gg.revival.factions.obj.PlayerFaction;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EventChestListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if(!action.equals(Action.RIGHT_CLICK_BLOCK) || event.getClickedBlock() == null || event.getClickedBlock().getType().equals(Material.AIR)) return;

        Block clickedBlock = event.getClickedBlock();

        EventChest eventChest = EventChestManager.getEventChestAtLocation(clickedBlock.getLocation());

        if(eventChest == null) return;

        event.setCancelled(true);

        if(player.getItemInHand() != null && EventKey.isKey(player.getItemInHand())) {
            List<ItemStack> receivedLoot = EventChestManager.getRandomItems(eventChest, 3); // TODO: Get this value from config
            ItemStack hand = player.getItemInHand();

            if(hand.getAmount() <= 1)
                player.setItemInHand(null);
            else
                hand.setAmount(hand.getAmount() - 1);

            for(ItemStack loot : receivedLoot) {
                if(player.getInventory().firstEmpty() == -1) {
                    player.getLocation().getWorld().dropItem(player.getLocation(), loot);
                    player.sendMessage(ChatColor.DARK_RED + "Loot has been dropped at your feet because your inventory is full!");
                    continue;
                }

                player.getInventory().addItem(loot);
            }

            if(FactionManager.getFactionByPlayer(player.getUniqueId()) != null) {
                PlayerFaction playerFaction = (PlayerFaction)FactionManager.getFactionByPlayer(player.getUniqueId());

                playerFaction.sendMessage(EventsMessages.asGeneral(EventsMessages.receivedLoot(player.getName(), receivedLoot)));
            } else {
                player.sendMessage(EventsMessages.asGeneral(EventsMessages.receivedLoot(player.getName(), receivedLoot)));
            }

            return;
        }

        LootTableManager.openLootTable(player, LootTableManager.getLootTableByName(eventChest.getLootTable()));
    }

}
