package gg.revival.factions.core.mechanics.invalidpearl;

import com.google.common.collect.ImmutableSet;
import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.core.tools.BlockTools;
import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.timers.TimerType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class InvalidPearlListener implements Listener {

    private final ImmutableSet<Material> invalidBlocks = ImmutableSet.of(Material.IRON_TRAPDOOR, Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.DARK_OAK_DOOR, Material.IRON_DOOR, Material.IRON_DOOR_BLOCK,
            Material.JUNGLE_DOOR, Material.SPRUCE_DOOR, Material.TRAP_DOOR, Material.WOOD_DOOR, Material.WOODEN_DOOR, Material.IRON_DOOR, Material.HOPPER, Material.BREWING_STAND, Material.ANVIL,
            Material.BED, Material.FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.JUNGLE_FENCE_GATE,
            Material.SPRUCE_FENCE_GATE, Material.ARMOR_STAND, Material.CHEST, Material.TRAPPED_CHEST, Material.ENCHANTMENT_TABLE,
            Material.BARRIER, Material.THIN_GLASS, Material.STAINED_GLASS_PANE, Material.SLIME_BLOCK);

    public boolean isInvalidBlock(Location location) {
        for(Block nearbyBlocks : BlockTools.getNearbyBlocks(location, 1)) {
            if(invalidBlocks.contains(nearbyBlocks.getType()))
                return true;
        }

        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!Configuration.invalidPearlBlocksEnabled)
            return;

        if(event.isCancelled())
            return;

        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        if(event.getClickedBlock() == null || event.getClickedBlock().getType() == null)
            return;

        if(!isInvalidBlock(event.getClickedBlock().getLocation()))
            return;

        if(event.getPlayer().getItemInHand() == null || !event.getPlayer().getItemInHand().getType().equals(Material.ENDER_PEARL))
            return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTeleport(PlayerTeleportEvent event) {
        if(!Configuration.invalidPearlBlocksEnabled)
            return;

        if(event.isCancelled())
            return;

        if(!event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL))
            return;

        Player player = event.getPlayer();
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());
        Location to = event.getTo();

        if(facPlayer == null) return;

        if(!isInvalidBlock(to.getBlock().getLocation())) return;

        ItemStack item = new ItemStack(Material.ENDER_PEARL);

        player.getInventory().addItem(item);

        if(facPlayer.isBeingTimed(TimerType.ENDERPEARL))
            facPlayer.removeTimer(TimerType.ENDERPEARL);

        player.sendMessage(ChatColor.RED + "Your enderpearl has been cancelled because it landed on an invalid block");

        event.setCancelled(true);
    }

}
