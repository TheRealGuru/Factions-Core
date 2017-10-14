package gg.revival.factions.core.kits;

import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FKit {

    @Getter String name;
    @Getter ItemStack helmet, chestplate, leggings, boots;
    @Getter Inventory contents;

    public FKit(String name, Inventory contents, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        this.name = name;
        this.contents = contents;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

}
