package gg.revival.factions.core.stats;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import gg.revival.driver.MongoAPI;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.db.DBManager;
import gg.revival.factions.core.stats.command.StatsCommand;
import gg.revival.factions.core.stats.listener.StatsListener;
import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.core.tools.Processor;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Stats {

    @Getter static Set<PlayerStats> activeStats = new HashSet<>();

    public static PlayerStats getStats(UUID uuid) {
        for(PlayerStats stats : activeStats) {
            if(!stats.getUuid().equals(uuid)) continue;

            if(Bukkit.getPlayer(uuid) != null)
                stats.setPlaytime(stats.getNewPlaytime());

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

                MongoCollection collection = DBManager.getStats();
                FindIterable<Document> query = null;

                try {
                    query = MongoAPI.getQueryByFilter(collection, "uuid", uuid.toString());
                } catch (LinkageError err) {
                    loadStats(uuid);
                    return;
                }

                Document document = query.first();

                if(document != null) {
                    long playtime = document.getLong("playtime");
                    int kills = document.getInteger("kills");
                    int deaths = document.getInteger("deaths");
                    int foundGold = document.getInteger("foundGold");
                    int foundDiamond = document.getInteger("foundDiamond");
                    int foundEmerald = document.getInteger("foundEmerald");

                    new BukkitRunnable() {
                        public void run() {
                            long loginTime = -1L;

                            if(Bukkit.getPlayer(uuid) != null)
                                loginTime = System.currentTimeMillis();

                            PlayerStats newPlayerStats = new PlayerStats(uuid, playtime, loginTime, kills, deaths, foundGold, foundDiamond, foundEmerald);
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

                            PlayerStats newPlayerStats = new PlayerStats(uuid, 0, loginTime, 0, 0, 0, 0, 0);
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

                MongoCollection collection = DBManager.getStats();
                FindIterable<Document> query = null;

                try {
                    query = MongoAPI.getQueryByFilter(collection, "uuid", uuid.toString());
                } catch (LinkageError err) {
                    loadAndReceiveStats(uuid, callback);
                    return;
                }

                Document document = query.first();

                if(document != null) {
                    long playtime = document.getLong("playtime");
                    int kills = document.getInteger("kills");
                    int deaths = document.getInteger("deaths");
                    int foundGold = document.getInteger("foundGold");
                    int foundDiamond = document.getInteger("foundDiamond");
                    int foundEmerald = document.getInteger("foundEmerald");

                    new BukkitRunnable() {
                        public void run() {
                            long loginTime = -1L;

                            if(Bukkit.getPlayer(uuid) != null)
                                loginTime = System.currentTimeMillis();

                            PlayerStats newPlayerStats = new PlayerStats(uuid, playtime, loginTime, kills, deaths, foundGold, foundDiamond, foundEmerald);
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

                            PlayerStats newPlayerStats = new PlayerStats(uuid, 0, loginTime, 0, 0, 0, 0, 0);
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

                MongoCollection collection = DBManager.getStats();
                FindIterable<Document> query = null;

                try {
                    query = MongoAPI.getQueryByFilter(collection, "uuid", stats.getUuid().toString());
                } catch (LinkageError err) {
                    saveStats(stats, unsafe);
                    return;
                }

                Document document = query.first();

                Document newDoc = new Document("uuid", stats.getUuid().toString())
                        .append("playtime", stats.getPlaytime())
                        .append("kills", stats.getKills())
                        .append("deaths", stats.getDeaths())
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

                MongoCollection collection = DBManager.getStats();
                FindIterable<Document> query = null;

                try {
                    query = MongoAPI.getQueryByFilter(collection, "uuid", stats.getUuid().toString());
                } catch (LinkageError err) {
                    saveStats(stats, unsafe);
                    return;
                }

                Document document = query.first();

                Document newDoc = new Document("uuid", stats.getUuid().toString())
                        .append("playtime", stats.getPlaytime())
                        .append("kills", stats.getKills())
                        .append("deaths", stats.getDeaths())
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

    public static String getFormattedStats(PlayerStats stats, String statsUsername) {
        StringBuilder message = new StringBuilder();

        message.append(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------" + "\n");
        message.append(ChatColor.RESET + "Displaying statistics for " + ChatColor.AQUA + statsUsername + "\n");
        message.append("     " + "\n");
        message.append(ChatColor.GREEN + "Kills" + ChatColor.WHITE + ": " + stats.getKills() + "\n");
        message.append(ChatColor.RED + "Deaths" + ChatColor.WHITE + ": " + stats.getDeaths() + "\n");
        message.append("     " + "\n");

        int seconds = (int) (stats.getPlaytime() / 1000) % 60;
        int minutes = (int) (stats.getPlaytime() / (1000 * 60) % 60);
        int hours   = (int) (stats.getPlaytime() / (1000 * 60 * 60) % 24);

        if(seconds >= 60)
            seconds = 59;

        StringBuilder time = new StringBuilder();

        if(hours > 0)
            time.append(hours + " hours ");

        if(minutes > 0)
            time.append(minutes + " minutes ");

        if(seconds > 0) {
            if(time.toString().length() > 0)
                time.append("and " + seconds + " seconds ");
            else
                time.append(seconds + " seconds ");
        }

        message.append(ChatColor.LIGHT_PURPLE + "Playtime" + ChatColor.WHITE + ": " + time.toString().trim() + "\n");
        message.append("     " + "\n");
        message.append(ChatColor.YELLOW + "Found " + ChatColor.GOLD + "Gold" + ChatColor.WHITE + ": " + stats.getFoundGold() + "\n");
        message.append(ChatColor.YELLOW + "Found " + ChatColor.AQUA + "Diamond" + ChatColor.WHITE + ": " + stats.getFoundDiamonds() + "\n");
        message.append(ChatColor.YELLOW + "Found " + ChatColor.GREEN + "Emerald" + ChatColor.WHITE + ": " + stats.getFoundEmeralds() + "\n");
        message.append(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------" + "\n");

        return message.toString();
    }

    public static void onEnable() {
        if(Configuration.statsEnabled)
            loadListeners();

        loadCommands();
    }

    public static void onDisable() {
        if(Configuration.statsEnabled && Configuration.trackStats) {
            for(PlayerStats loaded : activeStats)
                saveStats(loaded, true);
        }
    }

    private static void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new StatsListener(), FC.getFactionsCore());
    }

    private static void loadCommands() {
        FC.getFactionsCore().getCommand("statistics").setExecutor(new StatsCommand());
    }

}
