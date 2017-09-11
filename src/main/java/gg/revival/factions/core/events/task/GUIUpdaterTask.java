package gg.revival.factions.core.events.task;

import gg.revival.factions.core.events.engine.EventManager;
import gg.revival.factions.core.events.obj.EventsGUI;
import gg.revival.factions.core.tools.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitRunnable;

public class GUIUpdaterTask extends BukkitRunnable implements Runnable {

    @Override
    public void run() {
        if(EventManager.getActiveEvents().isEmpty()) return;

        for(Player players : Bukkit.getOnlinePlayers()) {
            if(players.getOpenInventory() == null) continue;

            InventoryView inventoryView = players.getOpenInventory();
            Inventory inventory = inventoryView.getTopInventory();

            if(!EventsGUI.isGUI(inventory)) return;

            EventsGUI.update(inventory);
        }
    }

}
