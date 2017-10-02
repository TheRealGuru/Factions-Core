package gg.revival.factions.core.events.engine;

import com.google.common.collect.Lists;
import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.tools.FileManager;
import gg.revival.factions.core.tools.Logger;
import gg.revival.factions.obj.PlayerFaction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class PalaceManager {

    @Getter @Setter static UUID capturedFaction = null;
    @Getter @Setter static boolean captured = false;
    @Getter @Setter static int palaceSecurityLevel = 3;
    @Getter @Setter static int lootRespawnInterval = 3600;
    @Getter @Setter static long nextLootRespawn = System.currentTimeMillis();
    @Getter static List<String> capturedPlayers = Lists.newArrayList();

    public static PlayerFaction getCappedFaction() {
        if(capturedFaction != null && FactionManager.getFactionByUUID(capturedFaction) != null && FactionManager.getFactionByUUID(capturedFaction) instanceof PlayerFaction)
            return (PlayerFaction)FactionManager.getFactionByUUID(capturedFaction);

        return null;
    }

    public static long calculateNextLootRespawn(int seconds) {
        return System.currentTimeMillis() + (seconds * 1000L);
    }

    public static boolean isCapper(Player player) {
        return (getCapturedFaction() != null && getCappedFaction().getRoster(false).contains(player.getUniqueId())) || (capturedPlayers.contains(player.getUniqueId().toString()));
    }

    public static void resetPalace() {
        captured = false;
        capturedFaction = null;
        capturedPlayers = Lists.newArrayList();

        FileManager.getEvents().set("configuration.palace.captured", false);
        FileManager.getEvents().set("configuration.palace.capped-faction", null);
        FileManager.getEvents().set("configuration.palace.capped-players", null);

        FileManager.saveEvents();

        Logger.log(Level.WARNING, "Palace owners have been cleared");
    }

    public static void setCappers(PlayerFaction playerFaction) {
        captured = true;
        capturedFaction = playerFaction.getFactionID();

        List<String> convertedIds = Lists.newArrayList();

        for(UUID cappers : playerFaction.getRoster(false))
            convertedIds.add(cappers.toString());

        capturedPlayers = convertedIds;

        FileManager.getEvents().set("configuration.palace.captured", captured);
        FileManager.getEvents().set("configuration.palace.capped-faction", capturedFaction.toString());
        FileManager.getEvents().set("configuration.palace.capped-players", capturedPlayers);

        FileManager.saveEvents();

        Logger.log("This palace owners have been updated. New owners: " + playerFaction.getDisplayName() + "(" + playerFaction.getFactionID().toString() + ")");
    }

    public static void loadPalace() {
        captured = FileManager.getEvents().getBoolean("configuration.palace.captured");

        if(!captured) return;

        lootRespawnInterval = FileManager.getEvents().getInt("configuration.palace.loot-spawn.interval");
        capturedFaction = UUID.fromString(FileManager.getEvents().getString("configuration.palace.capped-faction"));
        capturedPlayers.addAll(FileManager.getEvents().getStringList("configuration.palace.capped-players"));
    }

}
