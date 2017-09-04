package gg.revival.factions.core.mechanics;

import gg.revival.factions.core.FC;
import gg.revival.factions.core.mechanics.emeraldxp.EmeraldEXPListener;
import gg.revival.factions.core.tools.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class Mechanics
{

    public static void onEnable()
    {
        loadCommands();
        loadListeners();
        loadRecipes();
    }

    public static void onDisable()
    {

    }

    public static void loadCommands()
    {

    }

    public static void loadListeners()
    {
        if(Configuration.MECH_EMERALDXP_ENABLED)
            Bukkit.getPluginManager().registerEvents(new EmeraldEXPListener(), FC.getFactionsCore());
    }

    public static void loadRecipes()
    {
        if(Configuration.MECH_EMERALDXP_ENABLED)
        {
            ItemStack expBottle = new ItemStack(Material.EXP_BOTTLE);
            ShapedRecipe expRecipe = new ShapedRecipe(expBottle);
            expRecipe.shape(new String[] { "*" } );
            expRecipe.setIngredient('*', Material.EMERALD);

            FC.getFactionsCore().getServer().addRecipe(expRecipe);
        }
    }

}
