package gg.revival.factions.core.events.listener;

import gg.revival.factions.core.events.chests.LootTables;
import gg.revival.factions.core.tools.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class LootTablesListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(LootTables.getLootTableCreators().containsKey(player.getUniqueId()))
            LootTables.getLootTableCreators().remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerCloseInventory(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player)) return;

        Player player = (Player)event.getPlayer();

        if(!player.hasPermission(Permissions.CORE_ADMIN)) return;
        if(!LootTables.getLootTableCreators().containsKey(player.getUniqueId())) return;

        String tableName = LootTables.getLootTableCreators().get(player.getUniqueId());

        LootTables.createLootTable(tableName, event.getInventory());

        LootTables.getLootTableCreators().remove(player.getUniqueId());

        player.sendMessage(ChatColor.GREEN + "Loot table '" + tableName + "' updated");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory otherInventory = event.getInventory();

        if(clickedInventory != null && clickedInventory.getName() != null && clickedInventory.getName().contains("Viewing Loot Table: " + ChatColor.DARK_AQUA + "" + ChatColor.BOLD))
            event.setCancelled(true);

        if(otherInventory != null && otherInventory.getName() != null && otherInventory.getName().contains("Viewing Loot Table: " + ChatColor.DARK_AQUA + "" + ChatColor.BOLD))
            event.setCancelled(true);
    }

}
