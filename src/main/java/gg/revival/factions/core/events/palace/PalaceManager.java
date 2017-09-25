package gg.revival.factions.core.events.palace;

import com.google.common.collect.Sets;
import gg.revival.factions.core.FactionManager;
import gg.revival.factions.core.events.loot.LootTableManager;
import gg.revival.factions.core.events.messages.EventsMessages;
import gg.revival.factions.core.tools.FileManager;
import gg.revival.factions.obj.PlayerFaction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PalaceManager {

    @Getter @Setter static PlayerFaction cappingFaction = null;
    @Getter @Setter static int publicChestLevel = 0;
    @Getter @Setter static boolean captured = false;

    @Getter static Set<PalaceChest> palaceChests = Sets.newHashSet();

    public static boolean isCappingFaction(PlayerFaction playerFaction) {
        if(getCappingFaction() == null) return false;

        if(!getCappingFaction().getFactionID().equals(playerFaction.getFactionID())) return false;

        return true;
    }

    public static PalaceChest getPalaceChestByLocation(Location location) {
        for(PalaceChest palaceChest : palaceChests) {
            if(palaceChest.getLocation().equals(location))
                return palaceChest;
        }

        return null;
    }

    public static void spawnLoot() {
        for(PalaceChest palaceChest : palaceChests) {
            Location location = palaceChest.getLocation();

            if(location.getBlock() == null || !location.getBlock().getType().equals(Material.CHEST)) continue;

            Chest chest = (Chest)location.getBlock();
            Inventory chestInventory = chest.getBlockInventory();

            chestInventory.clear();

            for(ItemStack toAdd : getRandomItems(palaceChest, 2)) {
                Random random = new Random();
                int slot = random.nextInt(chestInventory.getSize());

                chestInventory.setItem(slot, toAdd);
            }
        }

        Bukkit.broadcastMessage(EventsMessages.asPalace(ChatColor.BLUE + "Palace chests have been refilled"));
    }

    public static List<ItemStack> getRandomItems(PalaceChest palaceChest, int pulls) {
        Random random = new Random();
        List<ItemStack> result = new ArrayList<>();

        Inventory lootChestInventory = LootTableManager.getLootTableByName(palaceChest.getLootTable());
        List<ItemStack> pool = new ArrayList<>();

        for(ItemStack contents : lootChestInventory.getContents()) {
            if(contents == null || contents.getType().equals(Material.AIR)) continue;
            pool.add(contents);
        }

        for(int i = 0; i < pulls; i++)
            result.add(pool.get(random.nextInt(pool.size())));

        return result;
    }

    public static void savePalaceChest(PalaceChest palaceChest) {
        String uuid = palaceChest.getUuid().toString();

        FileManager.getEvents().set("palace-chests." + uuid + ".loot-table", palaceChest.getLootTable());
        FileManager.getEvents().set("palace-chests." + uuid + ".level", palaceChest.getLevel());
        FileManager.getEvents().set("palace-chests." + uuid + ".location.x", palaceChest.getLocation().getBlockX());
        FileManager.getEvents().set("palace-chests." + uuid + ".location.y", palaceChest.getLocation().getBlockY());
        FileManager.getEvents().set("palace-chests." + uuid + ".location.z", palaceChest.getLocation().getBlockZ());
        FileManager.getEvents().set("palace-chests." + uuid + ".location.world", palaceChest.getLocation().getWorld().getName());

        FileManager.saveEvents();
    }

    public static void deletePalaceChest(PalaceChest palaceChest) {
        FileManager.getEvents().set("palace-chests." + palaceChest.getUuid().toString(), null);
        FileManager.saveEvents();
    }

    public static void loadPalace() {
        captured = FileManager.getEvents().getBoolean("configuration.palace.captured");

        if(captured)
            cappingFaction = (PlayerFaction)FactionManager.getFactionByUUID(UUID.fromString(FileManager.getEvents().getString("configuration.palace.capping-faction")));

        if(captured) {
            Calendar calendar = Calendar.getInstance();

            Calendar monday = Calendar.getInstance();
            monday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

            Calendar wednesday = Calendar.getInstance();
            wednesday.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);

            Calendar friday = Calendar.getInstance();
            friday.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

            if(calendar.after(friday))
                setPublicChestLevel(3);
            else if(calendar.after(wednesday))
                setPublicChestLevel(2);
            else if(calendar.after(monday))
                setPublicChestLevel(1);
            else
                setPublicChestLevel(0);
        }
    }
}
