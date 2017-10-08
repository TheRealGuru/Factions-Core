package gg.revival.factions.core.mechanics;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.mechanics.crowbars.Crowbar;
import gg.revival.factions.core.mechanics.crowbars.CrowbarCommand;
import gg.revival.factions.core.mechanics.crowbars.CrowbarListener;
import gg.revival.factions.core.mechanics.emeraldxp.EmeraldEXPListener;
import gg.revival.factions.core.mechanics.endermite.EndermiteListener;
import gg.revival.factions.core.mechanics.enderpearlcd.EnderpearlCDListener;
import gg.revival.factions.core.mechanics.enderpearlcd.EnderpearlCDTask;
import gg.revival.factions.core.mechanics.hardmode.HardmodeListener;
import gg.revival.factions.core.mechanics.highspawners.HighSpawnerListener;
import gg.revival.factions.core.mechanics.invalidpearl.InvalidPearlListener;
import gg.revival.factions.core.mechanics.mobstacking.Mobstacker;
import gg.revival.factions.core.mechanics.mobstacking.MobstackingListener;
import gg.revival.factions.core.mechanics.portalprotection.NetherPortalListener;
import gg.revival.factions.core.mechanics.spawnerbreaking.SpawnerBreakingListener;
import gg.revival.factions.core.mechanics.unenchantablebooks.BookUnenchantmentListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class Mechanics {

    @Getter private FC core;
    @Getter public Mobstacker mobstacker;
    @Getter public Crowbar crowbars;
    @Getter public EnderpearlCDTask enderpearlTask;

    public Mechanics(FC core) {
        this.core = core;
        this.mobstacker = new Mobstacker(core);
        this.crowbars = new Crowbar(core);
        this.enderpearlTask = new EnderpearlCDTask();

        onEnable();
    }

    public void onEnable() {
        loadListeners();
        loadCommands();
        loadRecipes();
    }

    public void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new CrowbarListener(core), core);
        Bukkit.getPluginManager().registerEvents(new EmeraldEXPListener(), core);
        Bukkit.getPluginManager().registerEvents(new EnderpearlCDListener(core), core);
        Bukkit.getPluginManager().registerEvents(new MobstackingListener(core), core);
        Bukkit.getPluginManager().registerEvents(new NetherPortalListener(core), core);
        Bukkit.getPluginManager().registerEvents(new BookUnenchantmentListener(core), core);
        Bukkit.getPluginManager().registerEvents(new HighSpawnerListener(core), core);
        Bukkit.getPluginManager().registerEvents(new InvalidPearlListener(core), core);
        Bukkit.getPluginManager().registerEvents(new SpawnerBreakingListener(core), core);
        Bukkit.getPluginManager().registerEvents(new EndermiteListener(core), core);
        Bukkit.getPluginManager().registerEvents(new HardmodeListener(core), core);

        mobstacker.run();
    }

    private void loadCommands() {
        core.getCommand("crowbar").setExecutor(new CrowbarCommand(core));
    }

    private void loadRecipes() {
        if(core.getConfiguration().emeraldXpEnabled) {
            ItemStack expBottle = new ItemStack(Material.EXP_BOTTLE);
            ShapedRecipe expRecipe = new ShapedRecipe(expBottle);
            expRecipe.shape(new String[] { "*" } );
            expRecipe.setIngredient('*', Material.EMERALD);

            core.getServer().addRecipe(expRecipe);
        }

        if(core.getConfiguration().settingsRemoveGApples) {
            ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE, 1, (short)1);
            core.getItemTools().deleteRecipe(gapple);
        }
    }

}
