package gg.revival.factions.core.limits.listeners;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.tools.Configuration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PotionLimitListener implements Listener {

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        if(!Configuration.limitPotions)
            return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if(!item.getType().equals(Material.POTION)) return;

        Potion potion = Potion.fromItemStack(item);

        if(Configuration.potionLimits.containsKey(potion.getType().getEffectType())) {
            int lvl = Configuration.potionLimits.get(potion.getType().getEffectType());

            if(potion.getLevel() > lvl) {
                player.sendMessage(ChatColor.RED + "This potion is disabled");

                event.setCancelled(true);
                event.setItem(null);
            }
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        if(!Configuration.limitPotions)
            return;

        ThrownPotion potion = event.getPotion();

        for(PotionEffect effects : potion.getEffects()) {
            if(Configuration.potionLimits.containsKey(effects.getType())) {
                int lvl = Configuration.potionLimits.get(effects.getType());

                if((effects.getAmplifier() + 1) > lvl) {
                    if(event.getPotion().getShooter() instanceof Player) {
                        Player player = (Player)event.getPotion().getShooter();
                        player.sendMessage(ChatColor.RED + "This potion is disabled");
                    }

                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        if(!Configuration.limitPotions)
            return;

        new BukkitRunnable() {
            public void run() {
                List<ItemStack> results = new CopyOnWriteArrayList<>(event.getContents().getContents());

                for(ItemStack contents : results) {
                    if(contents == null) continue;

                    if(!contents.getType().equals(Material.POTION)) continue;

                    Potion potion = Potion.fromItemStack(contents);

                    if(Configuration.potionLimits.containsKey(potion.getType().getEffectType())) {
                        int lvl = Configuration.potionLimits.get(potion.getType().getEffectType());

                        if(potion.getLevel() > lvl) {
                            event.getContents().remove(contents);

                            if(event.getContents().getIngredient() != null) {
                                event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), event.getContents().getIngredient());
                                event.getContents().setIngredient(null);
                            }
                        }
                    }
                }
            }
        }.runTaskLater(FC.getFactionsCore(), 1L);
    }

}
