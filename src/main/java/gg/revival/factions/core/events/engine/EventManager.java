package gg.revival.factions.core.events.engine;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import gg.revival.core.Revival;
import gg.revival.factions.claims.Claim;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.events.obj.CapZone;
import gg.revival.factions.core.events.obj.DTCEvent;
import gg.revival.factions.core.events.obj.Event;
import gg.revival.factions.core.events.obj.KOTHEvent;
import gg.revival.factions.obj.PlayerFaction;
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
import java.util.logging.Level;

public class EventManager {

    @Getter private FC core;
    @Getter Set<Event> events = Sets.newConcurrentHashSet();

    public EventManager(FC core) {
        this.core = core;
    }

    public Event getEventByName(String name) {
        for(Event event : events) {
            if(event.getEventName().equalsIgnoreCase(name))
                return event;
        }

        return null;
    }

    public Event getEventByLootChest(Location lootChestLocation) {
        for(Event event : events) {
            if(event.getLootChest().equals(lootChestLocation))
                return event;
        }

        return null;
    }

    public Event getEventByClaim(Claim claim) {
        for(Event event : events) {
            if(event.getHookedFactionId() != null && event.getHookedFactionId().equals(claim.getClaimOwner().getFactionID()))
                return event;
        }

        return null;
    }

    public Set<Event> getActiveEvents() {
        Set<Event> result = new HashSet<>();

        for(Event event : events) {
            if(event.isActive())
                result.add(event);
        }

        return result;
    }

    public void startEvent(Event event) {
        if(event.isActive()) return;

        event.setActive(true);

        if(event instanceof KOTHEvent) {
            KOTHEvent koth = (KOTHEvent)event;

            koth.setNextTicketTime(-1L);
            koth.setCappingFaction(null);
            koth.getTickets().clear();

            if(event.isPalace())
                Bukkit.broadcastMessage(core.getEvents().getEventMessages().asPalace(core.getEvents().getEventMessages().started(event)));
            else
                Bukkit.broadcastMessage(core.getEvents().getEventMessages().asKOTH(core.getEvents().getEventMessages().started(event)));
        }

        if(event instanceof DTCEvent) {
            DTCEvent dtc = (DTCEvent)event;

            dtc.setResetTime(-1L);
            dtc.setCappingFaction(null);
            dtc.getTickets().clear();

            if(event.isPalace())
                Bukkit.broadcastMessage(core.getEvents().getEventMessages().asPalace(core.getEvents().getEventMessages().started(event)));
            else
                Bukkit.broadcastMessage(core.getEvents().getEventMessages().asDTC(core.getEvents().getEventMessages().started(event)));
        }

        if(event.isPalace())
            core.getEvents().getPalaceManager().resetPalace();
    }

    public void stopEvent(Event event) {
        if(!event.isActive()) return;

        event.setActive(false);

        if(event instanceof KOTHEvent) {
            KOTHEvent koth = (KOTHEvent)event;

            koth.setCappingFaction(null);
            koth.setNextTicketTime(-1L);
            koth.getTickets().clear();

            if(event.isPalace())
                Bukkit.broadcastMessage(core.getEvents().getEventMessages().asPalace(core.getEvents().getEventMessages().stopped(event)));
            else
                Bukkit.broadcastMessage(core.getEvents().getEventMessages().asKOTH(core.getEvents().getEventMessages().stopped(event)));
        }

        if(event instanceof DTCEvent) {
            DTCEvent dtc = (DTCEvent)event;

            dtc.setResetTime(-1L);
            dtc.getTickets().clear();

            if(event.isPalace())
                Bukkit.broadcastMessage(core.getEvents().getEventMessages().asPalace(core.getEvents().getEventMessages().stopped(event)));
            else
                Bukkit.broadcastMessage(core.getEvents().getEventMessages().asDTC(core.getEvents().getEventMessages().stopped(event)));
        }
    }

