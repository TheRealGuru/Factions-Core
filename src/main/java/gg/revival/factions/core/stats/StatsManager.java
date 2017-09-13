package gg.revival.factions.core.stats;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import gg.revival.driver.MongoAPI;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.db.DBManager;
import gg.revival.factions.core.stats.listener.StatsListener;
import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.core.tools.Processor;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StatsManager {

    @Getter static Set<PlayerStats> activeStats = new HashSet<>();

    public static PlayerStats getStats(UUID uuid) {
        for(PlayerStats stats : activeStats) {
            if(!stats.getUuid().equals(uuid)) continue;

            return stats;
        }

        return null;
    }

    public static void loadStats(UUID uuid) {
        if(getStats(uuid) != null) return;

        new BukkitRunnable() {
            public void run() {
                if(DBManager.getStats() == null)
                    DBManager.setStats(MongoAPI.getCollection(Configuration.databaseName, "stats"));

                MongoCollection<Document> collection = DBManager.getStats();
                FindIterable<Document> query = collection.find(Filters.eq("uuid", uuid.toString()));
                Document document = query.first();

                if(document != null) {
                    long playtime = document.getLong("playtime");
                    int foundGold = document.getInteger("foundGold");
                    int foundDiamond = document.getInteger("foundDiamond");
                    int foundEmerald = document.getInteger("foundEmerald");

                    new BukkitRunnable() {
                        public void run() {
                            long loginTime = -1L;

                            if(Bukkit.getPlayer(uuid) != null)
                                loginTime = System.currentTimeMillis();

                            PlayerStats newPlayerStats = new PlayerStats(uuid, playtime, loginTime, foundGold, foundDiamond, foundEmerald);
                            activeStats.add(newPlayerStats);
                        }
                    }.runTask(FC.getFactionsCore());
                }

                else {
                    new BukkitRunnable() {
                        public void run() {
                            long loginTime = -1L;

                            if(Bukkit.getPlayer(uuid) != null)
                                loginTime = System.currentTimeMillis();

                            PlayerStats newPlayerStats = new PlayerStats(uuid, 0, loginTime, 0, 0, 0);
                            activeStats.add(newPlayerStats);
                        }
                    }.runTask(FC.getFactionsCore());
                }
            }
        }.runTaskAsynchronously(FC.getFactionsCore());
    }

    public static void loadAndReceiveStats(UUID uuid, StatsCallback callback) {
        if(getStats(uuid) != null) {
            callback.onQueryDone(getStats(uuid));
            return;
        }

        new BukkitRunnable() {
            public void run() {
                if(DBManager.getStats() == null)
                    DBManager.setStats(MongoAPI.getCollection(Configuration.databaseName, "stats"));

                MongoCollection<Document> collection = DBManager.getStats();
                FindIterable<Document> query = collection.find(Filters.eq("uuid", uuid.toString()));
                Document document = query.first();

                if(document != null) {
                    long playtime = document.getLong("playtime");
                    int foundGold = document.getInteger("foundGold");
                    int foundDiamond = document.getInteger("foundDiamond");
                    int foundEmerald = document.getInteger("foundEmerald");

                    new BukkitRunnable() {
                        public void run() {
                            long loginTime = -1L;

                            if(Bukkit.getPlayer(uuid) != null)
                                loginTime = System.currentTimeMillis();

                            PlayerStats newPlayerStats = new PlayerStats(uuid, playtime, loginTime, foundGold, foundDiamond, foundEmerald);
                            activeStats.add(newPlayerStats);
                            callback.onQueryDone(newPlayerStats);
                        }
                    }.runTask(FC.getFactionsCore());
                }

                else {
                    new BukkitRunnable() {
                        public void run() {
                            long loginTime = -1L;

                            if(Bukkit.getPlayer(uuid) != null)
                                loginTime = System.currentTimeMillis();

                            PlayerStats newPlayerStats = new PlayerStats(uuid, 0, loginTime, 0, 0, 0);
                            activeStats.add(newPlayerStats);
                            callback.onQueryDone(newPlayerStats);
                        }
                    }.runTask(FC.getFactionsCore());
                }
            }
        }.runTaskAsynchronously(FC.getFactionsCore());
    }

    public static void saveStats(PlayerStats stats, boolean unsafe) {
        if(unsafe) {
            Runnable saveTask = () -> {
                if(DBManager.getStats() == null)
                    DBManager.setStats(MongoAPI.getCollection(Configuration.databaseName, "stats"));

                MongoCollection<Document> collection = DBManager.getStats();
                FindIterable<Document> query = collection.find(Filters.eq("uuid", stats.getUuid().toString()));
                Document document = query.first();

                Document newDoc = new Document("uuid", stats.getUuid().toString())
                        .append("playtine", stats.getPlaytime())
                        .append("foundGold", stats.getFoundGold())
                        .append("foundDiamond", stats.getFoundDiamonds())
                        .append("foundEmerald", stats.getFoundEmeralds());

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
                if(DBManager.getStats() == null)
                    DBManager.setStats(MongoAPI.getCollection(Configuration.databaseName, "stats"));

                MongoCollection<Document> collection = DBManager.getStats();
                FindIterable<Document> query = collection.find(Filters.eq("uuid", stats.getUuid().toString()));
                Document document = query.first();

                Document newDoc = new Document("uuid", stats.getUuid().toString())
                        .append("playtine", stats.getPlaytime())
                        .append("foundGold", stats.getFoundGold())
                        .append("foundDiamond", stats.getFoundDiamonds())
                        .append("foundEmerald", stats.getFoundEmeralds());

                if(document != null)
                    collection.replaceOne(document, newDoc);
                else
                    collection.insertOne(newDoc);
            }
        }.runTaskAsynchronously(FC.getFactionsCore());
    }

    public static void onEnable() {
        loadListeners();
        loadCommands();
    }

    public static void onDisable() {
        for(PlayerStats loaded : activeStats) {
            saveStats(loaded, true);
        }
    }

    private static void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new StatsListener(), FC.getFactionsCore());
    }

    private static void loadCommands() {

    }

}
