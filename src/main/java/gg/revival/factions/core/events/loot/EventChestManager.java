package gg.revival.factions.core.events.loot;

import gg.revival.factions.core.tools.FileManager;
import gg.revival.factions.core.tools.Logger;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;

public class EventChestManager {

    @Getter static Set<EventChest> eventChests = new HashSet<>();

    public static EventChest getEventChestAtLocation(Location location) {
        for(EventChest eventChest : eventChests) {
            if(!eventChest.getLocation().equals(location)) continue;

            return eventChest;
        }

        return null;
    }

    public static List<ItemStack> getRandomItems(EventChest lootChest, int pulls) {
        Random random = new Random();
        List<ItemStack> result = new ArrayList<>();

        Inventory lootChestInventory = LootTableManager.getLootTableByName(lootChest.getLootTable());
        List<ItemStack> pool = new ArrayList<>();

        for(ItemStack contents : lootChestInventory.getContents()) {
            if(contents == null || contents.getType().equals(Material.AIR)) continue;
            pool.add(contents);
        }

        for(int i = 0; i < pulls; i++)
            result.add(pool.get(random.nextInt(pool.size())));

        return result;
    }

    public static void createEventChest(EventChest lootChest) {
        ArmorStand armorStand = (ArmorStand)lootChest.getLocation().getWorld().spawnEntity(lootChest.getLocation().add(0.5, 0, 0.5), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setSmall(true);

        ChatColor color = ChatColor.GOLD;

        switch(lootChest.getType()) {
            case RARE: color = ChatColor.AQUA;
                break;
            case COMBAT: color = ChatColor.DARK_RED;
                break;
            case BREWING: color = ChatColor.YELLOW;
        }

        armorStand.setCustomName(ChatColor.WHITE + "[ " + color + StringUtils.capitalize(lootChest.getType().toString().toLowerCase()) + " Loot Chest" + ChatColor.WHITE + " ]");

        eventChests.add(lootChest);

        saveEventChest(lootChest);
    }

    public static void deleteEventChest(EventChest lootChest) {
        String uuid = lootChest.getUuid().toString();

        FileManager.getEvents().set("event-chests." + uuid, null);
        FileManager.saveEvents();

        eventChests.remove(lootChest);

        Logger.log("Removed an Event Chest");
    }

    public static void saveEventChest(EventChest lootChest) {
        String uuid = lootChest.getUuid().toString();

        FileManager.getEvents().set("event-chests." + uuid + ".loot-table", lootChest.getLootTable());
        FileManager.getEvents().set("event-chests." + uuid + ".type", lootChest.getType().toString());
        FileManager.getEvents().set("event-chests." + uuid + ".location.x", lootChest.getLocation().getBlockX());
        FileManager.getEvents().set("event-chests." + uuid + ".location.y", lootChest.getLocation().getBlockY());
        FileManager.getEvents().set("event-chests." + uuid + ".location.z", lootChest.getLocation().getBlockZ());
        FileManager.getEvents().set("event-chests." + uuid + ".location.world", lootChest.getLocation().getWorld().getName());

        FileManager.saveEvents();

        Logger.log("Added a new Event Chest");
    }

    public static void loadEventChests() {
        if(FileManager.getEvents().get("event-chests") == null) {
            Logger.log(Level.WARNING, "Couldn't find any configured Event Chests");
            return;
        }

        for(String keys : FileManager.getEvents().getConfigurationSection("event-chests").getKeys(false)) {
            String foundLootTable = FileManager.getEvents().getString("event-chests." + keys + ".loot-table");
            String foundType = FileManager.getEvents().getString("event-chests." + keys + ".type");
            int x = FileManager.getEvents().getInt("event-chests." + keys + ".location.x");
            int y = FileManager.getEvents().getInt("event-chests." + keys + ".location.y");
            int z = FileManager.getEvents().getInt("event-chests." + keys + ".location.z");
            String worldName = FileManager.getEvents().getString("event-chests." + keys + ".location.world");

            UUID uuid = UUID.fromString(keys);
            EventChestType type = null;
            Location location = new Location(Bukkit.getWorld(worldName), x, y, z);

            switch(foundType) {
                case "RARE": type = EventChestType.RARE;
                    break;
                case "COMBAT": type = EventChestType.COMBAT;
                    break;
                case "BREWING": type = EventChestType.BREWING;
                    break;
            }

            EventChest eventChest = new EventChest(uuid, type, location, foundLootTable);
            eventChests.add(eventChest);
        }

        Logger.log("Loaded " + eventChests.size() + " Event Chests");
    }
}
