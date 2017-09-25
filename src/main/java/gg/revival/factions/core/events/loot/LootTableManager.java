package gg.revival.factions.core.events.loot;

import com.google.common.collect.Maps;
import gg.revival.factions.core.tools.FileManager;
import gg.revival.factions.core.tools.InvTools;
import gg.revival.factions.core.tools.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class LootTableManager {

    @Getter static Map<String, Inventory> lootTables = Maps.newHashMap();
    @Getter static Map<UUID, String> lootTableBuilders = Maps.newHashMap();

    public static String getBuildingLootTable(UUID uuid) {
        if(lootTableBuilders.containsKey(uuid))
            return lootTableBuilders.get(uuid);

        return null;
    }

    public static Inventory getLootTableByName(String name) {
        for(String tables : lootTables.keySet()) {
            if(tables.equalsIgnoreCase(name))
                return lootTables.get(tables);
        }

        return null;
    }

    public static void deleteLootTable(String name) {
        FileManager.getEvents().set("loot-tables." + name, null);
        FileManager.saveEvents();

        Logger.log("Loot table '" + name + "' has been deleted");
    }

    public static void saveLootTable(String name, Inventory inventory) {
        FileManager.getEvents().set("loot-tables." + name, InvTools.toBase64(inventory));
        FileManager.saveEvents();

        lootTables.put(name, inventory);

        Logger.log("Saved loot table '" + name + "'");
    }

    public static void loadLootTables() {
        if(FileManager.getEvents().get("loot-tables") == null) {
            Logger.log(Level.WARNING, "Couldn't find any configured loot tables");
            return;
        }

        for(String keys : FileManager.getEvents().getConfigurationSection("loot-tables").getKeys(false)) {
            try {
                Inventory lootTableInventory = InvTools.inventoryFromBase64(FileManager.getEvents().getString("loot-tables." + keys));

                lootTables.put(keys, lootTableInventory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Logger.log("Loaded " + lootTables.size() + " Loot tables");
    }

    public static void openLootTableEditor(Player openedTo, String tableName, Inventory previousInventory) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.BLACK + "Loot Table Editor");

        if(previousInventory != null && previousInventory.getContents().length > 0) {
            int cursor = 0;

            for(ItemStack contents : previousInventory.getContents()) {
                gui.setItem(cursor, contents);
                cursor++;
            }
        }

        openedTo.openInventory(gui);

        lootTableBuilders.put(openedTo.getUniqueId(), tableName);
    }

    public static void openLootTable(Player openedTo, Inventory lootTable) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.BLACK + "Loot Table");

        int cursor = 0;

        for(ItemStack contents : lootTable.getContents()) {
            gui.setItem(cursor, contents);
            cursor++;
        }

        openedTo.openInventory(gui);
    }

}