    public void finishEvent(Event event) {
        if(!event.isActive()) return;
        event.setActive(false);

        if(event instanceof KOTHEvent) {
            KOTHEvent koth = (KOTHEvent)event;

            koth.setNextTicketTime(-1L);
            koth.setContested(false);
            koth.getTickets().clear();

            if(event.isPalace()) {
                Bukkit.broadcastMessage(core.getEvents().getEventMessages().asPalace(core.getEvents().getEventMessages().captured(event)));
                core.getEvents().getPalaceManager().setCappers(koth.getCappingFaction());

                for(UUID capper : koth.getCappingFaction().getRoster(true))
                    Revival.getCore().getAccountManager().addXP(capper, "Captured Palace (HCFR)", 2000);
            } else {
                Bukkit.broadcastMessage(core.getEvents().getEventMessages().asKOTH(core.getEvents().getEventMessages().captured(event)));

                for(UUID capper : koth.getCappingFaction().getRoster(true))
                    Revival.getCore().getAccountManager().addXP(capper, "Captured an event (HCFR)", 500);
            }

            event.setLootChestFaction(koth.getCappingFaction());
            event.setLootChestUnlockTime(System.currentTimeMillis() + (30 * 1000L));
        }

        if(event instanceof DTCEvent) {
            DTCEvent dtc = (DTCEvent)event;

            dtc.setResetTime(-1L);
            dtc.getTickets().clear();

            if(event.isPalace()) {
                Bukkit.broadcastMessage(core.getEvents().getEventMessages().asPalace(core.getEvents().getEventMessages().captured(event)));
                core.getEvents().getPalaceManager().setCappers(dtc.getCappingFaction());

                for(UUID capper : dtc.getCappingFaction().getRoster(true))
                    Revival.getCore().getAccountManager().addXP(capper, "Captured Palace (HCFR)", 2000);
            } else {
                Bukkit.broadcastMessage(core.getEvents().getEventMessages().asDTC(core.getEvents().getEventMessages().captured(event)));

                for(UUID capper : dtc.getCappingFaction().getRoster(true))
                    Revival.getCore().getAccountManager().addXP(capper, "Captured an event (HCFR)", 500);
            }

            event.setLootChestFaction(dtc.getCappingFaction());
            event.setLootChestUnlockTime(System.currentTimeMillis() + (30 * 1000L));
        }

        if(event.isPalace())
            spawnKeys(event, core.getConfiguration().defaultPalaceKeys);
        else
            spawnKeys(event, core.getConfiguration().defaultKothKeys);
    }

    public void tickEvent(Event event) {
        if(event instanceof KOTHEvent) {
            KOTHEvent koth = (KOTHEvent)event;
            PlayerFaction capper = koth.getCappingFaction();

            if(!koth.getTickets().containsKey(capper))
                koth.getTickets().put(capper, 0);

            ImmutableMap<PlayerFaction, Integer> ticketCache = ImmutableMap.copyOf(koth.getTickets());

            for(PlayerFaction faction : ticketCache.keySet()) {
                int tickets = ticketCache.get(faction);

                if(!capper.equals(faction))
                    tickets -= 1;
                else
                    tickets += 1;

                if(tickets <= 0)
                    koth.getTickets().remove(faction);
                else
                    koth.getTickets().put(faction, tickets);

                if(tickets >= koth.getWinCond()) {
                    finishEvent(koth);
                    return;
                }
            }

            if(koth.isPalace())
                Bukkit.broadcastMessage(core.getEvents().getEventMessages().asPalace(core.getEvents().getEventMessages().ticked(koth)));
            else
                Bukkit.broadcastMessage(core.getEvents().getEventMessages().asKOTH(core.getEvents().getEventMessages().ticked(koth)));
        }

        if(event instanceof DTCEvent) {
            DTCEvent dtc = (DTCEvent)event;
            PlayerFaction capper = dtc.getCappingFaction();

            ImmutableMap<PlayerFaction, Integer> ticketCache = ImmutableMap.copyOf(dtc.getTickets());

            dtc.getTickets().clear();

            if(ticketCache.isEmpty()) {
                dtc.getTickets().put(capper, 1);
                return;
            }

            int tickets = ticketCache.get(capper) + 1;

            if(tickets >= dtc.getWinCond()) {
                finishEvent(event);
                return;
            }

            dtc.getTickets().put(capper, tickets);

            if(tickets % 50 == 0 && tickets < dtc.getWinCond()) {
                if(dtc.isPalace())
                    Bukkit.broadcastMessage(core.getEvents().getEventMessages().asPalace(core.getEvents().getEventMessages().ticked(dtc)));
                else
                    Bukkit.broadcastMessage(core.getEvents().getEventMessages().asDTC(core.getEvents().getEventMessages().ticked(dtc)));
            }
        }
    }

