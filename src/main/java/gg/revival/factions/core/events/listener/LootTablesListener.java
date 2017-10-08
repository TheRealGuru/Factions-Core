package gg.revival.factions.core.events.listener;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.tools.Permissions;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class LootTablesListener implements Listener {

    @Getter private FC core;

    public LootTablesListener(FC core) {
        this.core = core;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(core.getEvents().getLootTables().getLootTableCreators().containsKey(player.getUniqueId()))
            core.getEvents().getLootTables().getLootTableCreators().remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerCloseInventory(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player)) return;

        Player player = (Player)event.getPlayer();

        if(!player.hasPermission(Permissions.CORE_ADMIN)) return;
        if(!core.getEvents().getLootTables().getLootTableCreators().containsKey(player.getUniqueId())) return;

        String tableName = core.getEvents().getLootTables().getLootTableCreators().get(player.getUniqueId());

        core.getEvents().getLootTables().createLootTable(tableName, event.getInventory());

        core.getEvents().getLootTables().getLootTableCreators().remove(player.getUniqueId());

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
