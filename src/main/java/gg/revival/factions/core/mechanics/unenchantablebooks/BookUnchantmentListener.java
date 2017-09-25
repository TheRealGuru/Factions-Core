package gg.revival.factions.core.mechanics.unenchantablebooks;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BookUnchantmentListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.isCancelled())
            return;

        if(!event.getAction().equals(Action.LEFT_CLICK_BLOCK))
            return;

        if(event.getClickedBlock() == null || !event.getClickedBlock().getType().equals(Material.ENCHANTMENT_TABLE))
            return;

        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInHand();

        if(hand == null || !hand.getType().equals(Material.ENCHANTED_BOOK)) return;

        player.getInventory().setItemInHand(new ItemStack(Material.BOOK));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
    }

}
