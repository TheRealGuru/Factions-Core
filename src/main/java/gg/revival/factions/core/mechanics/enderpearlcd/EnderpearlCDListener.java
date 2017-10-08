package gg.revival.factions.core.mechanics.enderpearlcd;

import gg.revival.factions.core.FC;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EnderpearlCDListener implements Listener {

    @Getter private FC core;

    public EnderpearlCDListener(FC core) {
        this.core = core;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(!core.getConfiguration().enderpearlCooldownsEnabled) return;
        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)) return;

        if(player.getInventory().getItemInHand() == null || !player.getInventory().getItemInHand().getType().equals(Material.ENDER_PEARL)) return;

        if(!core.getMechanics().getEnderpearlTask().attemptEnderpearl(player.getUniqueId(), core.getConfiguration().enderpearlCooldownsDuration))
            event.setCancelled(true);
    }

}
