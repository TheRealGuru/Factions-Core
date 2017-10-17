package gg.revival.factions.core.mechanics.explosiveblocks;

import gg.revival.factions.core.FC;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplosiveBlockListener implements Listener {

    @Getter private FC core;

    public ExplosiveBlockListener(FC core) {
        this.core = core;
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if(!core.getConfiguration().explosiveBlockDamageDisabled) return;

        event.blockList().clear();
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if(!core.getConfiguration().explosiveBlockDamageDisabled) return;

        event.blockList().clear();
    }

}
