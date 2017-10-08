package gg.revival.factions.core.events.engine;

import com.google.common.collect.Lists;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.FactionManager;
import gg.revival.factions.obj.PlayerFaction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class PalaceManager {

    @Getter private FC core;
    @Getter @Setter UUID capturedFaction = null;
    @Getter @Setter boolean captured = false;
    @Getter @Setter int palaceSecurityLevel = 3;
    @Getter @Setter int lootRespawnInterval = 3600;
    @Getter @Setter long nextLootRespawn = System.currentTimeMillis();
    @Getter List<String> capturedPlayers = Lists.newArrayList();

    public PalaceManager(FC core) {
        this.core = core;
    }

    public PlayerFaction getCappedFaction() {
        if(capturedFaction != null && FactionManager.getFactionByUUID(capturedFaction) != null && FactionManager.getFactionByUUID(capturedFaction) instanceof PlayerFaction)
            return (PlayerFaction)FactionManager.getFactionByUUID(capturedFaction);

        return null;
    }

    public long calculateNextLootRespawn(int seconds) {
        return System.currentTimeMillis() + (seconds * 1000L);
    }

    public boolean isCapper(Player player) {
        return (getCapturedFaction() != null && getCappedFaction().getRoster(false).contains(player.getUniqueId())) || (capturedPlayers.contains(player.getUniqueId().toString()));
    }

    public void resetPalace() {
        captured = false;
        capturedFaction = null;
        capturedPlayers = Lists.newArrayList();

        core.getFileManager().getEvents().set("configuration.palace.captured", false);
        core.getFileManager().getEvents().set("configuration.palace.capped-faction", null);
        core.getFileManager().getEvents().set("configuration.palace.capped-players", null);

        core.getFileManager().saveEvents();

        core.getLog().log(Level.WARNING, "Palace owners have been cleared");
    }

    public void setCappers(PlayerFaction playerFaction) {
        captured = true;
        capturedFaction = playerFaction.getFactionID();

        List<String> convertedIds = Lists.newArrayList();

        for(UUID cappers : playerFaction.getRoster(false))
            convertedIds.add(cappers.toString());

        capturedPlayers = convertedIds;

        core.getFileManager().getEvents().set("configuration.palace.captured", captured);
        core.getFileManager().getEvents().set("configuration.palace.capped-faction", capturedFaction.toString());
        core.getFileManager().getEvents().set("configuration.palace.capped-players", capturedPlayers);

        core.getFileManager().saveEvents();

        core.getLog().log("This palace owners have been updated. New owners: " + playerFaction.getDisplayName() + "(" + playerFaction.getFactionID().toString() + ")");
    }

    public void loadPalace() {
        captured = core.getFileManager().getEvents().getBoolean("configuration.palace.captured");

        if(!captured) return;

        lootRespawnInterval = core.getFileManager().getEvents().getInt("configuration.palace.loot-spawn.interval");
        capturedFaction = UUID.fromString(core.getFileManager().getEvents().getString("configuration.palace.capped-faction"));
        capturedPlayers.addAll(core.getFileManager().getEvents().getStringList("configuration.palace.capped-players"));
    }

}
