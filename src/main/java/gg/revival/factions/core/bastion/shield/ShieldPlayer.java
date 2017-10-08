package gg.revival.factions.core.bastion.shield;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

class ShieldPlayer {

    @Getter UUID uuid;
    @Getter @Setter Collection<BlockPos> lastShownBlocks;

    ShieldPlayer(UUID uuid) {
        this.uuid = uuid;
        this.lastShownBlocks = new CopyOnWriteArrayList<>();
    }

}
