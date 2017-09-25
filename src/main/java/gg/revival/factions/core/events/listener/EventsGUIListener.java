package gg.revival.factions.core.events.listener;

import gg.revival.factions.core.events.obj.EventsGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class EventsGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();

        if(inventory == null || !EventsGUI.isGUI(inventory)) return;

        event.setCancelled(true);
    }

}
