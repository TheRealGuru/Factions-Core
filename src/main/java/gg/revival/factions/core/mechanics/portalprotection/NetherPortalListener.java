package gg.revival.factions.core.mechanics.portalprotection;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.tools.BlockTools;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class NetherPortalListener implements Listener {

    @Getter private FC core;

    public NetherPortalListener(FC core) {
        this.core = core;
    }

    @EventHandler
    public void onPortalUse(PlayerTeleportEvent event) {
        if(!core.getConfiguration().protectNetherPortals) return;

        if(!event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) return;

        Player player = event.getPlayer();

        new BukkitRunnable()
        {
            public void run()
            {
                for(Block nearbyBlocks : BlockTools.getNearbyBlocks(player.getLocation(), 4)) {
                    if(nearbyBlocks.getType().equals(Material.PORTAL))
                        player.sendBlockChange(nearbyBlocks.getLocation(), Material.AIR, (byte)0);

                    if(event.getTo().getWorld().getEnvironment().equals(World.Environment.NETHER) &&
                            nearbyBlocks.getWorld().getEnvironment().equals(World.Environment.NETHER)) {

                        if(nearbyBlocks.getLocation().getBlockY() == (player.getLocation().getBlockY() - 2))
                            nearbyBlocks.setType(Material.OBSIDIAN);
                    }
                }
            }
        }.runTask(core);
    }

}
