package gg.revival.factions.core.lives;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import gg.revival.driver.MongoAPI;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.deathbans.Death;
import gg.revival.factions.core.lives.command.LivesCommand;
import gg.revival.factions.core.lives.command.ReviveCommand;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Lives {

    @Getter private FC core;

    public Lives(FC core) {
        this.core = core;

        onEnable();
    }

    public void getLives(UUID uuid, LivesCallback callback) {
        new BukkitRunnable() {
            public void run() {
                if(core.getDatabaseManager().getLives() == null)
                    core.getDatabaseManager().setLives(MongoAPI.getCollection("factions", "lives"));

                MongoCollection<Document> collection = core.getDatabaseManager().getLives();
                FindIterable<Document> query = MongoAPI.getQueryByFilter(collection, "uuid", uuid.toString());
                Document document = query.first();

                if(document == null) {
                    new BukkitRunnable() {
                        public void run() {
                            callback.onQueryDone(0);
                        }
                    }.runTask(core);

                    return;
                }

                int lives = document.getInteger("lives");

                new BukkitRunnable() {
                    public void run() {
                        callback.onQueryDone(lives);
                    }
                }.runTask(core);
            }
        }.runTaskAsynchronously(core);
    }

    public void setLives(UUID uuid, int newLives, LivesUpdateCallback callback) {
        new BukkitRunnable() {
            public void run() {
                if(core.getDatabaseManager().getLives() == null)
                    core.getDatabaseManager().setLives(MongoAPI.getCollection("factions", "lives"));

                MongoCollection<Document> collection = core.getDatabaseManager().getLives();
                FindIterable<Document> query = MongoAPI.getQueryByFilter(collection, "uuid", uuid.toString());
                Document document = query.first();
                Document newDoc = new Document("uuid", uuid.toString()).append("lives", newLives);

                if(document == null) {
                    collection.insertOne(newDoc);

                    new BukkitRunnable() {
                        public void run() {
                            callback.onUpdate(uuid, newLives);
                        }
                    }.runTask(core);
                }

                else {
                    collection.replaceOne(document, newDoc);

                    new BukkitRunnable() {
                        public void run() {
                            callback.onUpdate(uuid, newLives);
                        }
                    }.runTask(core);
                }
            }
        }.runTaskAsynchronously(core);
    }

    public void revivePlayer(UUID uuid, Death death, ReviveCallback callback) {
        new BukkitRunnable() {
            public void run() {
                death.setExpiresTime(System.currentTimeMillis());
                core.getDeathbans().saveDeathban(death);

                new BukkitRunnable() {
                    public void run() {
                        callback.onComplete(true);
                    }
                }.runTask(core);
            }
        }.runTaskAsynchronously(core);
    }

    public void onEnable() {
        loadCommands();
    }

    private void loadCommands() {
        core.getCommand("lives").setExecutor(new LivesCommand(core));
        core.getCommand("revive").setExecutor(new ReviveCommand(core));
    }
}
