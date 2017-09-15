package gg.revival.factions.core.lives;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import gg.revival.driver.MongoAPI;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.db.DBManager;
import gg.revival.factions.core.deathbans.Death;
import gg.revival.factions.core.deathbans.Deathbans;
import gg.revival.factions.core.lives.command.LivesCommand;
import gg.revival.factions.core.lives.command.ReviveCommand;
import org.bson.Document;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Lives {

    public static void getLives(UUID uuid, LivesCallback callback) {
        new BukkitRunnable() {
            public void run() {
                if(DBManager.getLives() == null)
                    DBManager.setLives(MongoAPI.getCollection("factions", "lives"));

                MongoCollection<Document> collection = DBManager.getLives();
                FindIterable<Document> query = MongoAPI.getQueryByFilter(collection, "uuid", uuid.toString());
                Document document = query.first();

                if(document == null) {
                    new BukkitRunnable() {
                        public void run() {
                            callback.onQueryDone(0);
                        }
                    }.runTask(FC.getFactionsCore());

                    return;
                }

                int lives = document.getInteger("lives");

                new BukkitRunnable() {
                    public void run() {
                        callback.onQueryDone(lives);
                    }
                }.runTask(FC.getFactionsCore());
            }
        }.runTaskAsynchronously(FC.getFactionsCore());
    }

    public static void setLives(UUID uuid, int newLives, LivesUpdateCallback callback) {
        new BukkitRunnable() {
            public void run() {
                if(DBManager.getLives() == null)
                    DBManager.setLives(MongoAPI.getCollection("factions", "lives"));

                MongoCollection<Document> collection = DBManager.getLives();
                FindIterable<Document> query = MongoAPI.getQueryByFilter(collection, "uuid", uuid.toString());
                Document document = query.first();
                Document newDoc = new Document("uuid", uuid.toString()).append("lives", newLives);

                if(document == null) {
                    collection.insertOne(newDoc);

                    new BukkitRunnable() {
                        public void run() {
                            callback.onUpdate(uuid, newLives);
                        }
                    }.runTask(FC.getFactionsCore());
                }

                else {
                    collection.replaceOne(document, newDoc);

                    new BukkitRunnable() {
                        public void run() {
                            callback.onUpdate(uuid, newLives);
                        }
                    }.runTask(FC.getFactionsCore());
                }
            }
        }.runTaskAsynchronously(FC.getFactionsCore());
    }

    public static void revivePlayer(UUID uuid, Death death, ReviveCallback callback) {
        new BukkitRunnable() {
            public void run() {
                death.setExpiresTime(System.currentTimeMillis());
                Deathbans.saveDeathban(death);

                new BukkitRunnable() {
                    public void run() {
                        callback.onComplete(true);
                    }
                }.runTask(FC.getFactionsCore());
            }
        }.runTaskAsynchronously(FC.getFactionsCore());
    }

    public static void onEnable() {
        loadCommands();
    }

    public static void loadCommands() {
        FC.getFactionsCore().getCommand("lives").setExecutor(new LivesCommand());
        FC.getFactionsCore().getCommand("revive").setExecutor(new ReviveCommand());
    }
}
