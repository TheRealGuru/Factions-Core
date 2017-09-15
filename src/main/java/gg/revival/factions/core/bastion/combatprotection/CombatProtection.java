package gg.revival.factions.core.bastion.combatprotection;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import gg.revival.driver.MongoAPI;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.core.db.DBManager;
import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.core.tools.Processor;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.timers.TimerManager;
import gg.revival.factions.timers.TimerType;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class CombatProtection {

    public static void loadProtection(UUID uuid, ProtectionLookupCallback callback) {
        new BukkitRunnable() {
            public void run() {
                if(DBManager.getProtection() == null)
                    DBManager.setProtection(MongoAPI.getCollection(Configuration.databaseName, "protection"));

                MongoCollection collection = DBManager.getProtection();
                FindIterable<Document> query = null;

                try {
                    query = MongoAPI.getQueryByFilter(collection, "uuid", uuid.toString());
                } catch (LinkageError err) {
                    loadProtection(uuid, callback);
                    return;
                }

                Document document = query.first();

                if(document != null) {
                    if(document.getInteger("protection") > 0) {
                        int duration = document.getInteger("protection");

                        new BukkitRunnable() {
                            public void run() {
                                callback.onLookupComplete(duration);
                            }
                        }.runTask(FC.getFactionsCore());

                        return;
                    }
                }

                new BukkitRunnable() {
                    public void run() {
                        callback.onLookupComplete(0);
                    }
                }.runTask(FC.getFactionsCore());
            }
        }.runTaskAsynchronously(FC.getFactionsCore());
    }

    public static void saveProtection(UUID uuid, int remainingProtection, boolean unsafe) {
        if(unsafe) {
            Runnable saveTask = () -> {
                if(DBManager.getProtection() == null)
                    DBManager.setProtection(MongoAPI.getCollection(Configuration.databaseName, "protection"));

                MongoCollection collection = DBManager.getProtection();
                FindIterable<Document> query = null;

                try {
                    query = MongoAPI.getQueryByFilter(collection, "uuid", uuid.toString());
                } catch (LinkageError err) {
                    saveProtection(uuid, remainingProtection, unsafe);
                    return;
                }

                Document document = query.first();

                Document newDoc = new Document("uuid", uuid.toString()).append("protection", remainingProtection);

                if(document != null)
                    collection.replaceOne(document, newDoc);
                else
                    collection.insertOne(newDoc);
            };

            Processor.getExecutor().submit(saveTask);
            return;
        }

        new BukkitRunnable() {
            public void run() {
                if(DBManager.getProtection() == null)
                    DBManager.setProtection(MongoAPI.getCollection(Configuration.databaseName, "protection"));

                MongoCollection collection = DBManager.getProtection();
                FindIterable<Document> query = null;

                try {
                    query = MongoAPI.getQueryByFilter(collection, "uuid", uuid.toString());
                } catch (LinkageError err) {
                    saveProtection(uuid, remainingProtection, unsafe);
                    return;
                }

                Document document = query.first();
                Document newDoc = new Document("uuid", uuid.toString()).append("protection", remainingProtection);

                if(document != null)
                    collection.replaceOne(document, newDoc);
                else
                    collection.insertOne(newDoc);
            }
        }.runTaskAsynchronously(FC.getFactionsCore());
    }

    public static void deleteProtection(UUID uuid) {
        new BukkitRunnable() {
            public void run() {
                if(DBManager.getProtection() == null)
                    DBManager.setProtection(MongoAPI.getCollection(Configuration.databaseName, "protection"));

                MongoCollection collection = DBManager.getProtection();
                FindIterable<Document> query = null;

                try {
                    query = MongoAPI.getQueryByFilter(collection, "uuid", uuid.toString());
                } catch (LinkageError err) {
                    deleteProtection(uuid);
                    return;
                }

                Document document = query.first();

                if(document != null)
                    collection.deleteOne(document);
            }
        }.runTaskAsynchronously(FC.getFactionsCore());
    }

    public static boolean hasProt(Player player) {
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());
        return facPlayer.isBeingTimed(TimerType.PVPPROT);
    }

    public static boolean hasSafety(Player player) {
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());
        return facPlayer.isBeingTimed(TimerType.SAFETY);
    }

    public static void takeProtection(Player player) {
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer.isBeingTimed(TimerType.PVPPROT))
            facPlayer.removeTimer(TimerType.PVPPROT);

        player.sendMessage(ChatColor.RED + "Your PvP protection has been removed");

        deleteProtection(player.getUniqueId());
    }

    public static void takeSafety(Player player) {
        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer.isBeingTimed(TimerType.SAFETY))
            facPlayer.removeTimer(TimerType.SAFETY);

        player.sendMessage(ChatColor.RED + "Your PvP safety has been removed");
    }

    public static void giveProtection(Player player, int duration) {
        if(!Configuration.pvpProtEnabled) return;

        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer.isBeingTimed(TimerType.PVPPROT))
            facPlayer.getTimer(TimerType.PVPPROT).setExpire(System.currentTimeMillis() + (duration * 1000L));
        else
            facPlayer.addTimer(TimerManager.createTimer(TimerType.PVPPROT, duration));
    }

    public static void giveSafety(Player player, int duration) {
        if(!Configuration.pvpSafetyEnabled) return;

        FPlayer facPlayer = PlayerManager.getPlayer(player.getUniqueId());

        if(facPlayer.isBeingTimed(TimerType.SAFETY))
            facPlayer.getTimer(TimerType.SAFETY).setExpire(System.currentTimeMillis() + (duration * 1000L));
        else
            facPlayer.addTimer(TimerManager.createTimer(TimerType.SAFETY, duration));
    }

}
