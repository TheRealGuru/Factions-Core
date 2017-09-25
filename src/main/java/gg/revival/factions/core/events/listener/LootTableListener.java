package gg.revival.factions.core.events.listener;

import gg.revival.factions.core.events.loot.LootTableManager;
import gg.revival.factions.core.tools.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class LootTableListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(LootTableManager.getBuildingLootTable(player.getUniqueId()) != null)
            LootTableManager.getLootTableBuilders().remove(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player)) return;

        Player player = (Player)event.getPlayer();
        Inventory inventory = event.getInventory();

        if(LootTableManager.getBuildingLootTable(player.getUniqueId()) != null && player.hasPermission(Permissions.CORE_ADMIN)) {
            LootTableManager.saveLootTable(LootTableManager.getBuildingLootTable(player.getUniqueId()), inventory);

            player.sendMessage(ChatColor.GREEN + "Loot Table Updated");

            LootTableManager.getLootTableBuilders().remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;

        Inventory inventory = event.getInventory();
        Inventory clickedInventory = event.getClickedInventory();

        if(inventory.getName() != null && inventory.getName().equals(ChatColor.BLACK + "Loot Table")) {
            event.setResult(Event.Result.DENY);
            event.setCancelled(true);
        }

        if(clickedInventory.getName() != null && clickedInventory.getName().equals(ChatColor.BLACK + "Loot Table")) {
            event.setResult(Event.Result.DENY);
            event.setCancelled(true);
        }
    }

}
