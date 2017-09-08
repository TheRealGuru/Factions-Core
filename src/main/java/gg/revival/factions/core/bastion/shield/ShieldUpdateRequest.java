package gg.revival.factions.core.bastion.shield;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
public class ShieldUpdateRequest
{

    @Getter BlockPos position;
    @Getter ShieldPlayer player;
    @Setter boolean completed = false;

    public ShieldUpdateRequest(BlockPos position, ShieldPlayer player)
    {
        this.position = position;
        this.player = player;
    }

    public Player getBukkitPlayer()
    {
        return Bukkit.getPlayer(player.getUuid());
    }

}
