package gg.revival.factions.core.mechanics.crowbars;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CrowbarListener implements Listener
{

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if(event.isCancelled()) return;
        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if(event.getClickedBlock() == null || event.getClickedBlock().getType().equals(Material.AIR)) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInHand();

        if(!Crowbar.isCrowbar(item)) return;

        Crowbar.attemptCrowbar(player, item, event.getClickedBlock());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if(event.isCancelled()) return;
        if(!event.getBlock().getType().equals(Material.MOB_SPAWNER)) return;

        Block block = event.getBlock();
        CreatureSpawner creatureSpawner = (CreatureSpawner)block.getState();

        if(event.getItemInHand() != null && event.getItemInHand().getItemMeta() != null)
        {
            ItemMeta meta = event.getItemInHand().getItemMeta();

            if(meta.getDisplayName().contains(ChatColor.DARK_RED + StringUtils.capitalize(creatureSpawner.getSpawnedType().toString().toLowerCase()))) return;

            EntityType entityType = EntityType.valueOf(ChatColor.stripColor(meta.getDisplayName().toUpperCase().replace(" ", "_")));

            if(entityType == null) return;

            creatureSpawner.setSpawnedType(entityType);
            block.getState().update();
        }
    }

}
