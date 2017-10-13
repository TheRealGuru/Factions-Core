package gg.revival.factions.core.events.task;

import gg.revival.factions.core.FC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitRunnable;

public class GUIUpdaterTask extends BukkitRunnable implements Runnable {

    @Getter private FC core;

    public GUIUpdaterTask(FC core) {
        this.core = core;
    }

    @Override
    public void run() {
        if(core.getEvents().getEventManager() == null || core.getEvents().getEventManager().getActiveEvents().isEmpty()) return;

        for(Player players : Bukkit.getOnlinePlayers()) {
            if(players.getOpenInventory() == null) continue;

            InventoryView inventoryView = players.getOpenInventory();
            Inventory inventory = inventoryView.getTopInventory();

            if(!core.getEvents().getEventsGUI().isGUI(inventory)) continue;

            core.getEvents().getEventsGUI().update(inventory);
        }
    }

}
