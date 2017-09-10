package gg.revival.factions.core.events.engine;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.events.loot.EventKey;
import gg.revival.factions.core.events.messages.EventsMessages;
import gg.revival.factions.core.events.obj.CapZone;
import gg.revival.factions.core.events.obj.DTCEvent;
import gg.revival.factions.core.events.obj.Event;
import gg.revival.factions.core.events.obj.KOTHEvent;
import gg.revival.factions.core.tools.FileManager;
import gg.revival.factions.core.tools.Logger;
import gg.revival.factions.obj.PlayerFaction;
import gg.revival.factions.obj.ServerFaction;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventManager {

    @Getter static Set<Event> events = new HashSet<>();

    public static Event getEventByName(String name) {
        List<Event> cache = new CopyOnWriteArrayList<>(events);

        for(Event event : cache) {
            if(event.getEventName().equalsIgnoreCase(name))
                return event;
        }

        return null;
    }

    public static Event getEventByLootChest(Location lootChestLocation) {
        List<Event> cache = new CopyOnWriteArrayList<>(events);

        for(Event event : cache) {
            if(event.getLootChest().equals(lootChestLocation))
                return event;
        }

        return null;
    }

    public static Set<Event> getActiveEvents() {
        List<Event> cache = new CopyOnWriteArrayList<>(events);
        Set<Event> result = new HashSet<>();

        for(Event event : cache) {
            if(event.isActive())
                result.add(event);
        }

        return result;
    }

    public static void startEvent(Event event) {
        if(event.isActive()) return;

        event.setActive(true);

        if(event instanceof KOTHEvent) {
            KOTHEvent koth = (KOTHEvent)event;

            koth.setNextTicketTime(-1L);
            koth.setCappingFaction(null);
            koth.getTickets().clear();

            if(event.isPalace())
                Bukkit.broadcastMessage(EventsMessages.asPalace(EventsMessages.started(event)));
            else
                Bukkit.broadcastMessage(EventsMessages.asKOTH(EventsMessages.started(event)));
        }

        if(event instanceof DTCEvent) {
            DTCEvent dtc = (DTCEvent)event;

            dtc.setResetTime(-1L);
            dtc.setCappingFaction(null);
            dtc.getTickets().clear();

            if(event.isPalace())
                Bukkit.broadcastMessage(EventsMessages.asPalace(EventsMessages.started(event)));
            else
                Bukkit.broadcastMessage(EventsMessages.asDTC(EventsMessages.started(event)));
        }
    }

    public static void stopEvent(Event event) {
        if(!event.isActive()) return;

        event.setActive(false);

        if(event instanceof KOTHEvent) {
            KOTHEvent koth = (KOTHEvent)event;

            koth.setCappingFaction(null);
            koth.setNextTicketTime(-1L);
            koth.getTickets().clear();

            if(event.isPalace())
                Bukkit.broadcastMessage(EventsMessages.asPalace(EventsMessages.stopped(event)));
            else
                Bukkit.broadcastMessage(EventsMessages.asKOTH(EventsMessages.stopped(event)));
        }

        if(event instanceof DTCEvent) {
            DTCEvent dtc = (DTCEvent)event;

            dtc.setResetTime(-1L);
            dtc.getTickets().clear();

            if(event.isPalace())
                Bukkit.broadcastMessage(EventsMessages.asPalace(EventsMessages.stopped(event)));
            else
                Bukkit.broadcastMessage(EventsMessages.asDTC(EventsMessages.stopped(event)));
        }
    }

    public static void finishEvent(Event event) {
        if(!event.isActive()) return;
        event.setActive(false);

        if(event instanceof KOTHEvent) {
            KOTHEvent koth = (KOTHEvent)event;

            koth.setNextTicketTime(-1L);
            koth.setContested(false);
            koth.getTickets().clear();

            if(event.isPalace())
                Bukkit.broadcastMessage(EventsMessages.asPalace(EventsMessages.captured(event)));
            else
                Bukkit.broadcastMessage(EventsMessages.asKOTH(EventsMessages.captured(event)));

            event.setLootChestFaction(koth.getCappingFaction());
            // TODO: Get this value from config
            event.setLootChestUnlockTime(System.currentTimeMillis() + (30 * 1000L));
        }

        if(event instanceof DTCEvent) {
            DTCEvent dtc = (DTCEvent)event;

            dtc.setResetTime(-1L);
            dtc.getTickets().clear();

            if(event.isPalace())
                Bukkit.broadcastMessage(EventsMessages.asPalace(EventsMessages.captured(event)));
            else
                Bukkit.broadcastMessage(EventsMessages.asDTC(EventsMessages.captured(event)));

            event.setLootChestFaction(dtc.getCappingFaction());
            // TODO: Get this value from config
            event.setLootChestUnlockTime(System.currentTimeMillis() + (30 * 1000L));
        }

        // TODO: Get value from config
        spawnKeys(event, 3);
    }

    public static void tickEvent(Event event) {
        if(event instanceof KOTHEvent) {
            KOTHEvent koth = (KOTHEvent)event;
            PlayerFaction capper = koth.getCappingFaction();

            Map<PlayerFaction, Integer> ticketCache = new HashMap<>();
            ticketCache.putAll(koth.getTickets());

            if(ticketCache.isEmpty()) {
                koth.getTickets().put(capper, 1);

                if(koth.isPalace())
                    Bukkit.broadcastMessage(EventsMessages.asPalace(EventsMessages.ticked(koth)));
                else
                    Bukkit.broadcastMessage(EventsMessages.asKOTH(EventsMessages.ticked(koth)));

                return;
            }

            koth.getTickets().clear();

            for(PlayerFaction factions : ticketCache.keySet()) {
                int newTicketCount = ticketCache.get(factions);

                if(factions.getFactionID().equals(capper.getFactionID())) {
                    newTicketCount += 1;
                } else {
                    newTicketCount -= 1;
                }

                if(newTicketCount >= koth.getWinCond()) {
                    finishEvent(event);
                    return;
                }

                if(newTicketCount <= 0) continue;

                koth.getTickets().put(factions, newTicketCount);
            }

            if(koth.isPalace())
                Bukkit.broadcastMessage(EventsMessages.asPalace(EventsMessages.ticked(koth)));
            else
                Bukkit.broadcastMessage(EventsMessages.asKOTH(EventsMessages.ticked(koth)));
        }

        if(event instanceof DTCEvent) {
            DTCEvent dtc = (DTCEvent)event;
            PlayerFaction capper = dtc.getCappingFaction();

            Map<PlayerFaction, Integer> ticketCache = new HashMap<>();
            ticketCache.putAll(dtc.getTickets());

            dtc.getTickets().clear();

            for(PlayerFaction factions : ticketCache.keySet()) {
                int newTicketCount = ticketCache.get(factions);

                if(factions.getFactionID().equals(capper.getFactionID())) {
                    newTicketCount += 1;
                } else {
                    newTicketCount -= 1;
                }

                if(newTicketCount >= dtc.getWinCond()) {
                    finishEvent(event);
                    return;
                }

                if(newTicketCount <= 0) continue;

                dtc.getTickets().put(factions, newTicketCount);
            }
        }
    }

    public static void spawnKeys(Event event, int amount) {
        Location lootChestLocation = event.getLootChest();
        Chest chest = (Chest)lootChestLocation.getBlock().getState();
        Inventory inventory = chest.getBlockInventory();

        for(HumanEntity entities : inventory.getViewers()) {
            if(!(entities instanceof Player)) continue;

            Player player = (Player)entities;

            if(!event.getLootChestFaction().getRoster(true).contains(player.getUniqueId())) {
                player.closeInventory();
            }
        }

        new BukkitRunnable() {
            public void run() {
                inventory.addItem(EventKey.getKey(amount));
            }
        }.runTaskLater(FC.getFactionsCore(), 5L);

        new BukkitRunnable() {
            public void run() {
                if(event instanceof KOTHEvent) {
                    if(event.isPalace())
                        event.getLootChestFaction().sendMessage(EventsMessages.asPalace(ChatColor.LIGHT_PURPLE + "" + ChatColor.UNDERLINE + amount + " Event Keys" + ChatColor.YELLOW + " have spawned in the Event Loot Chest." + "\n" + ChatColor.AQUA + "You will also be able to loot the chests within the Palace for the following week!"));
                    else
                        event.getLootChestFaction().sendMessage(EventsMessages.asKOTH(ChatColor.LIGHT_PURPLE + "" + ChatColor.UNDERLINE + amount + " Event Keys" + ChatColor.YELLOW + " have spawned in the Event Loot Chest."));
                }

                if(event instanceof DTCEvent) {
                    if(event.isPalace())
                        event.getLootChestFaction().sendMessage(EventsMessages.asPalace(ChatColor.LIGHT_PURPLE + "" + ChatColor.UNDERLINE + amount + " Event Keys" + ChatColor.YELLOW + " have spawned in the Event Loot Chest." + "\n" + ChatColor.AQUA + "You will also be able to loot the chests within the Palace for the following week!"));
                    else
                        event.getLootChestFaction().sendMessage(EventsMessages.asDTC(ChatColor.LIGHT_PURPLE + "" + ChatColor.UNDERLINE + amount + " Event Keys" + ChatColor.YELLOW + " have spawned in the Event Loot Chest."));
                }
            }
        }.runTaskLater(FC.getFactionsCore(), 5 * 20L);
    }

    public static void loadEvents() {
        if(FileManager.getEvents().get("events") == null)
            return;

        for(String eventNames : FileManager.getEvents().getConfigurationSection("events").getKeys(false)) {
            Location lootChest = null;

            if(EventManager.getEventByName(eventNames) != null) continue;

            String displayName = ChatColor.translateAlternateColorCodes('&', FileManager.getEvents().getString("events." + eventNames + ".display-name"));

            int lootChestX = FileManager.getEvents().getInt("events." + eventNames + ".loot-chest.x");
            int lootChestY = FileManager.getEvents().getInt("events." + eventNames + ".loot-chest.y");
            int lootChestZ = FileManager.getEvents().getInt("events." + eventNames + ".loot-chest.z");
            String lootChestWorld = FileManager.getEvents().getString("events." + eventNames + ".loot-chest.world");
            lootChest = new Location(Bukkit.getWorld(lootChestWorld), lootChestX, lootChestY, lootChestZ);

            Map<Integer, Map<Integer, Integer>> schedule = new HashMap<>();

            for(String days : FileManager.getEvents().getConfigurationSection("events." + eventNames + ".schedule").getKeys(false)) {
                int hr = FileManager.getEvents().getInt("events." + eventNames + ".schedule." + days + ".hr");
                int min = FileManager.getEvents().getInt("events." + eventNames + ".schedule." + days + ".min");

                Map<Integer, Integer> time = new HashMap<>();
                time.put(hr, min);

                schedule.put(Integer.valueOf(days), time);
            }

            boolean palace = FileManager.getEvents().getBoolean("events." + eventNames + ".palace");

            if(FileManager.getEvents().getString("events." + eventNames + ".type").equalsIgnoreCase("KOTH")) {
                Location cornerOne = null, cornerTwo = null;

                String worldName = FileManager.getEvents().getString("events." + eventNames + ".capzone.world");

                int cornerOneX = FileManager.getEvents().getInt("events." + eventNames + ".capzone.cornerone.x");
                int cornerOneY = FileManager.getEvents().getInt("events." + eventNames + ".capzone.cornerone.y");
                int cornerOneZ = FileManager.getEvents().getInt("events." + eventNames + ".capzone.cornerone.z");
                cornerOne = new Location(Bukkit.getWorld(worldName), cornerOneX, cornerOneY, cornerOneZ);

                int cornerTwoX = FileManager.getEvents().getInt("events." + eventNames + ".capzone.cornertwo.x");
                int cornerTwoY = FileManager.getEvents().getInt("events." + eventNames + ".capzone.cornertwo.y");
                int cornerTwoZ = FileManager.getEvents().getInt("events." + eventNames + ".capzone.cornertwo.z");
                cornerTwo = new Location(Bukkit.getWorld(worldName), cornerTwoX, cornerTwoY, cornerTwoZ);

                int duration = FileManager.getEvents().getInt("events." + eventNames + ".duration");
                int winCond = FileManager.getEvents().getInt("events." + eventNames + ".win-cond");
                ServerFaction serverFaction = (ServerFaction) FactionManager.getFactionByUUID(UUID.fromString(FileManager.getEvents().getString("events." + eventNames + ".hooked-claim")));

                KOTHEvent kothEvent = new KOTHEvent(eventNames, displayName, serverFaction, lootChest, schedule, new CapZone(cornerOne, cornerTwo, worldName), duration, winCond, palace);
                EventManager.getEvents().add(kothEvent);

                continue;
            }

            if(FileManager.getEvents().getString("events." + eventNames + ".type").equalsIgnoreCase("DTC")) {

                continue;
            }
        }

        Logger.log("Loaded " + events.size() + " Events");
    }
}
