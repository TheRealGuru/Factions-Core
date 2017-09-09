package gg.revival.factions.core.events.loot;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EventKey {

    public static ItemStack getKey(int amount) {
        ItemStack item = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.DARK_RED + "Event Key");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.AQUA + "Right-click one of the three" + "\n" + ChatColor.AQUA + "loot chests in spawn to claim");
        meta.setLore(lore);

        item.setItemMeta(meta);
        item.setAmount(amount);

        return item;
    }

    public static boolean isKey(ItemStack item) {
        return
                item != null &&
                item.getType().equals(Material.TRIPWIRE_HOOK) &&
                item.getItemMeta() != null &&
                item.getItemMeta().getDisplayName() != null &&
                item.getItemMeta().getDisplayName().equals(getKey(1).getItemMeta().getDisplayName());
    }

}
