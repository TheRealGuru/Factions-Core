package gg.revival.factions.core.events.chests;

import com.google.common.collect.Maps;
import gg.revival.factions.core.tools.FileManager;
import gg.revival.factions.core.tools.InvTools;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class LootTables {

    @Getter static Map<String, Inventory> lootTables = Maps.newHashMap();
    @Getter static Map<UUID, String> lootTableCreators = Maps.newHashMap();

    public static Inventory getLootTableByName(String name) {
        for(String tableNames : lootTables.keySet())
            if(tableNames.equalsIgnoreCase(name)) return lootTables.get(tableNames);

        return null;
    }

    public static void createLootTable(String name, Inventory inventory) {
        String asString = InvTools.toBase64(inventory);

        FileManager.getEvents().set("loot-tables." + name + ".contents", asString);
        FileManager.saveEvents();

        lootTables.put(name, inventory);
    }

    public static void deleteLootTable(String name) {
        FileManager.getEvents().set("loot-tables." + name, null);
        FileManager.saveEvents();

        lootTables.remove(name);
    }

    public static void viewTable(Player player, String tableName) {
        Inventory gui = Bukkit.createInventory(null, 27, "Viewing Loot Table: " + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + tableName);

        if(getLootTableByName(tableName) != null) {
            for(ItemStack contents : getLootTableByName(tableName).getContents()) {
                if(contents == null || contents.getType().equals(Material.AIR)) continue;
                gui.addItem(contents);
            }
        }

        player.openInventory(gui);
    }

    public static void openEditor(Player player, String tableName) {
        lootTableCreators.put(player.getUniqueId(), tableName);

        Inventory gui = Bukkit.createInventory(null, 27, "Creating Loot Table: " + ChatColor.DARK_RED + "" + ChatColor.BOLD + tableName);

        if(getLootTableByName(tableName) != null) {
            for(ItemStack contents : getLootTableByName(tableName).getContents()) {
                if(contents == null || contents.getType().equals(Material.AIR)) continue;
                gui.addItem(contents);
            }
        }

        player.openInventory(gui);
        player.sendMessage(ChatColor.GREEN + "Now editing Loot Table '" + tableName + "'");
    }

}
