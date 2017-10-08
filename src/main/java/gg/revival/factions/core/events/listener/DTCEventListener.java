package gg.revival.factions.core.events.listener;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.events.obj.DTCEvent;
import gg.revival.factions.obj.Faction;
import gg.revival.factions.obj.PlayerFaction;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class DTCEventListener implements Listener {

    @Getter private FC core;

    public DTCEventListener(FC core) {
        this.core = core;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        DTCEvent dtc = core.getEvents().getDtcManager().getDTCByCore(block.getLocation());
        Faction faction = FactionManager.getFactionByPlayer(player.getUniqueId());

        if(dtc == null) return;
        if(!(faction instanceof PlayerFaction)) return;

        PlayerFaction playerFaction = (PlayerFaction)faction;

        if(dtc.getRecentBreaker() == null) {
            dtc.setRecentBreaker(playerFaction);
        }

        event.setCancelled(true);
    }

}
