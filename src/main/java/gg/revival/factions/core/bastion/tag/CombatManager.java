package gg.revival.factions.core.bastion.tag;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import gg.revival.driver.MongoAPI;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.timers.TimerManager;
import gg.revival.factions.timers.TimerType;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager {

    @Getter private FC core;

    public CombatManager(FC core) {
        this.core = core;
    }

    /**
     * Contains all active combat-loggers loaded on the server
     */
    @Getter Map<UUID, CombatLogger> combatLoggers = new HashMap<>();

    /**
     * Returns true if the given UUID has an active combat-logger
     * @param uuid
     * @return
     */
    public boolean hasLogger(UUID uuid)
    {
        return combatLoggers.containsKey(uuid);
    }

    /**
     * Returns a CombatLogger object if the given UUID has one
     * @param uuid
     * @return
     */
    public CombatLogger getLogger(UUID uuid) {
        if(!hasLogger(uuid)) return null;
        return combatLoggers.get(uuid);
    }

    /**
     * Returns a NumberLong of the players remaining combat-tag duration
     * @param uuid
     * @return
     */
    public long getTag(UUID uuid) {
        FPlayer facPlayer = PlayerManager.getPlayer(uuid);

        if(facPlayer == null) return 0L;

        if(facPlayer.isBeingTimed(TimerType.TAG))
            return facPlayer.getTimer(TimerType.TAG).getExpire() - System.currentTimeMillis();

        return 0L;
    }

    /**
     * Performs a combat-tag on the given player. TagReason determines if the player should be tagged for being attacked or for attacking
     * @param player
     * @param reason
     */
    public void tagPlayer(Player player, TagReason reason) {
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer == null) return;

        int duration = 0;

        if(reason.equals(TagReason.ATTACKED))
            duration = core.getConfiguration().tagAttacked;

        if(reason.equals(TagReason.ATTACKER))
            duration = core.getConfiguration().tagAttacker;

        if(facPlayer.isBeingTimed(TimerType.TAG)) {
            int current = (int)((getTag(player.getUniqueId()) - System.currentTimeMillis()) / 1000L);

            if(current >= duration) return;

            long newExpire = System.currentTimeMillis() + (duration * 1000L);

            facPlayer.getTimer(TimerType.TAG).setExpire(newExpire);

            return;
        }

        facPlayer.addTimer(TimerManager.createTimer(TimerType.TAG, duration));

        player.sendMessage(ChatColor.RED + "You are now combat-tagged. You will not be able to enter SafeZone claims until this timer expires");
    }

    /**
     * Creates a combat-logger entry on database for given UUID
     * @param uuid
     */
    public void creatLoggerEntry(UUID uuid) {
        new BukkitRunnable() {
            public void run() {
                if(core.getDatabaseManager().getCombatLoggers() == null)
                    core.getDatabaseManager().setCombatLoggers(MongoAPI.getCollection(core.getConfiguration().databaseName, "combatloggers"));

                MongoCollection<Document> collection = core.getDatabaseManager().getCombatLoggers();
                FindIterable<Document> query;

                try {
                    query = MongoAPI.getQueryByFilter(collection, "uuid", uuid.toString());
                } catch (LinkageError err) {
                    creatLoggerEntry(uuid);
                    return;
                }

                Document document = query.first();

                if(document == null) {
                    Document newDoc = new Document("uuid", uuid.toString());
                    collection.insertOne(newDoc);

                    core.getLog().log("Create combatlogger entry for '" + uuid.toString() + "'");
                }
            }
        }.runTaskAsynchronously(core);
    }

    /**
     * Removes combat-logger entry from database for given UUID
     * @param uuid
     */
    public void clearLoggerEntry(UUID uuid) {
        new BukkitRunnable() {
            public void run() {
                if(core.getDatabaseManager().getCombatLoggers() == null)
                    core.getDatabaseManager().setCombatLoggers(MongoAPI.getCollection(core.getConfiguration().databaseName, "combatloggers"));

                MongoCollection<Document> collection = core.getDatabaseManager().getCombatLoggers();
                FindIterable<Document> query;

                try {
                    query = MongoAPI.getQueryByFilter(collection, "uuid", uuid.toString());
                } catch (LinkageError err) {
                    clearLoggerEntry(uuid);
                    return;
                }

                Document document = query.first();

                if(document != null) {
                    collection.deleteOne(document);

                    core.getLog().log("Deleted combatlogger entry for '" + uuid.toString() + "'");
                }
            }
        }.runTaskAsynchronously(core);
    }

    /**
     * Returns a callback containing if the given UUID is a recent combat logger or not
     * @param uuid
     * @param callback
     */
    public void hasLoggerEntry(UUID uuid, CombatLoggerCallback callback) {
        new BukkitRunnable() {
            public void run() {
                if(core.getDatabaseManager().getCombatLoggers() == null)
                    core.getDatabaseManager().setCombatLoggers(MongoAPI.getCollection(core.getConfiguration().databaseName, "combatloggers"));

                MongoCollection<Document> collection = core.getDatabaseManager().getCombatLoggers();
                FindIterable<Document> query;

                try {
                    query = MongoAPI.getQueryByFilter(collection, "uuid", uuid.toString());
                } catch (LinkageError err) {
                    hasLoggerEntry(uuid, callback);
                    return;
                }

                Document document = query.first();

                new BukkitRunnable() {
                    public void run() {
                        callback.onQueryDone(document != null);
                    }
                }.runTask(core);
            }
        }.runTaskAsynchronously(core);
    }

}
