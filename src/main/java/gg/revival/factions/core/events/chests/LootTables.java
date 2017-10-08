package gg.revival.factions.core.events.chests;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.tools.InvTools;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class LootTables {

    @Getter private FC core;
    @Getter Map<String, Inventory> lootTables = Maps.newHashMap();
    @Getter Map<UUID, String> lootTableCreators = Maps.newHashMap();

    public LootTables(FC core) {
        this.core = core;
    }

    public Inventory getLootTableByName(String name) {
        for(String tableNames : lootTables.keySet())
            if(tableNames.equalsIgnoreCase(name)) return lootTables.get(tableNames);

        return null;
    }

    public void createLootTable(String name, Inventory inventory) {
        String asString = InvTools.toBase64(inventory);

        core.getFileManager().getEvents().set("loot-tables." + name + ".contents", asString);
        core.getFileManager().saveEvents();

        lootTables.put(name, inventory);
    }

    public void deleteLootTable(String name) {
        core.getFileManager().getEvents().set("loot-tables." + name, null);
        core.getFileManager().saveEvents();

        lootTables.remove(name);
    }

    public List<ItemStack> getLoot(Inventory inventory, int pulls) {
        List<ItemStack> result = Lists.newArrayList();
        List<ItemStack> contents = Lists.newArrayList();
        Random random = new Random();

        for(ItemStack inventoryContents : inventory.getContents()) {
            if(inventoryContents == null || inventoryContents.getType().equals(Material.AIR)) continue;
            contents.add(inventoryContents);
        }

        for(int i = 0; i < pulls; i++)
            result.add(contents.get(random.nextInt(contents.size())));

        return result;
    }

    public void viewTable(Player player, String tableName) {
        Inventory gui = Bukkit.createInventory(null, 27, "Viewing Loot Table: " + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + tableName);

        if(getLootTableByName(tableName) != null) {
            for(ItemStack contents : getLootTableByName(tableName).getContents()) {
                if(contents == null || contents.getType().equals(Material.AIR)) continue;
                gui.addItem(contents);
            }
        }

        player.openInventory(gui);
    }

    public void openEditor(Player player, String tableName) {
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
