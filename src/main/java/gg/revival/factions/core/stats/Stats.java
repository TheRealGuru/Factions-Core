package gg.revival.factions.core.stats;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import gg.revival.driver.MongoAPI;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.stats.command.StatsCommand;
import gg.revival.factions.core.stats.listener.StatsListener;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Stats {

    @Getter private FC core;
    @Getter Set<PlayerStats> activeStats = Sets.newConcurrentHashSet();

    public Stats(FC core) {
        this.core = core;

        onEnable();
    }

    public void loadStats(UUID uuid) {
        getStats(uuid, stats -> {
            PlayerStats result = null;

            if(stats == null)
                result = new PlayerStats(uuid, 0L, System.currentTimeMillis(), 0, 0, 0, 0, 0);
            else
                result = stats;

            activeStats.add(result);
        });
    }

    public void getStats(UUID uuid, StatsCallback callback) {
        ImmutableList<PlayerStats> cache = ImmutableList.copyOf(activeStats);

        for(PlayerStats stats : cache) {
            if(stats.getUuid().equals(uuid)) {
                callback.onQueryDone(stats);
                return;
            }
        }

        new BukkitRunnable() {
            public void run() {
                if(core.getDatabaseManager().getStats() == null)
                    core.getDatabaseManager().setStats(MongoAPI.getCollection(core.getConfiguration().databaseName, "playerstats"));

                MongoCollection<Document> collection = core.getDatabaseManager().getStats();
                FindIterable<Document> query = null;

                try {
                    query = MongoAPI.getQueryByFilter(collection, "uuid", uuid.toString());
                } catch (LinkageError err) {
                    getStats(uuid, callback);
                    return;
                }

                Document foundDocument = query.first();

                if(foundDocument != null) {
                    int kills = foundDocument.getInteger("kills");
                    int deaths = foundDocument.getInteger("deaths");
                    long playtime = foundDocument.getLong("playtime");
                    long loginTime = -1L;
                    int foundGold = foundDocument.getInteger("gold");
                    int foundDiamond = foundDocument.getInteger("diamond");
                    int foundEmerald = foundDocument.getInteger("emerald");

                    PlayerStats playerStats = new PlayerStats(uuid, playtime, loginTime, kills, deaths, foundGold, foundDiamond, foundEmerald);

                    new BukkitRunnable() {
                        public void run() {
                            callback.onQueryDone(playerStats);
                        }
                    }.runTask(core);

                    return;
                }

                new BukkitRunnable() {
                    public void run() {
                        callback.onQueryDone(null);
                    }
                }.runTask(core);
            }
        }.runTaskAsynchronously(core);
    }

    public void saveStats(PlayerStats stats, boolean unsafe) {
        if(unsafe) {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

            Runnable saveTask = () -> {
                if(core.getDatabaseManager().getStats() == null)
                    core.getDatabaseManager().setStats(MongoAPI.getCollection(core.getConfiguration().databaseName, "playerstats"));

                MongoCollection<Document> collection = core.getDatabaseManager().getStats();
                FindIterable<Document> query = null;

                try {
                    query = MongoAPI.getQueryByFilter(collection, "uuid", stats.getUuid().toString());
                } catch (LinkageError err) {
                    saveStats(stats, unsafe);
                    return;
                }

                Document foundDocument = query.first();

                Document newDocument = new Document("uuid", stats.getUuid().toString())
                        .append("kills", stats.getKills())
                        .append("deaths", stats.getDeaths())
                        .append("playtime", stats.getPlaytime())
                        .append("gold", stats.getFoundGold())
                        .append("diamond", stats.getFoundDiamonds())
                        .append("emerald", stats.getFoundEmeralds());

                if(foundDocument != null)
                    collection.replaceOne(foundDocument, newDocument);
                else
                    collection.insertOne(newDocument);
            };

            executorService.execute(saveTask);
        }

        else {
            new BukkitRunnable() {
                public void run() {
                    if(core.getDatabaseManager().getStats() == null)
                        core.getDatabaseManager().setStats(MongoAPI.getCollection(core.getConfiguration().databaseName, "playerstats"));

                    MongoCollection<Document> collection = core.getDatabaseManager().getStats();
                    FindIterable<Document> query = null;

                    try {
                        query = MongoAPI.getQueryByFilter(collection, "uuid", stats.getUuid().toString());
                    } catch (LinkageError err) {
                        saveStats(stats, unsafe);
                        return;
                    }

                    Document foundDocument = query.first();

                    Document newDocument = new Document("uuid", stats.getUuid().toString())
                            .append("kills", stats.getKills())
                            .append("deaths", stats.getDeaths())
                            .append("playtime", stats.getPlaytime())
                            .append("gold", stats.getFoundGold())
                            .append("diamond", stats.getFoundDiamonds())
                            .append("emerald", stats.getFoundEmeralds());

                    if(foundDocument != null)
                        collection.replaceOne(foundDocument, newDocument);
                    else
                        collection.insertOne(newDocument);
                }
            }.runTaskAsynchronously(core);
        }
    }

    /*

     */

    public String getFormattedStats(PlayerStats stats, String statsUsername) {
        StringBuilder message = new StringBuilder();

        message.append(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "-----------------------------------" + "\n");

        message.append(ChatColor.RESET + statsUsername + ChatColor.BOLD + " | " +
                ChatColor.GREEN + "Kills" + ChatColor.WHITE + ": " + stats.getKills() + ChatColor.RESET + " - " +
                ChatColor.RED + "Deaths" + ChatColor.WHITE + ": " + stats.getDeaths() + "\n     \n");

        int seconds = (int) (stats.getCurrentPlaytime() / 1000) % 60;
        int minutes = (int) (stats.getCurrentPlaytime() / (1000 * 60) % 60);
        int hours   = (int) (stats.getCurrentPlaytime() / (1000 * 60 * 60) % 24);

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

        message.append(ChatColor.BLUE + "Time Played" + ChatColor.WHITE + ": " + time.toString().trim() + "\n");

        message.append(ChatColor.GREEN + "Emeralds" + ChatColor.WHITE + ": " + stats.getFoundEmeralds() +
                " " + ChatColor.AQUA + "Diamonds" + ChatColor.WHITE + ": " + stats.getFoundDiamonds() +
                " " + ChatColor.GOLD + "Gold" + ChatColor.WHITE + ": " + stats.getFoundGold() + "\n");

        message.append(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "-----------------------------------" + "\n");

        return message.toString();
    }

    public void onEnable() {
        if(core.getConfiguration().statsEnabled)
            loadListeners();

        loadCommands();
    }

    public void onDisable() {
        if(core.getConfiguration().statsEnabled && core.getConfiguration().trackStats) {
            for(PlayerStats loaded : activeStats)
                saveStats(loaded, true);
        }
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new StatsListener(core), core);
    }

    private void loadCommands() {
        core.getCommand("statistics").setExecutor(new StatsCommand(core));
    }

}
