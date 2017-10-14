package gg.revival.factions.core.kits;

import com.google.common.collect.Sets;
import gg.revival.factions.core.FC;
import gg.revival.factions.core.kits.command.KitCommandExecutor;
import gg.revival.factions.core.tools.InvTools;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class Kits {

    @Getter private FC core;
    @Getter public Set<FKit> loadedKits = Sets.newHashSet();

    public Kits(FC core) {
        this.core = core;

        onEnable();
    }

    /**
     * Returns an FKit object based on the given name
     * @param name
     * @return
     */
    public FKit getKitByName(String name) {
        for(FKit kits : loadedKits)
            if(kits.getName().equalsIgnoreCase(name)) return kits;

        return null;
    }

    /**
     * Applies an FKit object to the given player
     * @param player
     * @param kit
     */
    public void giveKit(Player player, FKit kit) {
        if(!core.getConfiguration().kitsEnabled) {
            player.sendMessage(ChatColor.RED + "Kits are disabled on this server");
            return;
        }

        core.getPlayerTools().cleanupPlayer(player);

        player.getInventory().setHelmet(kit.getHelmet());
        player.getInventory().setChestplate(kit.getChestplate());
        player.getInventory().setLeggings(kit.getLeggings());
        player.getInventory().setBoots(kit.getBoots());

        for(ItemStack contents : kit.getContents()) {
            if(contents == null || contents.getType().equals(Material.AIR)) continue;
            player.getInventory().addItem(contents);
        }

        player.sendMessage(ChatColor.YELLOW + "Loaded kit" + ChatColor.WHITE + ": " + ChatColor.BLUE + kit.getName());
    }

    /**
     * Saves a players inventory in FKit format
     * @param player
     * @param name
     */
    public void saveKit(Player player, String name) {
        if(!core.getConfiguration().kitsEnabled) {
            player.sendMessage(ChatColor.RED + "Kits are disabled on this server");
            return;
        }

        FKit kit = new FKit(name, player.getInventory(),
                player.getInventory().getHelmet(), player.getInventory().getChestplate(), player.getInventory().getLeggings(), player.getInventory().getBoots());

        loadedKits.add(kit);

        core.getFileManager().getKits().set("kits." + name + ".contents", InvTools.toBase64(player.getInventory()));
        core.getFileManager().getKits().set("kits." + name + ".armor.helmet", InvTools.toBase64(player.getInventory().getHelmet()));
        core.getFileManager().getKits().set("kits." + name + ".armor.chestplate", InvTools.toBase64(player.getInventory().getChestplate()));
        core.getFileManager().getKits().set("kits." + name + ".armor.leggings", InvTools.toBase64(player.getInventory().getLeggings()));
        core.getFileManager().getKits().set("kits." + name + ".armor.boots", InvTools.toBase64(player.getInventory().getBoots()));

        core.getFileManager().saveKits();

        player.sendMessage(ChatColor.YELLOW + "Saved kit" + ChatColor.WHITE + ": " + ChatColor.BLUE + name);

        core.getLog().log(player.getName() + " created a new kit named '" + name + "'");
    }

    public void onEnable() {
        loadCommands();
    }

    private void loadCommands() {
        core.getCommand("kit").setExecutor(new KitCommandExecutor(core));
    }

}
