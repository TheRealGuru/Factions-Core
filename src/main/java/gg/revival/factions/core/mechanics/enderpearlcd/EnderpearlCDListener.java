package gg.revival.factions.core.mechanics.enderpearlcd;

import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.obj.FPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EnderpearlCDListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)) return;

        if(player.getInventory().getItemInHand() == null || !player.getInventory().getItemInHand().getType().equals(Material.ENDER_PEARL)) return;

        if(!EnderpearlCDTask.attemptEnderpearl(player.getUniqueId(), Configuration.enderpearlCooldownsDuration))
            event.setCancelled(true);
    }

}
