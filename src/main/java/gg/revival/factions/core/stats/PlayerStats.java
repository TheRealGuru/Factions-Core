package gg.revival.factions.core.stats;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class PlayerStats {

    @Getter UUID uuid;
    @Getter @Setter long playtime, loginTime;
    @Getter @Setter int kills, deaths, foundGold, foundDiamonds, foundEmeralds;

    public PlayerStats(UUID uuid, long playtime, long loginTime, int kills, int deaths, int foundGold, int foundDiamonds, int foundEmeralds) {
        this.uuid = uuid;
        this.playtime = playtime;
        this.loginTime = loginTime;
        this.kills = kills;
        this.deaths = deaths;
        this.foundGold = foundGold;
        this.foundDiamonds = foundDiamonds;
        this.foundEmeralds = foundEmeralds;
    }

    public long getCurrentPlaytime() {
        if(loginTime == -1L)
            this.loginTime = System.currentTimeMillis();

        return playtime + (System.currentTimeMillis() - loginTime);
    }

    public void addKill() {
        setKills(kills + 1);
    }

    public void addDeath() {
        setDeaths(deaths + 1);
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
