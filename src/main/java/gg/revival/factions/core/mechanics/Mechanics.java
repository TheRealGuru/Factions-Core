package gg.revival.factions.core.mechanics;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.mechanics.crowbars.CrowbarCommand;
import gg.revival.factions.core.mechanics.crowbars.CrowbarListener;
import gg.revival.factions.core.mechanics.emeraldxp.EmeraldEXPListener;
import gg.revival.factions.core.mechanics.endermite.EndermiteListener;
import gg.revival.factions.core.mechanics.enderpearlcd.EnderpearlCDListener;
import gg.revival.factions.core.mechanics.hardmode.HardmodeListener;
import gg.revival.factions.core.mechanics.highspawners.HighSpawnerListener;
import gg.revival.factions.core.mechanics.invalidpearl.InvalidPearlListener;
import gg.revival.factions.core.mechanics.mobstacking.Mobstacker;
import gg.revival.factions.core.mechanics.mobstacking.MobstackingListener;
import gg.revival.factions.core.mechanics.portalprotection.NetherPortalListener;
import gg.revival.factions.core.mechanics.spawnerbreaking.SpawnerBreakingListener;
import gg.revival.factions.core.mechanics.unenchantablebooks.BookUnchantmentListener;
import gg.revival.factions.core.tools.Configuration;
import gg.revival.factions.core.tools.ItemTools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class Mechanics {

    public static void onEnable() {
        loadListeners();
        loadCommands();
        loadRecipes();
    }

    public static void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new CrowbarListener(), FC.getFactionsCore());
        Bukkit.getPluginManager().registerEvents(new EmeraldEXPListener(), FC.getFactionsCore());
        Bukkit.getPluginManager().registerEvents(new EnderpearlCDListener(), FC.getFactionsCore());
        Bukkit.getPluginManager().registerEvents(new MobstackingListener(), FC.getFactionsCore());
        Bukkit.getPluginManager().registerEvents(new NetherPortalListener(), FC.getFactionsCore());
        Bukkit.getPluginManager().registerEvents(new BookUnchantmentListener(), FC.getFactionsCore());
        Bukkit.getPluginManager().registerEvents(new HighSpawnerListener(), FC.getFactionsCore());
        Bukkit.getPluginManager().registerEvents(new InvalidPearlListener(), FC.getFactionsCore());
        Bukkit.getPluginManager().registerEvents(new SpawnerBreakingListener(), FC.getFactionsCore());
        Bukkit.getPluginManager().registerEvents(new EndermiteListener(), FC.getFactionsCore());
        Bukkit.getPluginManager().registerEvents(new HardmodeListener(), FC.getFactionsCore());

        Mobstacker.run();
    }

    public static void loadCommands()
    {
        FC.getFactionsCore().getCommand("crowbar").setExecutor(new CrowbarCommand());
    }

    public static void loadRecipes() {
        if(Configuration.emeraldXpEnabled) {
            ItemStack expBottle = new ItemStack(Material.EXP_BOTTLE);
            ShapedRecipe expRecipe = new ShapedRecipe(expBottle);
            expRecipe.shape(new String[] { "*" } );
            expRecipe.setIngredient('*', Material.EMERALD);

            FC.getFactionsCore().getServer().addRecipe(expRecipe);
        }

        if(Configuration.settingsRemoveGApples) {
            ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE, 1, (short)1);
            ItemTools.deleteRecipe(gapple);
        }
    }

}
