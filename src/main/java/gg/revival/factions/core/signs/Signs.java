package gg.revival.factions.core.signs;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.mechanics.crowbars.Crowbar;
import gg.revival.factions.core.signs.listener.SignsListener;
import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.material.SpawnEgg;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Signs {

    /**
     * Contains players who have recently interacted with a shop sign, prevents double-clicks
     */
    @Getter static Set<UUID> interactLock = new HashSet<>();

    /**
     * Returns an ItemStack based on name, checks for custom materials as well
     * @param itemName The item to be looked up
     * @return ItemStack based on name
     */
    public static ItemStack getItemStackFromString(String itemName) {
        Material material;
        int data = 0;

        String[] obj = itemName.split(":");

        if(obj.length == 2) {
            material = Material.matchMaterial(obj[0]);

            try {
                data = Integer.valueOf(obj[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        else {
            material = Material.matchMaterial(itemName);
        }

        if(material == null) {
            if(itemName.equalsIgnoreCase(CustomMaterial.CROWBAR.toString()))
                return Crowbar.getCrowbar();

            if(itemName.equalsIgnoreCase(CustomMaterial.CHAIN_HELMET.toString().replace("_", " ")))
                return new ItemStack(Material.CHAINMAIL_HELMET);

            if(itemName.equalsIgnoreCase(CustomMaterial.CHAIN_CHESTPLATE.toString().replace("_", " ")))
                return new ItemStack(Material.CHAINMAIL_CHESTPLATE);

            if(itemName.equalsIgnoreCase(CustomMaterial.CHAIN_LEGGINGS.toString().replace("_", " ")))
                return new ItemStack(Material.CHAINMAIL_LEGGINGS);

            if(itemName.equalsIgnoreCase(CustomMaterial.CHAIN_BOOTS.toString().replace("_", " ")))
                return new ItemStack(Material.CHAINMAIL_BOOTS);

            if(itemName.equalsIgnoreCase(CustomMaterial.COW_EGG.toString().replace("_", " "))) {
                SpawnEgg spawnEgg = new SpawnEgg();
                spawnEgg.setSpawnedType(EntityType.COW);
                return spawnEgg.toItemStack();
            }

            if(itemName.equalsIgnoreCase(CustomMaterial.END_PORTAL_FRAME.toString().replace("_", " ")))
                return new ItemStack(Material.ENDER_PORTAL_FRAME);

            if(itemName.equalsIgnoreCase(CustomMaterial.LAPIS.toString().replace("_", " "))) {
                Dye dye = new Dye();
                dye.setColor(DyeColor.BLUE);
                return dye.toItemStack();
            }
        }

        if(material == null) return null;

        ItemStack item = new ItemStack(material, 1);
        item.setDurability((short)data);
        return item;
    }

    /**
     * Returns true of the given strings meet a buysigns requirements
     * @param lineOne
     * @param lineTwo
     * @param lineThree
     * @param lineFour
     * @return Is a valid sign or not
     */
    public static boolean isBuySign(String lineOne, String lineTwo, String lineThree, String lineFour) {
        if(!lineOne.equals(ChatColor.DARK_GREEN + "[Buy]"))
            return false;

        if(!NumberUtils.isNumber(lineTwo.replace("Amt: ", ""))) return false;

        ItemStack item = getItemStackFromString(lineThree);

        if(item == null) return false;

        if(!NumberUtils.isNumber(lineFour)) return false;

        return true;
    }

    /**
     * Returns true if the given strings meet a sellsigns requirements
     * @param lineOne
     * @param lineTwo
     * @param lineThree
     * @param lineFour
     * @return Is a valid sell sign
     */
    public static boolean isSellSign(String lineOne, String lineTwo, String lineThree, String lineFour) {
        if(!lineOne.equals(ChatColor.DARK_RED + "[Sell]"))
            return false;

        if(!NumberUtils.isNumber(lineTwo.replace("Amt: ", ""))) return false;

        ItemStack item = getItemStackFromString(lineThree);

        if(item == null) return false;

        if(!NumberUtils.isNumber(lineFour)) return false;

        return true;
    }

    /**
     * Returns true if the given strings meet a valid signs requirements
     * @param lineTwo
     * @param lineThree
     * @param lineFour
     * @return
     */
    public static boolean isValidSign(String lineTwo, String lineThree, String lineFour) {
        if(!NumberUtils.isNumber(lineTwo)) return false;

        ItemStack item = getItemStackFromString(lineThree);

        if(item == null) return false;

        if(!NumberUtils.isNumber(lineFour)) return false;

        return true;
    }

    public static void onEnable() {
        loadListeners();
    }

    public static void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new SignsListener(), FC.getFactionsCore());
    }

}