    public void spawnKeys(Event event, int amount) {
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
                inventory.addItem(core.getEvents().getEventKeys().getKeys(amount));
            }
        }.runTaskLater(core, 5L);

        new BukkitRunnable() {
            public void run() {
                if(event instanceof KOTHEvent) {
                    if(event.isPalace())
                        event.getLootChestFaction().sendMessage(core.getEvents().getEventMessages().asPalace(ChatColor.LIGHT_PURPLE + "" + ChatColor.UNDERLINE + amount + " Event Keys" + ChatColor.YELLOW + " have spawned in the Event Loot Chest." + "\n" + ChatColor.AQUA + "You will also be able to loot the chests within the Palace for the following week!"));
                    else
                        event.getLootChestFaction().sendMessage(core.getEvents().getEventMessages().asKOTH(ChatColor.LIGHT_PURPLE + "" + ChatColor.UNDERLINE + amount + " Event Keys" + ChatColor.YELLOW + " have spawned in the Event Loot Chest."));
                }

                if(event instanceof DTCEvent) {
                    if(event.isPalace())
                        event.getLootChestFaction().sendMessage(core.getEvents().getEventMessages().asPalace(ChatColor.LIGHT_PURPLE + "" + ChatColor.UNDERLINE + amount + " Event Keys" + ChatColor.YELLOW + " have spawned in the Event Loot Chest." + "\n" + ChatColor.AQUA + "You will also be able to loot the chests within the Palace for the following week!"));
                    else
                        event.getLootChestFaction().sendMessage(core.getEvents().getEventMessages().asDTC(ChatColor.LIGHT_PURPLE + "" + ChatColor.UNDERLINE + amount + " Event Keys" + ChatColor.YELLOW + " have spawned in the Event Loot Chest."));
                }
            }
        }.runTaskLater(core, 5 * 20L);
    }

    public void loadEvents() {
        if(core.getFileManager().getEvents().get("events") == null) {
            core.getLog().log(Level.WARNING, "It appears there aren't any configured events. Skipping...");
            return;
        }

        for(String eventNames : core.getFileManager().getEvents().getConfigurationSection("events").getKeys(false)) {
            Location lootChest;
            UUID hookedFactionId = null;

            String displayName = ChatColor.translateAlternateColorCodes('&', core.getFileManager().getEvents().getString("events." + eventNames + ".display-name"));
            boolean palace = core.getFileManager().getEvents().getBoolean("events." + eventNames + ".palace");
            int winCond = core.getFileManager().getEvents().getInt("events." + eventNames + ".win-cond");

            if(core.getFileManager().getEvents().get("events." + eventNames + ".hooked-claim") != null)
                hookedFactionId = UUID.fromString(core.getFileManager().getEvents().getString("events." + eventNames + ".hooked-claim"));

            int lootChestX = core.getFileManager().getEvents().getInt("events." + eventNames + ".loot-chest.x");
            int lootChestY = core.getFileManager().getEvents().getInt("events." + eventNames + ".loot-chest.y");
            int lootChestZ = core.getFileManager().getEvents().getInt("events." + eventNames + ".loot-chest.z");
            String lootChestWorld = core.getFileManager().getEvents().getString("events." + eventNames + ".loot-chest.world");
            lootChest = new Location(Bukkit.getWorld(lootChestWorld), lootChestX, lootChestY, lootChestZ);

            Map<Integer, Map.Entry<Integer, Integer>> schedule = new HashMap<>();

            for(String days : core.getFileManager().getEvents().getConfigurationSection("events." + eventNames + ".schedule").getKeys(false)) {
                int hr = core.getFileManager().getEvents().getInt("events." + eventNames + ".schedule." + days + ".hr");
                int min = core.getFileManager().getEvents().getInt("events." + eventNames + ".schedule." + days + ".min");

                schedule.put(Integer.valueOf(days), new AbstractMap.SimpleEntry<>(hr, min));
            }

            if(core.getFileManager().getEvents().getString("events." + eventNames + ".type").equalsIgnoreCase("KOTH")) {
                Location cornerOne, cornerTwo;

                String worldName = core.getFileManager().getEvents().getString("events." + eventNames + ".capzone.world");

                int cornerOneX = core.getFileManager().getEvents().getInt("events." + eventNames + ".capzone.cornerone.x");
                int cornerOneY = core.getFileManager().getEvents().getInt("events." + eventNames + ".capzone.cornerone.y");
                int cornerOneZ = core.getFileManager().getEvents().getInt("events." + eventNames + ".capzone.cornerone.z");
                cornerOne = new Location(Bukkit.getWorld(worldName), cornerOneX, cornerOneY, cornerOneZ);

                int cornerTwoX = core.getFileManager().getEvents().getInt("events." + eventNames + ".capzone.cornertwo.x");
                int cornerTwoY = core.getFileManager().getEvents().getInt("events." + eventNames + ".capzone.cornertwo.y");
                int cornerTwoZ = core.getFileManager().getEvents().getInt("events." + eventNames + ".capzone.cornertwo.z");
                cornerTwo = new Location(Bukkit.getWorld(worldName), cornerTwoX, cornerTwoY, cornerTwoZ);

                int duration = core.getFileManager().getEvents().getInt("events." + eventNames + ".duration");

                KOTHEvent kothEvent = new KOTHEvent(eventNames, displayName, hookedFactionId, lootChest, schedule, new CapZone(cornerOne, cornerTwo, worldName), duration, winCond, palace);
                core.getEvents().getEventManager().getEvents().add(kothEvent);

                continue;
            }

            if(core.getFileManager().getEvents().getString("events." + eventNames + ".type").equalsIgnoreCase("DTC")) {
                Location coreLocation;

                int coreX = core.getFileManager().getEvents().getInt("events." + eventNames + ".core.x");
                int coreY = core.getFileManager().getEvents().getInt("events." + eventNames + ".core.y");
                int coreZ = core.getFileManager().getEvents().getInt("events." + eventNames + ".core.z");
                String coreWorld = core.getFileManager().getEvents().getString("events." + eventNames + ".core.world");
                coreLocation = new Location(Bukkit.getWorld(coreWorld), coreX, coreY, coreZ);

                int regenTimer = core.getFileManager().getEvents().getInt("events." + eventNames + ".regen-timer");

                DTCEvent dtcEvent = new DTCEvent(eventNames, displayName, hookedFactionId, lootChest, schedule, coreLocation, winCond, regenTimer, palace);
                core.getEvents().getEventManager().getEvents().add(dtcEvent);
            }
        }

        core.getLog().log("Loaded " + events.size() + " Events");
    }
}
