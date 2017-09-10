package gg.revival.factions.core.deathbans;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import gg.revival.core.accounts.Account;
import gg.revival.core.punishments.Punishment;
import gg.revival.driver.MongoAPI;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.db.DBManager;
import gg.revival.factions.core.deathbans.command.DeathbanCommand;
import gg.revival.factions.core.deathbans.command.DeathsCommand;
import gg.revival.factions.core.deathbans.listener.DeathbanListener;
import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.core.tools.Logger;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Deathbans
{

    /**
     * Contains all active deathbans currently running on the server
     */
    @Getter static Set<Death> activeDeathbans = new HashSet<>();

    /**
     * Returns a Death object if the given UUID has an active deathban
     * @param uuid
     * @return
     */
    public static Death getActiveDeathban(UUID uuid)
    {
        List<Death> cache = new CopyOnWriteArrayList<>(activeDeathbans);

        for(Death deaths : cache)
        {
            if(deaths.getKilled().equals(uuid) && !deaths.isExpired())
                return deaths;
        }

        return null;
    }

    /**
     * Loads all deathbans from DB to cache
     */
    public static void loadDeathbans()
    {
        if(!MongoAPI.isConnected()) {
            new BukkitRunnable() {
                public void run() {
                    loadDeathbans();
                }
            }.runTaskLaterAsynchronously(FC.getFactionsCore(), 2 * 20L);

            return;
        }

        new BukkitRunnable() {
            public void run() {
                if(DBManager.getDeathbans() == null)
                    DBManager.setDeathbans(MongoAPI.getCollection(Configuration.databaseName, "deathbans"));

                MongoCollection collection = DBManager.getDeathbans();
                FindIterable<Document> query = collection.find();

                for (Document current : query) {
                    if (current.getLong("expires") <= System.currentTimeMillis()) continue;

                    UUID uuid = UUID.fromString(current.getString("uuid"));
                    UUID killed = UUID.fromString(current.getString("killed"));
                    String reason = current.getString("reason");
                    long created = current.getLong("created");
                    long expires = current.getLong("expires");

                    Death death = new Death(uuid, killed, reason, created, expires);

                    activeDeathbans.add(death);
                }

                Logger.log("Loaded " + activeDeathbans.size() + " deathbans");
            }
        }.runTaskAsynchronously(FC.getFactionsCore());
    }

    /**
     * Saves all deathbans from cache to DB
     * @param unsafe Block the thread? Usually used in the onDisable
     */
    public static void saveDeathbans(boolean unsafe)
    {
        if(!MongoAPI.isConnected())
            return;

        if(unsafe)
        {
            if(DBManager.getDeathbans() == null)
                DBManager.setDeathbans(MongoAPI.getCollection(Configuration.databaseName, "deathbans"));

            MongoCollection collection = DBManager.getDeathbans();

            for(Death deaths : activeDeathbans)
            {
                FindIterable<Document> query = collection.find(Filters.eq("uuid", deaths.getUuid().toString()));
                Document document = query.first();

                Document newDoc = new Document("uuid", deaths.getUuid().toString())
                        .append("killed", deaths.getKilled().toString())
                        .append("reason", deaths.getReason())
                        .append("created", deaths.getCreatedTime())
                        .append("expires", deaths.getExpiresTime());

                if(document != null)
                {
                    collection.replaceOne(document, newDoc);
                }

                else
                {
                    collection.insertOne(newDoc);
                }
            }
        }

        else
        {
            new BukkitRunnable() {
                public void run() {
                    if(DBManager.getDeathbans() == null)
                        DBManager.setDeathbans(MongoAPI.getCollection(Configuration.databaseName, "deathbans"));

                    MongoCollection collection = DBManager.getDeathbans();

                    for(Death deaths : activeDeathbans)
                    {
                        FindIterable<Document> query = collection.find(Filters.eq("uuid", deaths.getUuid().toString()));
                        Document document = query.first();

                        Document newDoc = new Document("uuid", deaths.getUuid().toString())
                                .append("killed", deaths.getKilled().toString())
                                .append("reason", deaths.getReason())
                                .append("created", deaths.getCreatedTime())
                                .append("expires", deaths.getExpiresTime());

                        if(document != null)
                        {
                            collection.replaceOne(document, newDoc);
                        }

                        else
                        {
                            collection.insertOne(newDoc);
                        }
                    }
                }
            }.runTaskAsynchronously(FC.getFactionsCore());
        }
    }

    /**
     * Returnns a set of Deaths found by a users UUID
     * @param uuid
     * @param callback
     */
    public static void getDeathsByUUID(UUID uuid, DeathbanCallback callback)
    {
        if(!MongoAPI.isConnected())
        {
            Set<Death> emptyResult = new HashSet<>();
            callback.onQueryDone(emptyResult);
        }

        new BukkitRunnable() {
            public void run() {
                Set<Death> result = new HashSet<>();

                MongoCollection collection = DBManager.getDeathbans();
                FindIterable<Document> query = collection.find(Filters.eq("killed", uuid.toString()));
                Iterator<Document> iterator = query.iterator();

                while(true)
                {
                    if (!(iterator.hasNext())) break;
                    Document current = iterator.next();

                    UUID uuid = UUID.fromString(current.getString("uuid"));
                    UUID killed = UUID.fromString(current.getString("killed"));
                    String reason = current.getString("reason");
                    long created = current.getLong("created");
                    long expires = current.getLong("expires");

                    Death death = new Death(uuid, killed, reason, created, expires);

                    result.add(death);
                }

                new BukkitRunnable() {
                    public void run() {
                        callback.onQueryDone(result);
                    }
                }.runTask(FC.getFactionsCore());
            }
        }.runTaskAsynchronously(FC.getFactionsCore());
    }

    /**
     * Deathban and kick a player for a given duration and reason
     * @param uuid
     * @param reason
     * @param duration
     */
    public static void deathbanPlayer(UUID uuid, String reason, int duration)
    {
        UUID dbID = UUID.randomUUID();
        long created = System.currentTimeMillis();
        long expires = System.currentTimeMillis() + (duration * 1000L);

        Death death = new Death(dbID, uuid, reason, created, expires);

        activeDeathbans.add(death);

        if(Bukkit.getPlayer(uuid) != null)
        {
            Player player = Bukkit.getPlayer(uuid);

            player.kickPlayer(getDeathbanMessage(death));
        }
    }

    /**
     * Gets deathban kick message based on active deathban
     * @param death
     * @return
     */
    public static String getDeathbanMessage(Death death)
    {
        long duration = death.getExpiresTime() - System.currentTimeMillis();

        int seconds = (int) (duration / 1000) % 60;
        int minutes = (int) (duration / (1000 * 60) % 60);
        int hours   = (int) (duration / (1000 * 60 * 60) % 24);


        if(seconds >= 60)
            seconds = 59;

        StringBuilder time = new StringBuilder();

        if(hours > 0)
            time.append(hours + " hours ");

        if(minutes > 0)
            time.append(minutes + " minutes ");

        if(seconds > 0)
        {
            if(time.toString().length() > 0)
            {
                time.append("and " + seconds + " seconds ");
            }

            else
            {
                time.append(seconds + " seconds ");
            }
        }

        return ChatColor.RED + "You are deathbanned for another " + time.toString().trim() + "." + "\n" +
                ChatColor.RED + " You can bypass this deathban by using a life. Purchase lives at " + ChatColor.AQUA + "http://hcfrevival.net/store" + ChatColor.RESET + ".";
    }

    /**
     * Sends an organized list of deathbans based on a UUID
     * @param player
     * @param username
     * @param deaths
     */
    public static void sendDeathbans(Player player, String username, Set<Death> deaths)
    {
        player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------");
        player.sendMessage("Displaying most recent deathbans for " + ChatColor.RED + username);
        player.sendMessage(ChatColor.YELLOW + "Hover over each deathban to view more information.");
        player.sendMessage("     " );

        SimpleDateFormat formatter = new SimpleDateFormat("M-d-yyyy '@' hh:mm:ss a");

        int cursor = 1;

        for(Death death : deaths)
        {
            if(cursor >= 10) break;

            Date create = new Date(death.getCreatedTime());
            Date expire = new Date(death.getExpiresTime());
            List<String> info = new ArrayList<>();

            info.add("Died on: " + formatter.format(create));
            info.add("Expires on: " + formatter.format(expire));

            new FancyMessage(cursor + ". ").then(death.getReason()).color(ChatColor.RED).tooltip(info).send(player);

            cursor++;
        }

        player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------");
    }

    public static void onEnable()
    {
        loadCommands();
        loadListeners();
        loadCommands();
        loadDeathbans();
    }

    public static void onDisable()
    {
        saveDeathbans(true);
    }

    public static void loadCommands()
    {
        FC.getFactionsCore().getCommand("deathban").setExecutor(new DeathbanCommand());
        FC.getFactionsCore().getCommand("deaths").setExecutor(new DeathsCommand());
    }

    public static void loadListeners()
    {
        if(Configuration.deathbansEnabled)
            Bukkit.getPluginManager().registerEvents(new DeathbanListener(), FC.getFactionsCore());
    }

}
