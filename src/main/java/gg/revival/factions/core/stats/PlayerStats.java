package gg.revival.factions.core.stats;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class PlayerStats {

    @Getter UUID uuid;
    @Getter @Setter long playtime, loginTime;
    @Getter @Setter int foundGold, foundDiamonds, foundEmeralds;

    public PlayerStats(UUID uuid, long playtime, long loginTime, int foundGold, int foundDiamonds, int foundEmeralds) {
        this.uuid = uuid;
        this.playtime = playtime;
        this.loginTime = loginTime;
        this.foundGold = foundGold;
        this.foundDiamonds = foundDiamonds;
        this.foundEmeralds = foundEmeralds;
    }

    public long getNewPlaytime() {
        return playtime + (System.currentTimeMillis() - loginTime);
    }

    public void addGold(int amount) {
        setFoundGold(foundGold + amount);
    }

    public void addDiamond(int amount) {
        setFoundDiamonds(foundDiamonds + amount);
    }

    public void addEmerald(int amount) {
        setFoundEmeralds(foundEmeralds + amount);
    }

}
