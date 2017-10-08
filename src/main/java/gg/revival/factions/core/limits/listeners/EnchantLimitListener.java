package gg.revival.factions.core.limits.listeners;

import gg.revival.factions.core.FC;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EnchantLimitListener implements Listener {

    @Getter private FC core;

    public EnchantLimitListener(FC core) {
        this.core = core;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!core.getConfiguration().limitEnchants)
            return;

        if(event.isCancelled())
            return;

        if(event.getEntity() instanceof Player) {
            Player damaged = (Player)event.getEntity();

            for(ItemStack armor : damaged.getInventory().getArmorContents()) {
                if(armor == null || armor.getEnchantments().isEmpty()) continue;

                for(Enchantment foundEnchants : armor.getEnchantments().keySet()) {
                    if(!core.getConfiguration().enchantmentLimits.containsKey(foundEnchants)) continue;

                    int currentLvl = armor.getEnchantmentLevel(foundEnchants);
                    int maxLvl = core.getConfiguration().enchantmentLimits.get(foundEnchants);

                    if(maxLvl == 0) {
                        armor.removeEnchantment(foundEnchants);
                        damaged.sendMessage(ChatColor.RED + "Removed Enchantment" + ChatColor.WHITE + ": " + StringUtils.capitalize(foundEnchants.getName().toLowerCase().replace("_", " ")));
                        continue;
                    }

                    if(currentLvl > maxLvl) {
                        armor.removeEnchantment(foundEnchants);
                        armor.addEnchantment(foundEnchants, maxLvl);
                        damaged.sendMessage(ChatColor.BLUE + "Modified Enchantment" + ChatColor.WHITE + ": " +
                                StringUtils.capitalize(foundEnchants.getName().toLowerCase().replace("_", " ") + " " + currentLvl + " -> " + maxLvl));
                    }
                }
            }
        }

        if(event.getDamager() instanceof Player) {
            Player damager = (Player)event.getDamager();

            if(damager.getItemInHand() != null && !damager.getItemInHand().getEnchantments().isEmpty()) {
                ItemStack hand = damager.getItemInHand();

                for(Enchantment foundEnchants : hand.getEnchantments().keySet()) {
                    if(!core.getConfiguration().enchantmentLimits.containsKey(foundEnchants)) continue;

                    int currentLvl = hand.getEnchantmentLevel(foundEnchants);
                    int maxLvl = core.getConfiguration().enchantmentLimits.get(foundEnchants);

                    if(maxLvl == 0) {
                        hand.removeEnchantment(foundEnchants);
                        damager.sendMessage(ChatColor.RED + "Removed Enchantment" + ChatColor.WHITE + ": " + StringUtils.capitalize(foundEnchants.getName().toLowerCase().replace("_", " ")));
                        continue;
                    }

                    if(currentLvl > maxLvl) {
                        hand.removeEnchantment(foundEnchants);
                        hand.addEnchantment(foundEnchants, maxLvl);
                        damager.sendMessage(ChatColor.BLUE + "Modified Enchantment" + ChatColor.WHITE + ": " +
                                StringUtils.capitalize(foundEnchants.getName().toLowerCase().replace("_", " ") + " " + currentLvl + " -> " + maxLvl));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        if(!core.getConfiguration().limitEnchants)
            return;

        Player player = event.getEnchanter();
        Map<Enchantment, Integer> toAdd = new HashMap<>(event.getEnchantsToAdd());

        for(Enchantment enchantment : toAdd.keySet()) {
            if(!core.getConfiguration().enchantmentLimits.containsKey(enchantment)) continue;

            int currentLvl = toAdd.get(enchantment);
            int maxLvl = core.getConfiguration().enchantmentLimits.get(enchantment);

            if(maxLvl == 0) {
                event.getEnchantsToAdd().remove(enchantment);
                player.sendMessage(ChatColor.RED + "Removed Enchantment" + ChatColor.WHITE + ": " + StringUtils.capitalize(enchantment.getName().toLowerCase().replace("_", " ")));
                continue;
            }

            if(currentLvl > maxLvl) {
                player.sendMessage(ChatColor.BLUE + "Modified Enchantment" + ChatColor.WHITE + ": " +
                StringUtils.capitalize(enchantment.getName().toLowerCase().replace("_", " ") + " " + currentLvl + " -> " + maxLvl));
                event.getEnchantsToAdd().put(enchantment, maxLvl);
                continue;
            }

            event.getEnchantsToAdd().put(enchantment, currentLvl);
        }
    }

}
