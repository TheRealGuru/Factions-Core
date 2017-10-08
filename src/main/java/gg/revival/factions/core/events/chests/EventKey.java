package gg.revival.factions.core.events.chests;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class EventKey {

    public ItemStack getKeys(int amount) {
        ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta meta = key.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Event Key");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Right-click a loot chest", ChatColor.GRAY + "located in spawn while holding", ChatColor.GRAY + "this item to claim loot!"));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);

        key.setItemMeta(meta);

        key.addUnsafeEnchantment(Enchantment.LUCK, 1);
        key.setAmount(amount);

        return key;
    }

    public boolean isKey(ItemStack item) {
        ItemStack key = getKeys(1);
        return item != null && item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equals(key.getItemMeta().getDisplayName());
    }

}
