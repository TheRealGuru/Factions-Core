package gg.revival.factions.core.bastion.tag;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class CombatLogger
{

    @Getter UUID uuid;
    @Getter String displayName;
    @Getter Entity npc;
    @Getter Location location;
    @Getter List<ItemStack> inventoryContents;
    @Getter @Setter boolean isDead;

    public CombatLogger(UUID uuid, String displayName, Location location, List<ItemStack> inventoryContents) {
        this.uuid = uuid;
        this.displayName = displayName;
        this.location = location;
        this.inventoryContents = inventoryContents;
        this.isDead = false;
    }

    public void build() {
        Villager villager = (Villager)location.getWorld().spawnEntity(location, EntityType.VILLAGER);

        villager.setCanPickupItems(false);
        villager.setCustomName("(" + ChatColor.RED + "Combat Logger" + ChatColor.RESET + ") " + displayName);
        villager.setCustomNameVisible(true);
        villager.setAdult();
        villager.setProfession(Villager.Profession.LIBRARIAN);

        this.npc = villager;
    }

    public void destroy() {
        if(npc == null || npc.isDead()) return;

        npc.remove();
    }

    public void kill() {
        isDead = true;
        destroy();
    }

}
