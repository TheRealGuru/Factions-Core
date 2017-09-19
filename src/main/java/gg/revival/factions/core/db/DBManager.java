package gg.revival.factions.core.db;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import gg.revival.driver.MongoAPI;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.PlayerManager;
import gg.revival.factions.core.db.listener.DatabaseListener;
import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.obj.FPlayer;
import gg.revival.factions.timers.Timer;
import gg.revival.factions.timers.TimerManager;
import gg.revival.factions.timers.TimerType;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DBManager {

    @Getter @Setter static MongoCollection<Document> bastion;
    @Getter @Setter static MongoCollection<Document> deathbans;
    @Getter @Setter static MongoCollection<Document> lives;
    @Getter @Setter static MongoCollection<Document> stats;

    public static void saveTimerData(final FPlayer player) {
        final List<Timer> timers = new ArrayList<>(player.getTimers());

        new BukkitRunnable() {
            public void run() {
                if(bastion == null)
                    bastion = MongoAPI.getCollection(Configuration.databaseName, "bastion");

                MongoCollection<Document> collection = bastion;
                FindIterable<Document> query;

                try {
                    query = MongoAPI.getQueryByFilter(collection, "uuid", player.getUuid().toString());
                } catch (LinkageError err) {
                    saveTimerData(player);
                    return;
                }

                int tagDuration = 0, protectionDuration = 0, progressionDuration = 0;

                for(Timer timer : timers) {
                    if(timer.getType().equals(TimerType.TAG))
                        tagDuration = (int)((timer.getExpire() - System.currentTimeMillis()) / 1000L);

                    if(timer.getType().equals(TimerType.PVPPROT))
                        protectionDuration = (int)((timer.getExpire() - System.currentTimeMillis()) / 1000L);

                    if(timer.getType().equals(TimerType.PROGRESSION))
                        progressionDuration = (int)((timer.getExpire() - System.currentTimeMillis()) / 1000L);
                }

                Document foundDocument = query.first();
                Document newDocument = new Document("uuid", player.getUuid().toString())
                        .append("tag", tagDuration).append("protection", protectionDuration).append("progression", progressionDuration);

                if(foundDocument != null)
                    collection.replaceOne(foundDocument, newDocument);
                else
                    collection.insertOne(newDocument);
            }
        }.runTaskAsynchronously(FC.getFactionsCore());
    }

    public static void loadTimerData(UUID uuid) {
        new BukkitRunnable() {
            public void run() {
                if(bastion == null)
                    bastion = MongoAPI.getCollection(Configuration.databaseName, "bastion");

                MongoCollection<Document> collection = bastion;
                FindIterable<Document> query;

                try {
                    query = MongoAPI.getQueryByFilter(collection, "uuid", uuid.toString());
                } catch (LinkageError err) {
                    loadTimerData(uuid);
                    return;
                }

                Document foundDocument = query.first();
                int tagDuration = 0, protectionDuration = 0, progressionDuration = 0;

                if(foundDocument != null) {
                    tagDuration = foundDocument.getInteger("tag");
                    protectionDuration = foundDocument.getInteger("protection");
                    progressionDuration = foundDocument.getInteger("progression");
                } else {
                    protectionDuration = Configuration.pvpProtDuration;
                    progressionDuration = Configuration.progressDuration;
                }

                FPlayer facPlayer = PlayerManager.getPlayer(uuid);

                if(tagDuration > 0)
                    facPlayer.addTimer(TimerManager.createTimer(TimerType.TAG, tagDuration));

                if(protectionDuration > 0)
                    facPlayer.addTimer(TimerManager.createTimer(TimerType.PVPPROT, protectionDuration));

                if(progressionDuration > 0)
                    facPlayer.addTimer(TimerManager.createTimer(TimerType.PROGRESSION, progressionDuration));
            }
        }.runTaskAsynchronously(FC.getFactionsCore());
    }

    public static void onEnable() {
        loadListeners();
    }

    public static void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new DatabaseListener(), FC.getFactionsCore());
    }

}
