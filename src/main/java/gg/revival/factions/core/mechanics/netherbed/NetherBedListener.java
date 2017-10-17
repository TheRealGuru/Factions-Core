package gg.revival.factions.core.mechanics.netherbed;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.tools.Permissions;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class NetherBedListener implements Listener {

    @Getter private FC core;

    public NetherBedListener(FC core) {
        this.core = core;
    }

    @EventHandler
    public void onPlayerEnterBedEvent(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        Block bed = event.getBed();

        if(!core.getConfiguration().netherBedsDisabled) return;

        if(player.hasPermission(Permissions.CORE_ADMIN)) return;

        if(player.getWorld().getEnvironment().equals(World.Environment.NETHER) || bed.getWorld().getEnvironment().equals(World.Environment.NETHER))
            event.setCancelled(true);
    }
}
