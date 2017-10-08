package gg.revival.factions.core.events.listener;

import gg.revival.factions.core.FC;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class EventsGUIListener implements Listener {

    @Getter private FC core;

    public EventsGUIListener(FC core) {
        this.core = core;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();

        if(inventory == null || !core.getEvents().getEventsGUI().isGUI(inventory)) return;

        event.setCancelled(true);
    }

}
