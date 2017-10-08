package gg.revival.factions.core.mechanics.crowbars;

import gg.revival.factions.claims.Claim;
import gg.revival.factions.claims.ClaimManager;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.FactionManager;
import gg.revival.factions.obj.Faction;
import gg.revival.factions.obj.PlayerFaction;
import gg.revival.factions.obj.ServerFaction;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Crowbar {

    @Getter private FC core;

    public Crowbar(FC core) {
        this.core = core;
    }

    public ItemStack getCrowbar() {
        ItemStack item = new ItemStack(Material.DIAMOND_HOE);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(ChatColor.DARK_RED + "Crowbar");

        itemMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Mob-spawner uses: " + ChatColor.BLUE + core.getConfiguration().crowbarSpawnerUse,
                ChatColor.YELLOW + "Portal-frame uses: " + ChatColor.BLUE + core.getConfiguration().crowbarPortalUse));

        item.setItemMeta(itemMeta);

        return item;
    }

    boolean isCrowbar(ItemStack item) {
        if(item == null || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) return false;

        String name = item.getItemMeta().getDisplayName();

        if(!name.equals(ChatColor.DARK_RED + "Crowbar")) return false;

        return true;
    }

    private int getRemainingSpawnerUses(ItemStack item) {
        if(!isCrowbar(item)) return 0;

        List<String> lore = item.getItemMeta().getLore();

        int spawners = 0;

        for(String line : lore) {
            if(line.contains(ChatColor.YELLOW + "Mob-spawner uses: " + ChatColor.BLUE))
                spawners = Integer.valueOf(line.replace(ChatColor.YELLOW + "Mob-spawner uses: " + ChatColor.BLUE, ""));
        }

        return spawners;
    }

    private int getRemainingPortalUses(ItemStack item) {
        if(!isCrowbar(item)) return 0;

        List<String> lore = item.getItemMeta().getLore();

        int portalFrames = 0;

        for(String line : lore) {
            if(line.contains(ChatColor.YELLOW + "Portal-frame uses: " + ChatColor.BLUE))
                portalFrames = Integer.valueOf(line.replace(ChatColor.YELLOW + "Portal-frame uses: " + ChatColor.BLUE, ""));
        }

        return portalFrames;
    }

    void attemptCrowbar(Player player, ItemStack item, Block block) {
        Claim claim = ClaimManager.getClaimAt(block.getLocation(), false);

        if(claim != null) {
            Faction faction = FactionManager.getFactionByPlayer(player.getUniqueId());

            if(faction == null || !claim.getClaimOwner().equals(faction.getFactionID())) {
                if(claim.getClaimOwner() instanceof ServerFaction)
                    return;

                if(claim.getClaimOwner() instanceof PlayerFaction) {
                    PlayerFaction playerFaction = (PlayerFaction)claim.getClaimOwner();

                    if(!playerFaction.isRaidable()) return;
                }
            }
        }

        List<String> lore = item.getItemMeta().getLore();
        List<String> newLore = new ArrayList<>();

        int spawnerUses = getRemainingSpawnerUses(item);
        int portalUses = getRemainingPortalUses(item);

        if(block.getType().equals(Material.MOB_SPAWNER)) {
            BlockState blockState = block.getState();
            CreatureSpawner creatureSpawner = ((CreatureSpawner)blockState);
            ItemStack asItem = null;
            ItemMeta meta = null;

            String mobName = StringUtils.capitalize(creatureSpawner.getSpawnedType().name().toLowerCase());

            if(spawnerUses <= 0) {
                player.sendMessage(ChatColor.RED + "Remaining mob-spawner uses: " + spawnerUses);
                return;
            }

            if(creatureSpawner.getSpawnedType().equals(EntityType.SILVERFISH) || !player.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                player.sendMessage(ChatColor.RED + "You are not allowed to claim this spawner");
                return;
            }

            ItemMeta crowbarMeta = item.getItemMeta();

            crowbarMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Mob-spawner uses: " + ChatColor.BLUE + (spawnerUses - 1),
                    ChatColor.YELLOW + "Portal-frame uses: " + ChatColor.BLUE + portalUses));

            item.setItemMeta(crowbarMeta);
            player.setItemInHand(item);

            asItem = new ItemStack(block.getType());
            meta = asItem.getItemMeta();
            meta.setDisplayName(ChatColor.DARK_RED + mobName);
            asItem.setItemMeta(meta);

            block.setType(Material.AIR);
            block.getWorld().dropItem(block.getLocation(), asItem);

            player.sendMessage(ChatColor.YELLOW + "Remaining mob-spawner uses: " + ChatColor.BLUE + (spawnerUses - 1));

            if(portalUses == 0 && (spawnerUses - 1) == 0) {
                if(isCrowbar(player.getInventory().getItemInHand())) {
                    player.getInventory().setItemInHand(null);
                    player.sendMessage(ChatColor.RED + "Your crowbar has snapped");
                }
            }
        }

        if(block.getType().equals(Material.ENDER_PORTAL_FRAME)) {
            if(portalUses <= 0) {
                player.sendMessage(ChatColor.RED + "Remaining portal-frame uses: " + portalUses);
                return;
            }

            for(String line : lore) {
                if(line.contains(ChatColor.YELLOW + "Portal-frame uses: " + ChatColor.BLUE))
                    line.replace(String.valueOf(portalUses), String.valueOf((portalUses - 1)));

                newLore.add(line);
            }

            ItemMeta crowbarMeta = item.getItemMeta();

            crowbarMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Mob-spawner uses: " + ChatColor.BLUE + spawnerUses,
                    ChatColor.YELLOW + "Portal-frame uses: " + ChatColor.BLUE + (portalUses - 1)));

            item.setItemMeta(crowbarMeta);
            player.setItemInHand(item);

            ItemStack asItem = new ItemStack(Material.ENDER_PORTAL_FRAME);

            block.setType(Material.AIR);
            block.getWorld().dropItem(block.getLocation(), asItem);

            player.sendMessage(ChatColor.YELLOW + "Remaining portal-frame uses: " + ChatColor.BLUE + (portalUses - 1));

            if((portalUses - 1) == 0 && spawnerUses == 0) {
                if(isCrowbar(player.getInventory().getItemInHand())) {
                    player.getInventory().setItemInHand(null);
                    player.sendMessage(ChatColor.RED + "Your crowbar has snapped");
                }
            }
        }
    }
}
